package com.example.tasktube.server.application.models;

import com.example.tasktube.server.application.utils.SlotUtils;
import com.example.tasktube.server.domain.values.TaskSettings;
import com.example.tasktube.server.domain.values.slot.Slot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PushTaskDto(
        @Nonnull UUID id,
        @Nonnull String name,
        @Nonnull String tube,
        @Nonnull String correlationId,
        @Nullable List<Slot> input,
        @Nullable List<UUID> waitTasks,
        @Nonnull Instant createdAt,
        @Nullable TaskSettingsDto settings
) {

    public List<UUID> getWaitingTaskIdList() {
        final List<UUID> barrierTasks = new ArrayList<>();
        if (waitTasks() != null && !waitTasks().isEmpty()) {
            barrierTasks.addAll(waitTasks());
        }
        if (input() != null && !input().isEmpty()) {
            final List<UUID> taskSlots = SlotUtils.getTaskIdList(input());
            barrierTasks.addAll(taskSlots);
        }

        return barrierTasks;
    }

    public TaskSettings getSettings() {
        return Optional.ofNullable(settings())
                .map(TaskSettingsDto::to)
                .orElse(null);
    }
}
