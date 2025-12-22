package com.example.tasktube.client.sdk.task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Task<TResult> {
    private final List<TaskRecord<?>> children = new LinkedList<>();

    public List<TaskRecord<?>> getChildren() {
        return children;
    }

    public String getName() {
        return getClass().getCanonicalName();
    }

    public final Nothing nothing() {
        return new Nothing();
    }

    public final <V> Constant<V> constant(final V value) {
        return new Constant<>(value);
    }

    public final <V> ValueList<V> list(final List<Value<V>> values) {
        return new ValueList<>(values);
    }

    @SafeVarargs
    public final <V> ValueList<V> list(final Value<V>... values) {
        return new ValueList<>(Arrays.asList(values));
    }

    public final <R> TaskResult<R> pushIn(final Task0<R> task, final TaskConfiguration... configurations) {
        return addChild(task, configurations).getResult();
    }

    public final <R, A0> TaskResult<R> pushIn(final Task1<R, A0> task, final Value<A0> value, final TaskConfiguration... configurations) {
        final TaskRecord<R> child = addChild(task, configurations);
        child.addArg(value);
        return child.getResult();
    }

    public final <R, A0, A1> TaskResult<R> pushIn(final Task2<R, A0, A1> task, final Value<A0> value0, final Value<A1> value1, final TaskConfiguration... configurations) {
        final TaskRecord<R> child = addChild(task, configurations);
        child.addArg(value0);
        child.addArg(value1);
        return child.getResult();
    }

    public final TaskConfiguration waitFor(final TaskResult<Integer> taskResult) {
        return new TaskConfiguration.WaitForTask(taskResult);
    }

    public final TaskConfigurationInternal configure() {
        return new TaskConfigurationInternal();
    }

    private <R> TaskRecord<R> addChild(final Task<R> task, final TaskConfiguration[] configurations) {
        final TaskRecord<R> child = task.attachTo(this);

        if (configurations.length > 0) {
            Arrays.stream(configurations)
                    .forEach(c -> c.applyTo(child));
        }

        return child;
    }

    private TaskRecord<TResult> attachTo(final Task<?> parent) {
        final TaskRecord<TResult> child = new TaskRecord.Builder<TResult>()
                .setName(getName())
                .setParent(parent.record.getId())
                .setTube(parent.record.getTube())
                .build();

        parent.appendChild(child);

        return child;
    }

    private void appendChild(final TaskRecord<?> child) {
        children.add(child);
    }

    public TaskOutput run(final TaskInput input) {
        return null; //TODO
    }

    public class TaskConfigurationInternal {
        public TaskConfiguration.MaxCountOfFailures maxCountOfFailures(final int value) {
            return new TaskConfiguration.MaxCountOfFailures(value);
        }

        public TaskConfiguration.FailureRetryTimeoutSeconds failureRetryTimeoutSeconds(final int value) {
            return new TaskConfiguration.FailureRetryTimeoutSeconds(value);
        }
    }
}
