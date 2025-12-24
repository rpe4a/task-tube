package com.example.tasktube.client.sdk.dto;

import jakarta.annotation.Nonnull;

import java.time.Instant;

public record ProcessTaskRequest(
        @Nonnull String client,
        @Nonnull Instant processedAt
) {
}
