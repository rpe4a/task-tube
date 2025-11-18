package com.example.tasktube.server.api;

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
}
