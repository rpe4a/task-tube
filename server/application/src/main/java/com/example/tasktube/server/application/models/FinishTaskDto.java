package com.example.tasktube.server.application.models;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FinishTaskDto(
        @NonNull UUID taskId,
        @Nullable List<TaskDto> children,
        @NonNull Map<String, Object> output,
        @NonNull String client,
        @NonNull Instant finishedAt
) {
}
