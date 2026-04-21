package com.example.tasktube.server.application.queries;

import com.example.tasktube.server.domain.enties.Task;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record ParentTasksQuery(
        @Nullable UUID taskId,
        @Nullable String taskName,
        @Nullable String tube,
        @Nullable Task.Status status,
        @Nullable Instant createdFrom,
        @Nullable Instant createdTo,
        @Nullable String sort,
        @Nullable String by,
        int page,
        int size
) {
}
