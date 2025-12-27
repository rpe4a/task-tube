package com.example.tasktube.server.domain.values.slot;

import java.util.Map;

public abstract sealed class Slot permits NothingSlot, ConstantSlot, TaskSlot, SlotList {
    private final SlotType type;
    private Map<String, Object> metadata;

    public Slot() {
        type = null;
    }

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

    public enum SlotType {
        NOTHING,
        CONSTANT,
        TASK,
        LIST
    }
}
