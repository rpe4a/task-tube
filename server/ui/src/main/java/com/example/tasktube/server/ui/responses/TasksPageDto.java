package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TasksPageDto(
        UUID id,
        String name,
        String tube,
        String status,
        String correlationId,
        Instant updatedAt,
        Instant createdAt,
        Instant abortedAt,
        Instant canceledAt,
        Instant completedAt,
        String handledBy,
        boolean isTerminationRequested,
        boolean isRecoveryRequested
) {
}
