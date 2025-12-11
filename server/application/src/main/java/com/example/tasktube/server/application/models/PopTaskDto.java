package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @Nullable Map<String, Object> input
) {
    public static PopTaskDto from(final Task task) {
        return new PopTaskDto(task.getId(), task.getName(), task.getTube(), task.getInput());
    }
}
