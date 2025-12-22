package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;

import java.time.Instant;
import java.util.List;

public record FinishTaskRequest(
        List<TaskRequest> children,
        Slot output,
        String client,
        Instant finishedAt
) {
}
