package com.example.tasktube.server.application.utils;

import com.example.tasktube.server.domain.values.slot.Slot;
import com.example.tasktube.server.domain.values.slot.ListSlot;
import com.example.tasktube.server.domain.values.slot.TaskSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SlotUtils {

    public static List<UUID> getTaskIdList(final List<Slot> slots) {
        if (slots == null || slots.isEmpty()) {
            return List.of();
        }

        final List<UUID> taskIdList = new ArrayList<>();

        taskIdList.addAll(getTaskIdListInternal(slots));

        taskIdList.addAll(slots
                .stream()
                .filter(s -> Slot.SlotType.LIST.equals(s.getType()))
                .map(s -> (ListSlot) s)
                .flatMap(s -> getTaskIdList(s.values).stream())
                .toList());

        return taskIdList;
    }

    private static List<UUID> getTaskIdListInternal(final List<Slot> slots) {
        return slots
                .stream()
                .filter(s -> Slot.SlotType.TASK.equals(s.getType()))
                .map(s -> ((TaskSlot) s).getTaskReference())
                .toList();
    }
}
