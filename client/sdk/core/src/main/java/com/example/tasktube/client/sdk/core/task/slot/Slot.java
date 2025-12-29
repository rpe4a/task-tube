package com.example.tasktube.client.sdk.core.task.slot;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract sealed class Slot permits ConstantSlot, ListSlot, TaskSlot {
    private final SlotType type;
    private Map<String, Object> metadata;

    protected Slot(@Nonnull final SlotType type) {
        this.type = Objects.requireNonNull(type);
    }

    @Nullable
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Nonnull
    public Slot setMetadata(@Nullable final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Nonnull
    public SlotType getType() {
        return type;
    }

    public enum SlotType {
        CONSTANT,
        TASK,
        LIST,
    }
}
