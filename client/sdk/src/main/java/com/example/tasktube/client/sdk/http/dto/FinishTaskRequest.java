package com.example.tasktube.client.sdk.http.dto;

import com.example.tasktube.client.sdk.task.slot.Slot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;

public record FinishTaskRequest(
        @Nullable List<TaskRequest> children,
        @Nonnull Slot output,
        @Nonnull String client,
        @Nonnull Instant finishedAt
) {
}
