package com.example.tasktube.client.sdk.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record FinishTaskRequest(
        List<TaskRequest> children,
        Map<String, Object> output,
        String client,
        Instant finishedAt
) {
}
