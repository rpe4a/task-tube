package com.example.tasktube.client.sdk.dto;

import jakarta.annotation.Nonnull;

public record PopTasksRequest(
        @Nonnull String client,
        @Nonnull int count
) {
}
