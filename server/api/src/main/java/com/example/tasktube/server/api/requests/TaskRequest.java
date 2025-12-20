package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.values.Slot;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record TaskRequest(
        @NotNull(message = "id is invalid") UUID id,
        @NotBlank(message = "name is invalid") String name,
        @NotBlank(message = "tube is invalid") String tube,
        @Nullable Slot[] input,
        @Nullable UUID[] waitTasks,
        @NotNull(message = "createdAt is invalid") Instant createdAt,
        @Nullable TaskSettingRequest settings

) {
    public PushTaskDto to() {
        return new PushTaskDto(
                id,
                name,
                tube,
                Objects.isNull(input) ? null : Arrays.asList(input),
                Objects.isNull(waitTasks) ? null : Arrays.asList(waitTasks),
                createdAt,
                Optional.ofNullable(settings)
                        .map(TaskSettingRequest::to)
                        .orElse(null)
        );
    }

}
