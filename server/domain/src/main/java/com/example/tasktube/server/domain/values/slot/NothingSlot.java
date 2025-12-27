package com.example.tasktube.server.domain.values.slot;

public final class NothingSlot extends Slot {
    private Object value;
    private String valueReferenceType;

    public NothingSlot() {
        super(SlotType.NOTHING);
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
}
