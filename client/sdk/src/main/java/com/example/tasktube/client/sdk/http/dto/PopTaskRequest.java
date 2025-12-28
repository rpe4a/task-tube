package com.example.tasktube.client.sdk.http.dto;

import jakarta.annotation.Nonnull;

public record PopTaskRequest(
        @Nonnull String client
) {
}
