package com.example.tasktube.client.sdk.slot;

import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.Nothing;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.Value;
import com.example.tasktube.client.sdk.task.ValueList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class SlotValueMapper {
    private final ObjectMapper objectMapper;

    public SlotValueMapper(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public Slot map(final Value<?> value) {
        Preconditions.checkNotNull(value);
        if (Nothing.class.isAssignableFrom(value.getClass())) {
            final Nothing nothing = (Nothing) value;
            return new Slot()
                    .setValue(null)
                    .setValueReferenceType(getJavaType(nothing.getType()))
                    .setFill(true)
                    .setType(Slot.SlotType.NOTHING);
        } else if (Constant.class.isAssignableFrom(value.getClass())) {
            final Constant<?> constant = (Constant<?>) value;
            return new Slot()
                    .setValue(constant.getData())
                    .setValueReferenceType(getJavaType(constant.getType()))
                    .setFill(true)
                    .setType(Slot.SlotType.CONSTANT);
        } else if (TaskResult.class.isAssignableFrom(value.getClass())) {
            final TaskResult<?> taskResult = (TaskResult<?>) value;
            return new Slot()
                    .setTaskReference(taskResult.getId())
                    .setMetadata(null)
                    .setType(Slot.SlotType.TASK);
        } else if (ValueList.class.isAssignableFrom(value.getClass())) {
            final ValueList<?> valueList = (ValueList<?>) value;
            final List<Slot> slots = valueList.get().stream()
                    .map(this::map)
                    .toList();
            return new SlotList()
                    .setType(Slot.SlotType.LIST)
                    .setValues(slots);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }
    }

    private String getJavaType(final Type type) {
        return objectMapper
                .getTypeFactory()
                .constructType(type)
                .toCanonical();
    }

}
