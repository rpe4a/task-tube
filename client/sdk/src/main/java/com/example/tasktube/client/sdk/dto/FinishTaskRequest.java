package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;

public record FinishTaskRequest(
        @Nullable List<TaskRequest> children,
        @Nullable Slot result,
        @Nonnull String client,
        @Nonnull Instant finishedAt
) {
}
