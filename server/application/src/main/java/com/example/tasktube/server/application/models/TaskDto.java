package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull Map<String, Object> input,
        @Nullable List<UUID> waitTasks,
        @NonNull Instant createdAt
) {

    public Task to(final boolean isRoot) {
        return new Task(
                id,
                name,
                tube,
                Task.Status.CREATED,
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
                TaskSettings.getDefault()
        );
    }
}
