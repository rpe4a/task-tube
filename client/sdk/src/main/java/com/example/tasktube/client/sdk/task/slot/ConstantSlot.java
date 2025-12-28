package com.example.tasktube.client.sdk.task.slot;

import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

public final class ConstantSlot extends Slot {
    private Object value;
    private String valueReferenceType;

    public ConstantSlot() {
        super(SlotType.CONSTANT);
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Nonnull
    public ConstantSlot setValue(@Nullable final Object value) {
        this.value = value;
        return this;
    }

    @Nonnull
    public String getValueReferenceType() {
        return valueReferenceType;
    }

    @Nonnull
    public ConstantSlot setValueReferenceType(@Nonnull final String valueTypeReference) {
        this.valueReferenceType = Objects.requireNonNull(valueTypeReference);
        return this;
    }
}
