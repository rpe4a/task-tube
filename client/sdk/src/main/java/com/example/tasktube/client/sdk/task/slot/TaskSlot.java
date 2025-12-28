package com.example.tasktube.client.sdk.task.slot;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public final class TaskSlot extends Slot {
    private String taskReference;

    public TaskSlot() {
        super(SlotType.TASK);
    }

    @Nonnull
    public String getTaskReference() {
        return taskReference;
    }

    @Nonnull
    public TaskSlot setTaskReference(@Nonnull final UUID taskId) {
        this.taskReference = Objects.requireNonNull(taskId).toString();
        return this;
    }
}
