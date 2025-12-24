package com.example.tasktube.client.sdk.slot;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlotArgumentDeserializer {
    private final ObjectMapper objectMapper;

    public SlotArgumentDeserializer(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public Object deserialize(final Slot slot) {
        Preconditions.checkNotNull(slot);

        return objectMapper.convertValue(slot.getValue(), getJavaType(slot.getValueReferenceType()));
    }

    public Object deserialize(final SlotList slotList) {
        Preconditions.checkNotNull(slotList);

        return  slotList.values
                .stream()
                .map(this::deserialize)
                .toList();
    }

    private JavaType getJavaType(final String canonicalName) {
        return objectMapper
                .getTypeFactory()
                .constructFromCanonical(canonicalName);
    }

}
