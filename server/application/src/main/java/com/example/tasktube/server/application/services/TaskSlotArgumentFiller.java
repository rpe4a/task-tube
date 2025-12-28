package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.argument.Argument;
import com.example.tasktube.server.domain.values.argument.ConstantArgument;
import com.example.tasktube.server.domain.values.argument.ListArgument;
import com.example.tasktube.server.domain.values.slot.ConstantSlot;
import com.example.tasktube.server.domain.values.slot.ListSlot;
import com.example.tasktube.server.domain.values.slot.Slot;
import com.example.tasktube.server.domain.values.slot.TaskSlot;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskSlotArgumentFiller implements IArgumentFiller {
    private static final int MAX_PARALLEL_VALUES_COUNT = 16;

    private final ITaskRepository taskRepository;

    public TaskSlotArgumentFiller(final ITaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository);
    }

    @Override
    public Argument fill(final ConstantSlot slot) {
        return new ConstantArgument()
                .setValue(slot.getValue())
                .setValueReferenceType(slot.getValueReferenceType())
                .setMetadata(slot.getMetadata());
    }

    @Override
    public Argument fill(final TaskSlot slot) {
        final UUID taskId = slot.getTaskReference();
        final Task task = taskRepository.get(taskId).get();
        if (!task.isCompleted()) {
            throw new ApplicationException("Task is not completed");
        }

        return fill(task.getOutput());
    }

    @Override
    public Argument fill(final ListSlot slot) {
        final List<Argument> arguments =
                (
                        slot.values.size() <= MAX_PARALLEL_VALUES_COUNT
                                ? slot.values.stream()
                                : slot.values.parallelStream()
                )
                        .map(this::fill)
                        .toList();
        return new ListArgument()
                .setValues(arguments)
                .setMetadata(slot.getMetadata());
    }

    private Argument fill(final Slot slot) {
        return slot.fill(this);
    }
}
