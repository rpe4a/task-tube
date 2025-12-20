package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Slot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @Nullable List<Slot> input
) {
    public static PopTaskDto from(final Task task) {
        return new PopTaskDto(task.getId(), task.getName(), task.getTube(), task.getInput());
    }
}
