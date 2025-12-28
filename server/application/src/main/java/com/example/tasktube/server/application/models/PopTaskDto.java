package com.example.tasktube.server.application.models;

import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.domain.values.argument.Argument;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public record PopTaskDto(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull String tube,
        @NonNull String correlationId,
        @NonNull List<Argument> arguments,
        @NonNull TaskSettings settings
) {
}
