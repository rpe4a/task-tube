package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.PushTaskDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record TaskRequest(
        @NotNull(message = "id is invalid") UUID id,
        @NotBlank(message = "name is invalid") String name,
        @NotBlank(message = "tube is invalid") String tube,
        @Nullable Map<String, Object> input,
        @Nullable List<UUID> waitTasks,
        @NotNull(message = "createdAt is invalid") Instant createdAt,
        @Nullable TaskSettingRequest settings

) {
    public PushTaskDto to() {
        return new PushTaskDto(
                id,
                name,
                tube,
                input,
                waitTasks,
                createdAt,
                Optional.ofNullable(settings)
                        .map(TaskSettingRequest::to)
                        .orElse(null)
        );
    }

}
