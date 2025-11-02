package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.google.common.base.Preconditions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FinishTaskRequest(
        @Nullable List<TaskRequest> children,
        @Nullable Map<String, Object> output,
        @Nullable String client,
        @Nullable Instant finishedAt
) {
    public FinishTaskDto to(final UUID taskId) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(output);
        Preconditions.checkNotNull(client);

        final List<TaskDto> tasks = children == null ? null : children()
                .stream()
                .map(TaskRequest::to)
                .toList();

        return new FinishTaskDto(
                taskId,
                tasks,
                output,
                client,
                finishedAt == null ? Instant.now() : finishedAt
        );
    }
}
