package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.values.slot.ConstantSlot;
import com.example.tasktube.server.domain.values.slot.ListSlot;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.example.tasktube.server.domain.values.slot.TaskSlot;

public interface IArgumentFiller {
    Slot fill(final ConstantSlot slot);

    Slot fill(final TaskSlot slot);

    Slot fill(final ListSlot slot);
}
