package com.example.tasktube.server.api;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.TaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
import org.apache.commons.lang3.ThreadUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static TaskDto createTaskDto() {
        return createTaskDto(null, null);
    }

    public static TaskDto createTaskDto(final TaskSettingsDto taskSettingsDto) {
        return createTaskDto(null, taskSettingsDto);
    }

    public static TaskDto createTaskDto(final List<UUID> waitFor) {
        return createTaskDto(waitFor, null);
    }

    public static TaskDto createTaskDto(final List<UUID> waitFor, final TaskSettingsDto taskSettingsDto) {
        return new TaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube" + UUID.randomUUID(),
                Map.of("key", "value"),
                waitFor,
                Instant.now(),
                taskSettingsDto
        );
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client) {
        return createFinishTaskDto(taskId, client, null);
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client, final List<TaskDto> children) {
        return new FinishTaskDto(taskId, children, Map.of("key", "value"), client, Instant.now());
    }

    public static void await(final long amount, final TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(amount));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
