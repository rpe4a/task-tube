package com.example.tasktube.server.domain.values.slot;

import java.util.LinkedList;
import java.util.List;

public final class SlotList extends Slot {
    public List<Slot> values = new LinkedList<>();

    public SlotList() {
        super(SlotType.LIST);
    }

    public void add(final Slot slot) {
        values.add(slot);
    }

    public List<Slot> getValues() {
        return values;
    }

    public SlotList setValues(final List<Slot> slots) {
        values = slots;
        return this;
    }
}
