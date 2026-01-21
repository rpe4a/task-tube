package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.TaskSettings;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PopTaskResponse(
        @NotNull UUID id,
        @NotBlank String name,
        @NotBlank String tube,
        @NotBlank String correlationId,
        @NotNull TaskSettings settings
) {
    public static PopTaskResponse from(final PopTaskDto task) {
        return new PopTaskResponse(task.id(), task.name(), task.tube(), task.correlationId(), task.settings());
    }
}

