package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.Slot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FinishTaskDto(
        @NonNull UUID taskId,
        @Nullable List<PushTaskDto> children,
        @NonNull Slot output,
        @NonNull String client,
        @NonNull Instant finishedAt
) {
}
