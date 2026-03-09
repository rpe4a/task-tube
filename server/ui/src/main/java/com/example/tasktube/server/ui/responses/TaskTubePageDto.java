package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TaskTubePageDto(
        UUID id,
        String name,
        String status,
        UUID parentId,
        Instant createdAt,
        Instant abortedAt,
        Instant completedAt
) {
}
