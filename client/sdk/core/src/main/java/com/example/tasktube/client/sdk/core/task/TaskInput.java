package com.example.tasktube.client.sdk.core.task;

import com.example.tasktube.client.sdk.core.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.core.task.argument.Argument;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

public class TaskInput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private Argument[] arguments;
    private final TaskSettings settings;

    public TaskInput(
            @Nonnull final UUID id,
            @Nonnull final String name,
            @Nonnull final String tube,
            @Nonnull final String correlationId,
            @Nonnull final TaskSettings settings
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.tube = Objects.requireNonNull(tube);
        this.correlationId = Objects.requireNonNull(correlationId);
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
    public String getCorrelationId() {
        return correlationId;
    }

    @Nonnull
    public TaskSettings getSettings() {
        return settings;
    }

    @Nullable
    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(@Nullable final Argument[] arguments) {
        this.arguments = Objects.requireNonNull(arguments);
    }
}
