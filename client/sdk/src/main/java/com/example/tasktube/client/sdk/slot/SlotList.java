package com.example.tasktube.client.sdk.slot;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.LinkedList;
import java.util.List;

public class SlotList extends Slot {
    public List<Slot> values = new LinkedList<>();

    public void add(@Nonnull final Slot slot) {
        Preconditions.checkNotNull(slot);

        values.add(slot);
    }

    @Nonnull
    public List<Slot> getValues() {
        return values;
    }

    @Override
    public SlotList setType(@Nonnull final SlotType type) {
        Preconditions.checkNotNull(type);

        return (SlotList) super.setType(type);
    }

    public SlotList setValues(@Nonnull final List<Slot> slots) {
        Preconditions.checkNotNull(slots);

        values = slots;
        return this;
    }
}
