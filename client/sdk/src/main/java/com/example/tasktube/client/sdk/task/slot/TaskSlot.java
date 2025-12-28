package com.example.tasktube.client.sdk.task.slot;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public final class TaskSlot extends Slot {
    private String taskReference;

    public TaskSlot() {
        super(SlotType.TASK);
    }

    public TaskSlot(@Nonnull final UUID taskReference) {
        this(SlotType.TASK, Objects.requireNonNull(taskReference.toString()));
    }

    public TaskSlot(@Nonnull final String taskReference) {
        this(SlotType.TASK, taskReference);
    }

    private TaskSlot(@Nonnull final SlotType type, @Nonnull final String taskReference) {
        super(type);
        this.taskReference = Objects.requireNonNull(taskReference);
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
