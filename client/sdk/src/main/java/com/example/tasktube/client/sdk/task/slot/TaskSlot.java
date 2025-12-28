package com.example.tasktube.client.sdk.task.slot;

import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public final class TaskSlot extends Slot<TaskSlot> {
    private String taskReference;

    public TaskSlot() {
        super(SlotType.TASK);
    }

    public String getTaskReference() {
        return taskReference;
    }

    public TaskSlot setTaskReference(final UUID taskId) {
        this.taskReference = taskId.toString();
        return this;
    }
}
