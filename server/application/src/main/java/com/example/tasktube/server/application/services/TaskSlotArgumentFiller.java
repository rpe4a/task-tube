package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
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
    public Slot fill(final ConstantSlot slot) {
        return slot;
    }

    @Override
    public Slot fill(final TaskSlot slot) {
        final UUID taskId = slot.getTaskReference();
        final Task task = taskRepository.get(taskId).get();
        if (!task.isCompleted()) {
            throw new ApplicationException("Task is not completed");
        }

        return fill(task.getOutput());
    }

    @Override
    public Slot fill(final ListSlot slot) {
        final List<Slot> slots =
                (
                        slot.values.size() <= MAX_PARALLEL_VALUES_COUNT
                                ? slot.values.stream()
                                : slot.values.parallelStream()
                )
                        .map(this::fill)
                        .toList();
        return new ListSlot()
                .setValues(slots);
    }

    private Slot fill(final Slot slot) {
        return slot.fill(this);
    }
}
