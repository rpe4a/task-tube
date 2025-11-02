package com.example.tasktube.server.api.requests;

import com.example.tasktube.server.application.models.TaskDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TaskRequest(
        UUID id,
        String name,
        String tube,
        Map<String, Object> input,
        List<UUID> waitTasks,
        Instant createdAt
) {
    public TaskDto to() {
        return new TaskDto(
                id,
                name,
                tube,
                input,
                waitTasks,
                createdAt
        );
    }

}
