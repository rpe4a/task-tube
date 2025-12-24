package com.example.tasktube.client.sdk.dto;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.task.TaskSettings;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record PopTaskResponse(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nullable List<Slot> args,
        @Nonnull TaskSettings settings
) {
}
