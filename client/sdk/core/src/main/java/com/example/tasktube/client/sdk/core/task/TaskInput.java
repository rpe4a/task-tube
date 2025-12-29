package com.example.tasktube.client.sdk.core.task;

import com.example.tasktube.client.sdk.core.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.core.task.argument.Argument;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public class TaskInput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private final Argument[] arguments;
    private final TaskSettings settings;

    public TaskInput(
            @Nonnull final UUID id,
            @Nonnull final String name,
            @Nonnull final String tube,
            @Nonnull final String correlationId,
            @Nonnull final Argument[] arguments,
            @Nonnull final TaskSettings settings
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.tube = Objects.requireNonNull(tube);
        this.correlationId = Objects.requireNonNull(correlationId);
        this.arguments = Objects.requireNonNull(arguments);
        this.settings = Objects.requireNonNull(settings);
    }

    @Nonnull
    public static TaskInput from(@Nonnull final PopTaskResponse response) {
        Preconditions.checkNotNull(response);

        return new TaskInput(
                response.id(),
                response.name(),
                response.tube(),
                response.correlationId(),
                response.arguments(),
                response.settings()
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
    public Argument[] getArguments() {
        return arguments;
    }

    @Nonnull
    public String getCorrelationId() {
        return correlationId;
    }

    @Nonnull
    public TaskSettings getSettings() {
        return settings;
    }
}
