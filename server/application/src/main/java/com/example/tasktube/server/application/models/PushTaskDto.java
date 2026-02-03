package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.slot.Slot;
import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record  PushTaskDto(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nullable List<Slot> input,
        @Nullable List<UUID> waitTasks,
        @Nonnull Instant createdAt,
        @Nullable TaskSettingsDto settings
) {
    public Task to(final boolean isRoot) {
        return new Task(
                id,
                name,
                tube,
                Task.Status.CREATED,
                correlationId,
                null,
                input,
                null,
                isRoot,
                Instant.now(),
                createdAt,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                Optional.ofNullable(settings)
                        .map(TaskSettingsDto::to)
                        .orElse(null),
                null,
                null
        );
    }
}
