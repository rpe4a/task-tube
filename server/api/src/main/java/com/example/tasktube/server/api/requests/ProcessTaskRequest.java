package com.example.tasktube.server.api.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ProcessTaskRequest(
        @NotBlank String client,
        @NotNull Instant processedAt
) {
}
