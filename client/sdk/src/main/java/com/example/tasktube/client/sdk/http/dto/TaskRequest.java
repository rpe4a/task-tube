package com.example.tasktube.client.sdk.http.dto;

import com.example.tasktube.client.sdk.task.slot.Slot;
import com.example.tasktube.client.sdk.task.TaskSettings;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record TaskRequest(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nullable Slot[] input,
        @Nullable UUID[] waitTasks,
        @Nonnull Instant createdAt,
        @Nonnull TaskSettings settings
) {
}
