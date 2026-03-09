package com.example.tasktube.server.application.queries;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record TaskTubeTreeNodeQuery(
        @Nullable String correlationId,
        @Nullable UUID taskId
) {
}
