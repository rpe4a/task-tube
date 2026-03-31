package com.example.tasktube.server.ui.responses;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.domain.values.slot.Slot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TaskTubeTaskResponse(
        UUID id,
        String name,
        String tube,
        Task.Status status,
        String correlationId,
        UUID parentId,
        List<Slot> input,
        Slot output,
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
