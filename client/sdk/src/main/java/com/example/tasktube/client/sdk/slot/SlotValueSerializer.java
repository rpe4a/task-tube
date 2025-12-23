package com.example.tasktube.client.sdk.slot;

import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.Nothing;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.ValueList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class SlotValueSerializer {
    private final ObjectMapper objectMapper;

    public SlotValueSerializer(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Nonnull
    public Slot serialize(@Nonnull final Nothing<?> value) {
        Preconditions.checkNotNull(value);

        return new Slot()
                .setValue(value.getNull())
                .setValueReferenceType(getJavaType(value.getType()))
                .setFill(true)
                .setType(Slot.SlotType.NOTHING);
    }

    @Nonnull
    public Slot serialize(@Nonnull final Constant<?> value) {
        Preconditions.checkNotNull(value);

        return new Slot()
                .setValue(value.getData())
                .setValueReferenceType(getJavaType(value.getType()))
                .setFill(true)
                .setType(Slot.SlotType.CONSTANT);
    }

    @Nonnull
    public Slot serialize(@Nonnull final TaskResult<?> value) {
        Preconditions.checkNotNull(value);

        return new Slot()
                .setTaskReference(value.getId())
                .setMetadata(null)
                .setType(Slot.SlotType.TASK);
    }

    @Nonnull
    public Slot serialize(@Nonnull final ValueList<?> value) {
        Preconditions.checkNotNull(value);

        final List<Slot> slots = value.get().stream()
                .map(v -> v.serialize(this))
                .toList();

        return new SlotList()
                .setType(Slot.SlotType.LIST)
                .setValues(slots);
    }

    private String getJavaType(final Type type) {
        return objectMapper
                .getTypeFactory()
                .constructType(type)
                .toCanonical();
    }

}
