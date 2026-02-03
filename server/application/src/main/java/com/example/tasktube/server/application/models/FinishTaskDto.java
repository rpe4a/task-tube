package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.slot.Slot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FinishTaskDto(
        @Nonnull UUID taskId,
        @Nullable List<PushTaskDto> children,
        @Nullable List<LogRecordDto> logs,
        @Nonnull Slot output,
        @Nonnull String client,
        @Nonnull Instant finishedAt
) {
}
