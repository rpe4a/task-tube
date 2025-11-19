package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.TaskDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestUtils {

    public static TaskDto createTaskDto() {
        return createTaskDto(null);
    }

    public static TaskDto createTaskDto(final List<UUID> waitFor) {
        return new TaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube" + UUID.randomUUID(),
                Map.of("key", "value"),
                waitFor,
                Instant.now()
        );
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client) {
        return createFinishTaskDto(taskId, client, null);
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client, final List<TaskDto> children) {
        return new FinishTaskDto(taskId, children, Map.of("key", "value"), client, Instant.now());
    }
}
