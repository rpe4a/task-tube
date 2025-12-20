package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.task.TaskSetting;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskRequest(
        UUID id,
        String name,
        String tube,
        Slot[] input,
        UUID[] waitTasks,
        Instant createdAt,
        TaskSetting settings
) {
}
