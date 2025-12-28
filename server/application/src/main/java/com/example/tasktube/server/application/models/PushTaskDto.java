package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.slot.Slot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PushTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull String correlationId,
        @Nullable List<Slot> input,
        @Nullable List<UUID> waitTasks,
        @NonNull Instant createdAt,
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
                null,
                null,
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
                null
        );
    }
}
