package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.PopTaskDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PopTaskResponse(
        @NotNull UUID id,
        @NotBlank String name,
        @NotBlank String tube,
        @Nullable Map<String, Object> input
) {
    public static PopTaskResponse from(final PopTaskDto task) {
        return new PopTaskResponse(task.id(), task.name(), task.tube(), task.input());
    }
}

