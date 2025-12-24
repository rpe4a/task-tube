package com.example.tasktube.client.sdk.dto;

import jakarta.annotation.Nonnull;

public record PopTaskRequest(
        @Nonnull String client
) {
}
