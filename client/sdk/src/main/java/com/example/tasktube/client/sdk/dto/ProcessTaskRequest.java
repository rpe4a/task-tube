package com.example.tasktube.client.sdk.dto;

import java.time.Instant;

public record ProcessTaskRequest(
        String client,
        Instant processedAt
) {
}
