package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.slot.Slot;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskInput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private final List<Slot> args;
    private final TaskSettings settings;

    public TaskInput(
            final UUID id,
            final String name,
            final String tube,
            final String correlationId,
            final List<Slot> args,
            final TaskSettings settings
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.correlationId = correlationId;
        this.args = args;
        this.settings = settings;
    }

    public static TaskInput from(final PopTaskResponse response) {
        Objects.requireNonNull(response);

        return new TaskInput(
                response.id(),
                response.name(),
                response.tube(),
                response.correlationId(),
                response.args(),
                response.settings()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTube() {
        return tube;
    }

    public List<Slot> getArgs() {
        return args;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public TaskSettings getSettings() {
        return settings;
    }
}
