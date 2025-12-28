package com.example.tasktube.server.domain.port.out;

import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.slot.ConstantSlot;
import com.example.tasktube.server.domain.values.slot.ListSlot;
import com.example.tasktube.server.domain.values.slot.TaskSlot;

public interface IArgumentFiller {
    Argument fill(final ConstantSlot slot);

    Argument fill(final TaskSlot slot);

    Argument fill(final ListSlot slot);
}
