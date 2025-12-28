package com.example.tasktube.server.domain.values.slot;

import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.values.argument.Argument;

import java.util.Map;

public abstract sealed class Slot permits ConstantSlot, TaskSlot, ListSlot {
    private final SlotType type;
    private Map<String, Object> metadata;

    protected Slot(final SlotType type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Slot setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public SlotType getType() {
        return type;
    }

    public abstract Argument fill(final IArgumentFiller argumentFiller);

    public enum SlotType {
        CONSTANT,
        TASK,
        LIST
    }
}
