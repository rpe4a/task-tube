package com.example.tasktube.client.sdk.slot;

import jakarta.annotation.Nonnull;

public final class NothingSlot extends ConstantSlot {

    public NothingSlot() {
        super(SlotType.NOTHING);
    }

    @Override
    public Object deserialize(@Nonnull final SlotArgumentDeserializer slotDeserializer) {
        return null;
    }
}
