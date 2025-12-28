package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.TaskSettings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull String correlationId,
        @Nullable List<Argument> arguments,
        @NonNull TaskSettings settings
) {
}
