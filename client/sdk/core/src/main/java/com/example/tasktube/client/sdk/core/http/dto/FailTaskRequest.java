package com.example.tasktube.client.sdk.core.http.dto;

import jakarta.annotation.Nonnull;

import java.time.Instant;

public record FailTaskRequest(
        @Nonnull String client,
        @Nonnull Instant failedAt,
        @Nonnull String failedReason
) {
}
