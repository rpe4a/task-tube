package com.example.tasktube.server.ui.responses;

import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.values.slot.Slot;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record TaskPushRequest(
        @NotNull(message = "id is invalid") UUID id,
        @NotBlank(message = "name is invalid") String name,
        @NotBlank(message = "tube is invalid") String tube,
        @NotBlank(message = "correlationId is invalid") String correlationId,
        @Nullable Slot[] input,
        @Nullable UUID[] waitTasks,
        @NotNull(message = "createdAt is invalid") Instant createdAt,
        @Nullable TaskPushSettingRequest settings

) {
    public PushTaskDto to() {
        return new PushTaskDto(
                id,
                name,
                tube,
                correlationId,
                Objects.isNull(input) ? null : Arrays.asList(input),
                Objects.isNull(waitTasks) ? null : Arrays.asList(waitTasks),
                createdAt,
                Optional.ofNullable(settings)
                        .map(TaskPushSettingRequest::to)
                        .orElse(null)
        );
    }
}
