package com.example.tasktube.client.sdk.dto;

import java.time.Instant;

public record FailTaskRequest(
        String client,
        Instant failedAt,
        String failedReason
) {
}
