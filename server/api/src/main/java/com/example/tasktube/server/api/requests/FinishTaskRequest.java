package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.LogRecordDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FinishTaskRequest(
        @Nullable List<TaskRequest> children,
        @Nullable List<LogRequest> logs,
        @NotNull Slot output,
        @NotBlank String client,
        @NotNull Instant finishedAt
) {
    public FinishTaskDto to(final UUID taskId) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(client);

        final List<PushTaskDto> tasks =
                children == null
                        ? null
                        : children().stream().map(TaskRequest::to).toList();

        final List<LogRecordDto> logRecords =
                logs == null
                        ? null
                        : logs().stream().map(LogRequest::to).toList();


        return new FinishTaskDto(
                taskId,
                tasks,
                logRecords,
                output,
                client,
                finishedAt == null ? Instant.now() : finishedAt
        );
    }
}
