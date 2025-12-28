package com.example.tasktube.client.sdk.task.slot;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

public final class ConstantSlot extends Slot {
    private Object value;
    private String valueReferenceType;

    public ConstantSlot() {
        this(SlotType.CONSTANT, null, Object.class.getCanonicalName());
    }

    public ConstantSlot(@Nullable final Object value, @Nonnull final String valueReferenceType) {
        this(SlotType.CONSTANT, value, valueReferenceType);
    }

    private ConstantSlot(@Nonnull final SlotType type, @Nullable final Object value, @Nullable final String valueReferenceType) {
        super(type);
        this.value = value;
        this.valueReferenceType = Objects.requireNonNull(valueReferenceType);
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
