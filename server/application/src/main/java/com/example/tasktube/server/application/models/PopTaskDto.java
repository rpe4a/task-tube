package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;
import org.springframework.lang.NonNull;

import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull String correlationId,
        @NonNull TaskSettings settings
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
