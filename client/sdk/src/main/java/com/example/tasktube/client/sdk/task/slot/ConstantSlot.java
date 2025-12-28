package com.example.tasktube.client.sdk.task.slot;

import com.example.tasktube.client.sdk.task.argument.ArgumentDeserializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

public final class ConstantSlot extends Slot<ConstantSlot> {
    private Object value;
    private String valueReferenceType;

    public ConstantSlot() {
        this(SlotType.CONSTANT);
    }

    private ConstantSlot(final SlotType type) {
        super(type);
    }

    public Object getValue() {
        return value;
    }

    public ConstantSlot setValue(final Object value) {
        this.value = value;
        return this;
    }

    public String getValueReferenceType() {
        return valueReferenceType;
    }

    public ConstantSlot setValueReferenceType(final String valueTypeReference) {
        this.valueReferenceType = valueTypeReference;
        return this;
    }
}
