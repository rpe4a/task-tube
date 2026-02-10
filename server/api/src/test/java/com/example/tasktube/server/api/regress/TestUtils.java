package com.example.tasktube.server.api.regress;

import com.example.tasktube.server.application.models.FinishTaskDto;
import com.example.tasktube.server.application.models.PushTaskDto;
import com.example.tasktube.server.application.models.TaskSettingsDto;
import com.example.tasktube.server.domain.values.slot.ConstantSlot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static PushTaskDto createPushTaskDto() {
        return createPushTaskDto(null, null);
    }

    public static PushTaskDto createPushTaskDto(final TaskSettingsDto taskSettingsDto) {
        return createPushTaskDto(null, taskSettingsDto);
    }

    public static PushTaskDto createPushTaskDto(final List<UUID> waitFor) {
        return createPushTaskDto(waitFor, null);
    }

    public static PushTaskDto createPushTaskDto(final List<UUID> waitFor, final TaskSettingsDto taskSettingsDto) {
        return new PushTaskDto(
                UUID.randomUUID(),
                "task" + UUID.randomUUID(),
                "tube",
                UUID.randomUUID().toString(),
                new ArrayList<>(),
                waitFor,
                Instant.now(),
                taskSettingsDto
        );
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client) {
        return createFinishTaskDto(taskId, client, null);
    }

    public static FinishTaskDto createFinishTaskDto(final UUID taskId, final String client, final List<PushTaskDto> children) {
        return new FinishTaskDto(taskId, children, null, new ConstantSlot(), client, Instant.now());
    }

    public static void await(final long amount, final TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(amount));
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
