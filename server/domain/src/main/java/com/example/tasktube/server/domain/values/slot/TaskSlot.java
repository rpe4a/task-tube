package com.example.tasktube.server.domain.values.slot;

import java.util.UUID;

public final class TaskSlot extends Slot {
    private UUID taskReference;

    public TaskSlot() {
        super(SlotType.TASK);
    }

    public UUID getTaskReference() {
        return taskReference;
    }

    public Slot setTaskReference(final UUID taskId) {
        this.taskReference = taskId;
        return this;
    }
}
