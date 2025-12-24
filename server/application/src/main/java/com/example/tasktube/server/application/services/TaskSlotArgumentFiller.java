package com.example.tasktube.server.application.services;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.values.Slot;
import com.example.tasktube.server.domain.values.SlotList;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TaskSlotArgumentFiller {
    private static final int MAX_PARALLEL_VALUES_COUNT = 16;

    private final ITaskRepository taskRepository;

    public TaskSlotArgumentFiller(final ITaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository);
    }

    public Slot fill(final Slot slot) {
        if (slot.isNothingSlot() || slot.isConstantSlot()) {
            return slot;
        } else if (slot.isTaskSlot()) {
            return fillTaskSlot(slot);
        } else if (slot.isListSlot()) {
            return fillSlotList((SlotList) slot);
        } else {
            throw new IllegalArgumentException("Invalid slot type: " + slot.getType());
        }
    }

    private Slot fillTaskSlot(final Slot slot) {
        final UUID taskId = slot.getTaskReference();
        final Task task = taskRepository.get(taskId).get();
        if (!task.isCompleted()) {
            throw new ApplicationException("Task is not completed");
        }

        return fill(task.getOutput());
    }

    private Slot fillSlotList(final SlotList slotList) {
        final List<Slot> slots =
                (
                        slotList.values.size() <= MAX_PARALLEL_VALUES_COUNT
                                ? slotList.values.stream()
                                : slotList.values.parallelStream()
                )
                        .map(this::fill)
                        .toList();
        return new SlotList()
                .setType(Slot.SlotType.LIST)
                .setValues(slots);
    }
}
