package com.example.tasktube.client.sdk.core.http.dto;

import com.example.tasktube.client.sdk.core.task.TaskSettings;
import com.example.tasktube.client.sdk.core.task.argument.Argument;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record PopTaskResponse(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nonnull Argument[] arguments,
        @Nonnull TaskSettings settings
) {
}
