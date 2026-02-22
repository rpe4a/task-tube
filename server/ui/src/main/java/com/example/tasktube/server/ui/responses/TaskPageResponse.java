package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TaskPageResponse(
        UUID id,
        String name,
        String tube,
        String status,
        Instant createdAt,
        Instant updatedAt,
        Instant abortedAt,
        Instant completedAt,
        String handledBy
) {
}
