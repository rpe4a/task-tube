package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.slot.Slot;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskInput {
    private final UUID id;
    private final String name;
    private final String tube;
    private final String correlationId;
    private final List<Slot<?>> args;
    private final TaskSettings settings;

    public TaskInput(
            @Nonnull final UUID id,
            @Nonnull final String name,
            @Nonnull final String tube,
            @Nonnull final String correlationId,
            @Nonnull final List<Slot<?>> args,
            @Nonnull final TaskSettings settings
    ) {
        this.id = id;
        this.name = name;
        this.tube = tube;
        this.correlationId = correlationId;
        this.args = args;
        this.settings = settings;
    }

    public static @Nonnull TaskInput from(@Nonnull final PopTaskResponse response) {
        Preconditions.checkNotNull(response);

        return new TaskInput(
                response.id(),
                response.name(),
                response.tube(),
                response.correlationId(),
                Objects.isNull(response.args()) ? List.of() : response.args(),
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

    @Nullable
    public List<Slot<?>> getArgs() {
        return args;
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
