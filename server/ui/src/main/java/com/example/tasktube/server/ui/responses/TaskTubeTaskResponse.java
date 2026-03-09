package com.example.tasktube.server.ui.responses;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;

import java.time.Instant;
import java.util.UUID;

public record TaskTubeTaskResponse(
        UUID id,
        String name,
        String tube,
        Task.Status status,
        String correlationId,
        UUID parentId,
        String input,
        String output,
        Instant updatedAt,
        Instant createdAt,
        Instant canceledAt,
        Instant scheduledAt,
        Instant startedAt,
        Instant heartbeatAt,
        Instant finishedAt,
        Instant failedAt,
        Instant abortedAt,
        Instant completedAt,
        int failures,
        String failedReason,
        TaskSettings settings,
        String handledBy,
        int countChildren
) {
}
