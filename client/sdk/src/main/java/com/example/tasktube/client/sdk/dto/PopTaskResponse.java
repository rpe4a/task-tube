package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.task.TaskSettings;

import java.util.List;
import java.util.UUID;

public record PopTaskResponse(
        UUID id,
        String name,
        String tube,
        String correlationId,
        List<Slot> args,
        TaskSettings settings
) {
}
