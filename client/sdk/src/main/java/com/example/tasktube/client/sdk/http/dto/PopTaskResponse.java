package com.example.tasktube.client.sdk.http.dto;

import com.example.tasktube.client.sdk.task.TaskSettings;
import com.example.tasktube.client.sdk.task.argument.Argument;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record PopTaskResponse(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nullable List<Argument> arguments,
        @Nonnull TaskSettings settings
) {
}
