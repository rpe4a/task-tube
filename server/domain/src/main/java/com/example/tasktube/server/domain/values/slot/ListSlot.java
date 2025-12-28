package com.example.tasktube.server.domain.values.slot;

import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.values.argument.Argument;

import java.util.LinkedList;
import java.util.List;

public final class ListSlot extends Slot {
    public List<Slot> values = new LinkedList<>();

    public ListSlot() {
        super(SlotType.LIST);
    }

    public void add(final Slot slot) {
        values.add(slot);
    }

    public List<Slot> getValues() {
        return values;
    }

    public ListSlot setValues(final List<Slot> slots) {
        values = slots;
        return this;
    }

    @Override
    public Argument fill(final IArgumentFiller argumentFiller) {
        return argumentFiller.fill(this);
    }
}
