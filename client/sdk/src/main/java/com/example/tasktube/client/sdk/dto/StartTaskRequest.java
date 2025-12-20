package com.example.tasktube.client.sdk.dto;

import java.time.Instant;

public record StartTaskRequest(
        String client,
        Instant startedAt
) {
}
