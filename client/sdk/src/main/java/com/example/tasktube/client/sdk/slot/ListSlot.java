package com.example.tasktube.client.sdk.slot;

import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.LinkedList;
import java.util.List;

public final class ListSlot extends Slot<ListSlot> {
    public List<? extends Slot<?>> values = new LinkedList<>();

    ListSlot() {
        super(SlotType.LIST);
    }

    @Nonnull
    public List<? extends Slot<?>> getValues() {
        return values;
    }

    public ListSlot setValues(@Nonnull final List<? extends Slot<?>> slots) {
        Preconditions.checkNotNull(slots);

        values = slots;
        return this;
    }

    @Override
    public Object deserialize(@Nonnull final SlotArgumentDeserializer slotDeserializer) {
        Preconditions.checkNotNull(slotDeserializer);

        return slotDeserializer.deserialize(this);
    }
}
