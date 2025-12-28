package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.task.slot.Slot;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class TaskOutput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private Slot result;
    private String failureMessage;
    private List<TaskRequest> children;

    private TaskOutput(
            @Nonnull final UUID id,
            @Nonnull final String name,
            @Nonnull final String tube,
            @Nonnull final String correlationId
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.correlationId = correlationId;
    }

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
    public Slot getResult() {
        return result;
    }

    public TaskOutput setResult(@Nonnull final Slot result) {
        Preconditions.checkNotNull(result);

        this.result = result;
        return this;
    }

    @Nullable
    public String getFailureMessage() {
        return failureMessage;
    }

    public TaskOutput setFailureMessage(@Nonnull final String failureMessage) {
        Preconditions.checkNotNull(failureMessage);

        this.failureMessage = failureMessage;
        return this;
    }

    @Nullable
    public List<TaskRequest> getChildren() {
        return List.copyOf(children);
    }

    public TaskOutput setChildren(final List<TaskRequest> children) {
        Preconditions.checkNotNull(children);

        this.children = List.copyOf(children);
        return this;
    }

}
