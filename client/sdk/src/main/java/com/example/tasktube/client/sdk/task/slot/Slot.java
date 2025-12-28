package com.example.tasktube.client.sdk.task.slot;

import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import jakarta.annotation.Nonnull;

import java.util.Map;

public abstract sealed class Slot<T extends Slot<?>> permits ConstantSlot, ListSlot, TaskSlot {
    private final SlotType type;
    private Map<String, Object> metadata;

    protected Slot(final SlotType type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public T setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
        return (T) this;
    }

    public SlotType getType() {
        return type;
    }

    public enum SlotType {
        CONSTANT,
        TASK,
        LIST,
    }
}
