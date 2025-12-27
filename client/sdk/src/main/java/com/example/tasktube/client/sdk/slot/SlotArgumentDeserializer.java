package com.example.tasktube.client.sdk.slot;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class SlotArgumentDeserializer {
    private final ObjectMapper objectMapper;

    public SlotArgumentDeserializer(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public Object deserialize(final ConstantSlot slot) {
        Preconditions.checkNotNull(slot);

        return objectMapper.convertValue(slot.getValue(), getJavaType(slot.getValueReferenceType()));
    }

    // TODO - как от этого избавиться ввести Аргументы?
    public Object deserialize(final TaskSlot slot) {
        throw new IllegalStateException("Task slot cannot be deserialized. Server side is currently not supported.");
    }

    public Object deserialize(final ListSlot listSlot) {
        Preconditions.checkNotNull(listSlot);

        return listSlot.values
                .stream()
                .map(s -> s.deserialize(this))
                .toList();
    }

    private JavaType getJavaType(final String canonicalName) {
        return objectMapper
                .getTypeFactory()
                .constructFromCanonical(canonicalName);
    }

}
