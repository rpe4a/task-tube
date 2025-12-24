package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotArgumentDeserializer;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class Task<TResult> {
    private static final String CANNOT_FIND_METHOD_RUN = "Cannot find method run.";
    private static final String DEFAULT_METHOD_NAME = "run";

    private final List<TaskRecord<?>> children = new LinkedList<>();

    private UUID id;
    private String tube;
    private String correlationId;
    private TaskSettings settings;

    private SlotArgumentDeserializer slotDeserializer;
    private SlotValueSerializer slotSerializer;

    @Nonnull
    public UUID getId() {
        return id;
    }

    private void setId(final UUID id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return getClass().getCanonicalName();
    }

    @Nonnull
    public TaskSettings getSettings() {
        return settings;
    }

    private void setSettings(final TaskSettings settings) {
        this.settings = settings;
    }

    @Nonnull
    public String getTube() {
        return tube;
    }

    private void setTube(final String tube) {
        this.tube = tube;
    }

    @Nonnull
    public String getCorrelationId() {
        return correlationId;
    }

    private void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    private void setSlotDeserializer(final SlotArgumentDeserializer slotDeserializer) {
        this.slotDeserializer = slotDeserializer;
    }

    private void setSlotValueSerializer(final SlotValueSerializer slotValueSerializer) {
        this.slotSerializer = slotValueSerializer;
    }

    @Nonnull
    public final <V> Nothing<V> nothing() {
        return new Nothing<>();
    }

    @Nonnull
    public final <V> Constant<V> constant(@Nonnull final V value) {
        Preconditions.checkNotNull(value);

        return new Constant<>(value);
    }

    @Nonnull
    public final <V> Constant<V> constant(@Nonnull final V value, @Nonnull final TypeReference<V> typeReference) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(typeReference);

        return new Constant<>(value, typeReference);
    }

    @Nonnull
    public final <V> ValueList<V> list(@Nonnull final List<Value<V>> values) {
        Preconditions.checkNotNull(values);

        return new ValueList<>(values);
    }

    @SafeVarargs
    @Nonnull
    public final <V> ValueList<V> list(@Nonnull final Value<V>... values) {
        Preconditions.checkNotNull(values);

        return new ValueList<>(Arrays.asList(values));
    }

    @Nonnull
    public final <R> TaskResult<R> pushIn(
            @Nonnull final Task0<R> task,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(configurations);

        return addChild(task, configurations).getResult();
    }

    @Nonnull
    public final <R, A0> TaskResult<R> pushIn(
            @Nonnull final Task1<R, A0> task,
            @Nonnull final Value<A0> value,
            final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArg(value);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1> TaskResult<R> pushIn(
            @Nonnull final Task2<R, A0, A1> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArg(value0);
        child.addArg(value1);
        return child.getResult();
    }

    @Nonnull
    public final TaskConfiguration waitFor(@Nonnull final TaskResult<Integer> taskResult) {
        Preconditions.checkNotNull(taskResult);

        return new TaskConfiguration.WaitForTask(taskResult);
    }

    @Nonnull
    public final TaskConfigurationInternal configure() {
        return new TaskConfigurationInternal();
    }

    private <R> TaskRecord<R> addChild(final Task<R> task, final TaskConfiguration[] configurations) {
        final TaskRecord<R> child = task.attachTo(this);

        for (final TaskConfiguration configuration : configurations) {
            configuration.applyTo(child);
        }

        return child;
    }

    private TaskRecord<TResult> attachTo(final Task<?> parent) {
        final TaskRecord<TResult> child = new TaskRecord.Builder<TResult>()
                .setName(getName())
                .setParent(parent.getId())
                .setTube(parent.getTube())
                .setCorrelationId(parent.getCorrelationId())
                .build();

        parent.appendChild(child);

        return child;
    }

    private void appendChild(final TaskRecord<?> child) {
        children.add(child);
    }

    void execute(
            @Nonnull final TaskInput input,
            @Nonnull final TaskOutput output,
            @Nonnull final SlotArgumentDeserializer slotDeserializer,
            @Nonnull final SlotValueSerializer slotValueSerializer
    ) {
        Preconditions.checkNotNull(slotDeserializer);
        Preconditions.checkNotNull(slotValueSerializer);
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(output);
        Preconditions.checkArgument(input.getName().equals(getName()));

        setSlotDeserializer(slotDeserializer);
        setSlotValueSerializer(slotValueSerializer);

        setId(input.getId());
        setTube(input.getTube());
        setCorrelationId(input.getCorrelationId());
        setSettings(input.getSettings());

        final Value<TResult> result = executeRunMethod(input.getArgs()).orElseGet(this::nothing);

        output.setResult(result.serialize(slotSerializer));
        output.setChildren(
                children.stream()
                        .map(taskRecord -> taskRecord.toRequest(slotSerializer))
                        .toList()
        );
    }

    private Optional<Value<TResult>> executeRunMethod(final List<Slot> slots) {
        try {
            final Method run = getRunMethod();
            final Parameter[] parameters = run.getParameters();
            final Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = slots.get(i).deserialize(slotDeserializer);
            }
            return Optional.ofNullable((Value<TResult>) run.invoke(this, args));
        } catch (final InvocationTargetException | IllegalAccessException e) {
            final Throwable innerException = Objects.nonNull(e.getCause()) ? e.getCause() : e;
            throw new RunnableMethodException(innerException);
        }
    }

    private Method getRunMethod() {
        final Method[] methods = Objects.requireNonNull(this.getClass()).getMethods();
        for (final Method method : methods) {
            if (DEFAULT_METHOD_NAME.equals(method.getName())) {
                return method;
            }
        }
        throw new IllegalArgumentException(CANNOT_FIND_METHOD_RUN);
    }

    public static class TaskConfigurationInternal {

        @Nonnull
        public TaskConfiguration.MaxCountOfFailures maxCountOfFailures(final int value) {
            return new TaskConfiguration.MaxCountOfFailures(value);
        }

        @Nonnull
        public TaskConfiguration.FailureRetryTimeoutSeconds failureRetryTimeoutSeconds(final int value) {
            return new TaskConfiguration.FailureRetryTimeoutSeconds(value);
        }

        @Nonnull
        public TaskConfiguration.TimeoutSeconds timeoutSeconds(final int value) {
            return new TaskConfiguration.TimeoutSeconds(value);
        }

        @Nonnull
        public TaskConfiguration.HeartbeatTimeoutSeconds heartbeatTimeoutSeconds(final int value) {
            return new TaskConfiguration.HeartbeatTimeoutSeconds(value);
        }
    }

    public static class Executor {
        public static final String EXECUTE_METHOD_NAME = "execute";
        private final Task<?> task;

        public Executor(final Task<?> task) {
            this.task = task;
        }

        public void invoke(
                final TaskInput input,
                final TaskOutput output,
                final SlotArgumentDeserializer slotDeserializer,
                final SlotValueSerializer slotSerializer
        ) {
            try {
                final Method execute = Objects.requireNonNull(task.getClass())
                        .getMethod(EXECUTE_METHOD_NAME, TaskInput.class, TaskOutput.class, SlotArgumentDeserializer.class, SlotValueSerializer.class);

                execute.invoke(task, input, output, slotDeserializer, slotSerializer);
            } catch (final NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (final InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
