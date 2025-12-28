package com.example.tasktube.server.domain.values.slot;

import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.values.argument.Argument;

public final class ConstantSlot extends Slot {
    private Object value;
    private String valueReferenceType;

    public ConstantSlot() {
        super(SlotType.CONSTANT);
    }

    public Object getValue() {
        return value;
    }

    public Slot setValue(final Object value) {
        this.value = value;
        return this;
    }

    public String getValueReferenceType() {
        return valueReferenceType;
    }

    public Slot setValueReferenceType(final String valueTypeReference) {
        this.valueReferenceType = valueTypeReference;
        return this;
    }

    @Override
    public Argument fill(final IArgumentFiller argumentFiller) {
        return argumentFiller.fill(this);
    }
}
