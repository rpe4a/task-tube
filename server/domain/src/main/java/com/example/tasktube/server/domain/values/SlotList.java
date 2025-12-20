package com.example.tasktube.server.domain.values;

import java.util.LinkedList;
import java.util.List;

public class SlotList extends Slot {
    public List<Slot> values = new LinkedList<>();

    public void add(final Slot slot) {
        values.add(slot);
    }

    public List<Slot> getValues() {
        return values;
    }

    @Override
    public SlotList setType(final SlotType type) {
        return (SlotList) super.setType(type);
    }

    public SlotList setValues(final List<Slot> slots) {
        values = slots;
        return this;
    }
}
