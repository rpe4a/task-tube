package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.domain.values.TaskSettings;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record TaskRequest(
        UUID id,
        String name,
        String tube,
        Map<String, Object> input,
        List<UUID> waitTasks,
        Instant createdAt,
        TaskSettingRequest settings

) {
    public TaskDto to() {
        return new TaskDto(
                id,
                name,
                tube,
                input,
                waitTasks,
                createdAt,
                Optional.ofNullable(settings)
                        .map(TaskSettingRequest::to)
                        .orElse(null)
        );
    }

}
