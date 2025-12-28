package com.example.tasktube.client.sdk.task.slot;

import jakarta.annotation.Nonnull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class ListSlot extends Slot {
    public List<? extends Slot> values;

    ListSlot(@Nonnull final List<? extends Slot> values) {
        this(SlotType.LIST, values);
    }

    ListSlot() {
        this(SlotType.LIST, new LinkedList<>());
    }

    private ListSlot(@Nonnull final SlotType type, @Nonnull final List<? extends Slot> values) {
        super(type);
        this.values = List.copyOf(Objects.requireNonNull(values));
    }

    @Nonnull
    public List<? extends Slot> getValues() {
        return values;
    }

    @Nonnull
    public ListSlot setValues(@Nonnull final List<? extends Slot> slots) {
        values = List.copyOf(Objects.requireNonNull(slots));
        return this;
    }
}
