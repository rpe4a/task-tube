package com.example.tasktube.server.api.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record FailTaskRequest(
        @NotBlank String client,
        @NotNull Instant failedAt,
        @NotBlank String failedReason
) {
}
