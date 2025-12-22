package com.example.tasktube.client.sdk.task;

import java.util.UUID;

public class TaskOutput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private Value<?> result;
    private String failureMessage;

    public TaskOutput(
            final UUID id,
            final String name,
            final String tube,
            final String correlationId
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.correlationId = correlationId;
    }

    public static TaskOutput createInstance(final TaskInput input) {
        return new TaskOutput(
                input.getId(),
                input.getName(),
                input.getTube(),
                input.getCorrelationId()
        );
    }

    public TaskOutput setFailureMessage(final String failureMessage) {
        this.failureMessage = failureMessage;
        return this;
    }
}
