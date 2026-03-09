package com.example.tasktube.server.ui.responses;

import java.time.Instant;
import java.util.UUID;

public record TaskTubeTreeNode(
        UUID id,
        String name,
        String status,
        UUID parentId,
        Instant createdAt,
        Instant scheduledAt,
        Instant startedAt,
        Instant finishedAt,
        Instant abortedAt,
        Instant canceledAt,
        Instant completedAt,
        int childrenCount
) {
}
