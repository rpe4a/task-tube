package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record PopTaskDto(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nonnull TaskSettings settings
) {

    public static PopTaskDto from(final Task task) {
        return new PopTaskDto(
                task.getId(),
                task.getName(),
                task.getTube(),
                task.getCorrelationId(),
                task.getSettings()
        );
    }
}
