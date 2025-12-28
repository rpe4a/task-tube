package com.example.tasktube.server.domain.values.slot;

import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.values.argument.Argument;

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

    @Override
    public Argument fill(final IArgumentFiller argumentFiller) {
        return argumentFiller.fill(this);
    }
}
