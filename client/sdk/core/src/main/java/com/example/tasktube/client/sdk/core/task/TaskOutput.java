package com.example.tasktube.client.sdk.core.task;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskOutput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private Value<?> result;
    private String failureMessage;
    private TaskRecord<?>[] children;

    private TaskOutput(
            @Nonnull final UUID id,
            @Nonnull final String name,
            @Nonnull final String tube,
            @Nonnull final String correlationId
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.tube = Objects.requireNonNull(tube);
        this.correlationId = Objects.requireNonNull(correlationId);
        this.result = new Constant<>(null, Object.class);
    }

    @Nonnull
    public static TaskOutput createInstance(@Nonnull final TaskInput input) {
        Preconditions.checkNotNull(input);

        return new TaskOutput(
                input.getId(),
                input.getName(),
                input.getTube(),
                input.getCorrelationId()
        );
    }

    @Nonnull
    public UUID getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getTube() {
        return tube;
    }

    @Nonnull
    public String getCorrelationId() {
        return correlationId;
    }

    @Nonnull
    public Value<?> getResult() {
        return result;
    }

    @Nonnull
    public TaskOutput setResult(@Nonnull final Value<?> result) {
        this.result = Objects.requireNonNull(result);
        return this;
    }

    @Nullable
    public String getFailureMessage() {
        return failureMessage;
    }

    @Nonnull
    public TaskOutput setFailureMessage(@Nonnull final String failureMessage) {
        this.failureMessage = Objects.requireNonNull(failureMessage);
        return this;
    }

    @Nullable
    public TaskRecord<?>[] getChildren() {
        return children;
    }

    @Nonnull
    public TaskOutput setChildren(@Nonnull final List<TaskRecord<?>> children) {
        this.children = Objects.requireNonNull(children).toArray(new TaskRecord<?>[0]);
        return this;
    }

}
