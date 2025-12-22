package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Slot;
import com.example.tasktube.server.domain.values.TaskSettings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull String correlationId,
        @Nullable List<Slot> args,
        @NonNull TaskSettings settings
) {
    public static PopTaskDto from(final Task task) {
        return new PopTaskDto(task.getId(), task.getName(), task.getTube(), task.getCorrelationId(), task.getInput(), task.getSettings());
    }
}
