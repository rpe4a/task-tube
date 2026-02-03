package com.example.tasktube.client.sdk.core.task;

import com.example.tasktube.client.sdk.core.task.argument.Argument;
import com.example.tasktube.client.sdk.core.task.argument.ArgumentDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract sealed class Task<TResult> permits Task0, Task1, Task2, Task3, Task4, Task5, Task6, Task7 {

    private static final String CANNOT_FIND_METHOD_RUN = "Cannot find method run.";
    private static final String DEFAULT_METHOD_NAME = "run";

    private final List<TaskRecord<?>> children = new LinkedList<>();
    private final List<LogRecord> logs = new LinkedList<>();

    private TaskLoggerWrapper loggerWrapper;

    private UUID id;
    private String tube;
    private String correlationId;
    private TaskSettings settings;
    private ArgumentDeserializer argumentDeserializer;

    @Nonnull
    public UUID getId() {
        return id;
    }

    private void setId(@Nonnull final UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nonnull
    public String getName() {
        return getClass().getCanonicalName();
    }

    @Nonnull
    public TaskSettings getSettings() {
        return settings;
    }

    private void setSettings(@Nonnull final TaskSettings settings) {
        this.settings = Objects.requireNonNull(settings);
    }

    @Nonnull
    public String getTube() {
        return tube;
    }

    private void setTube(@Nonnull final String tube) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tube));
        this.tube = tube;
    }

    @Nonnull
    public String getCorrelationId() {
        return correlationId;
    }

    private void setCorrelationId(@Nonnull final String correlationId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tube));
        this.correlationId = correlationId;
    }

    private void setArgumentDeserializer(@Nonnull final ArgumentDeserializer argumentDeserializer) {
        this.argumentDeserializer = Objects.requireNonNull(argumentDeserializer);
    }

    @Nonnull
    public final <V> Constant<V> nothing() {
        return new Constant<>(null, Object.class);
    }

    @Nonnull
    public final <V> Constant<V> constant(@Nonnull final V value) {
        return new Constant<>(Objects.requireNonNull(value));
    }

    @Nonnull
    public final <V> Constant<V> constant(@Nonnull final V value, @Nonnull final TypeReference<V> typeReference) {
        return new Constant<>(Objects.requireNonNull(value), Objects.requireNonNull(typeReference));
    }

    @Nonnull
    public final <V> ListValue<V> list(@Nonnull final List<Value<V>> values) {
        return new ListValue<>(Objects.requireNonNull(values));
    }

    @SafeVarargs
    @Nonnull
    public final <V> ListValue<V> list(@Nonnull final Value<V>... values) {
        return new ListValue<>(Arrays.asList(Objects.requireNonNull(values)));
    }

    @Nonnull
    public final TaskConfiguration waitFor(@Nonnull final TaskResult<Integer> taskResult) {
        return new TaskConfiguration.WaitForTask(Objects.requireNonNull(taskResult));
    }

    @Nonnull
    public final TaskConfigurationInternal configure() {
        return new TaskConfigurationInternal();
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
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value);
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
        child.addArgument(value0);
        child.addArgument(value1);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1, A2> TaskResult<R> pushIn(
            @Nonnull final Task3<R, A0, A1, A2> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final Value<A2> value2,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(value2);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value0);
        child.addArgument(value1);
        child.addArgument(value2);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1, A2, A3> TaskResult<R> pushIn(
            @Nonnull final Task4<R, A0, A1, A2, A3> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final Value<A2> value2,
            @Nonnull final Value<A3> value3,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(value2);
        Preconditions.checkNotNull(value3);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value0);
        child.addArgument(value1);
        child.addArgument(value2);
        child.addArgument(value3);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1, A2, A3, A4> TaskResult<R> pushIn(
            @Nonnull final Task5<R, A0, A1, A2, A3, A4> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final Value<A2> value2,
            @Nonnull final Value<A3> value3,
            @Nonnull final Value<A4> value4,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(value2);
        Preconditions.checkNotNull(value3);
        Preconditions.checkNotNull(value4);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value0);
        child.addArgument(value1);
        child.addArgument(value2);
        child.addArgument(value3);
        child.addArgument(value4);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1, A2, A3, A4, A5> TaskResult<R> pushIn(
            @Nonnull final Task6<R, A0, A1, A2, A3, A4, A5> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final Value<A2> value2,
            @Nonnull final Value<A3> value3,
            @Nonnull final Value<A4> value4,
            @Nonnull final Value<A5> value5,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(value2);
        Preconditions.checkNotNull(value3);
        Preconditions.checkNotNull(value4);
        Preconditions.checkNotNull(value5);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value0);
        child.addArgument(value1);
        child.addArgument(value2);
        child.addArgument(value3);
        child.addArgument(value4);
        child.addArgument(value5);
        return child.getResult();
    }

    @Nonnull
    public final <R, A0, A1, A2, A3, A4, A5, A6> TaskResult<R> pushIn(
            @Nonnull final Task7<R, A0, A1, A2, A3, A4, A5, A6> task,
            @Nonnull final Value<A0> value0,
            @Nonnull final Value<A1> value1,
            @Nonnull final Value<A2> value2,
            @Nonnull final Value<A3> value3,
            @Nonnull final Value<A4> value4,
            @Nonnull final Value<A5> value5,
            @Nonnull final Value<A6> value6,
            @Nonnull final TaskConfiguration... configurations
    ) {
        Preconditions.checkNotNull(task);
        Preconditions.checkNotNull(value0);
        Preconditions.checkNotNull(value1);
        Preconditions.checkNotNull(value2);
        Preconditions.checkNotNull(value3);
        Preconditions.checkNotNull(value4);
        Preconditions.checkNotNull(value5);
        Preconditions.checkNotNull(value6);
        Preconditions.checkNotNull(configurations);

        final TaskRecord<R> child = addChild(task, configurations);
        child.addArgument(value0);
        child.addArgument(value1);
        child.addArgument(value2);
        child.addArgument(value3);
        child.addArgument(value4);
        child.addArgument(value5);
        child.addArgument(value6);
        return child.getResult();
    }

    @Nonnull
    public TaskLogger logger() {
        TaskLoggerWrapper wrapper = loggerWrapper;
        if (wrapper == null) {
            synchronized (this) {
                if (loggerWrapper == null) {
                    loggerWrapper = new TaskLoggerWrapper(new TaskLogger(logs));
                }
                wrapper = loggerWrapper;
            }
        }
        return wrapper.logger;
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

    private void execute(
            @Nonnull final TaskInput input,
            @Nonnull final TaskOutput output,
            @Nonnull final ArgumentDeserializer slotDeserializer
    ) {
        Preconditions.checkNotNull(slotDeserializer);
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(output);
        Preconditions.checkArgument(input.getName().equals(getName()));

        setArgumentDeserializer(slotDeserializer);

        setId(input.getId());
        setTube(input.getTube());
        setCorrelationId(input.getCorrelationId());
        setSettings(input.getSettings());

        final Value<TResult> result = executeRunMethod(input.getArguments()).orElseGet(this::nothing);

        output.setResult(result);
        output.setChildren(children);
        output.setLogs(logs);
    }

    private Optional<Value<TResult>> executeRunMethod(final Argument[] arguments) {
        try {
            final Method run = getRunMethod();
            final Parameter[] parameters = run.getParameters();
            final Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = arguments[i].deserialize(argumentDeserializer);
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

        public Executor(@Nonnull final Task<?> task) {
            this.task = Objects.requireNonNull(task);
        }

        public void invoke(
                @Nonnull final TaskInput input,
                @Nonnull final TaskOutput output,
                @Nonnull final ArgumentDeserializer argumentDeserializer
        ) {
            Preconditions.checkNotNull(input);
            Preconditions.checkNotNull(output);
            Preconditions.checkNotNull(argumentDeserializer);

            try {
                final Method execute = findExecuteMethod(task.getClass());

                execute.setAccessible(true);

                execute.invoke(task, input, output, argumentDeserializer);
            } catch (final InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private Method findExecuteMethod(final Class<?> taskClazz) {
            try {
                return taskClazz
                        .getSuperclass()
                        .getDeclaredMethod(EXECUTE_METHOD_NAME, TaskInput.class, TaskOutput.class, ArgumentDeserializer.class);
            } catch (final NoSuchMethodException e) {
                return findExecuteMethod(taskClazz.getSuperclass());
            }
        }
    }

    private static class TaskLoggerWrapper {

        public final TaskLogger logger;

        public TaskLoggerWrapper(final TaskLogger logger) {
            this.logger = logger;
        }
    }
}
