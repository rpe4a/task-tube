package com.example.tasktube.client.sdk.http.dto;

import com.example.tasktube.client.sdk.task.slot.Slot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;

public record FinishTaskRequest(
        @Nullable TaskRequest[] children,
        @Nonnull Slot output,
        @Nonnull String client,
        @Nonnull Instant finishedAt
) {
}
