package com.example.tasktube.server.application.queries;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record TaskTubeTaskQuery(
        @Nullable String correlationId,
        @Nullable UUID taskId
) {
}
