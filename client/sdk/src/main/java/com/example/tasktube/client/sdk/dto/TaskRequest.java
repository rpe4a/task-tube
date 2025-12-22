package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.task.TaskSettings;

import java.time.Instant;
import java.util.UUID;

public record TaskRequest(
        UUID id,
        String name,
        String tube,
        Slot[] input,
        UUID[] waitTasks,
        Instant createdAt,
        TaskSettings settings
) {
}
