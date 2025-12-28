package com.example.tasktube.client.sdk.task.slot;

import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.ListValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class SlotValueSerializer {
    private final ObjectMapper objectMapper;

    public SlotValueSerializer(@Nonnull final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Nonnull
    public Slot  serialize(@Nonnull final Constant<?> value) {
        Preconditions.checkNotNull(value);

        return new ConstantSlot()
                .setValue(value.getData())
                .setValueReferenceType(getJavaType(value.getType()));
    }

    @Nonnull
    public Slot serialize(@Nonnull final TaskResult<?> value) {
        Preconditions.checkNotNull(value);

        return new TaskSlot()
                .setTaskReference(value.getId());
    }

    @Nonnull
    public Slot  serialize(@Nonnull final ListValue<?> value) {
        Preconditions.checkNotNull(value);

        final List<? extends Slot> slots = value.get().stream()
                .map(v -> v.serialize(this))
                .toList();

        return new ListSlot()
                .setValues(slots);
    }

    private String getJavaType(final Type type) {
        return objectMapper
                .getTypeFactory()
                .constructType(type)
                .toCanonical();
    }

}
