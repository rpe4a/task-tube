package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public class TaskOutput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private Slot result;
    private String failureMessage;

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

    public TaskOutput setFailureMessage(@Nonnull final String failureMessage) {
        Preconditions.checkNotNull(failureMessage);

        this.failureMessage = failureMessage;
        return this;
    }

    public TaskOutput setResult(@Nonnull final Slot result) {
        Preconditions.checkNotNull(failureMessage);

        this.result = result;
        return this;
    }
}
