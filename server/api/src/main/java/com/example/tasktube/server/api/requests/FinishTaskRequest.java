package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.google.common.base.Preconditions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FinishTaskRequest(
        @Nullable List<TaskRequest> children,
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

        return new FinishTaskDto(
                taskId,
                tasks,
                output,
                client,
                finishedAt == null ? Instant.now() : finishedAt
        );
    }
}
