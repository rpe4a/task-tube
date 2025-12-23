package com.example.tasktube.client.sdk.slot;

import com.example.tasktube.client.sdk.task.Constant;
import com.example.tasktube.client.sdk.task.Nothing;
import com.example.tasktube.client.sdk.task.TaskResult;
import com.example.tasktube.client.sdk.task.Value;
import com.example.tasktube.client.sdk.task.ValueList;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class SlotArgumentDeserializer {
    private final ObjectMapper objectMapper;

    public SlotArgumentDeserializer(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public Slot deserialize(final Slot slot) {
        Preconditions.checkNotNull(slot);
        return objectMapper.convertValue(slot.getValue(), getJavaType(slot.getValueReferenceType()));
    }

    private JavaType getJavaType(final String canonicalName) {
        return objectMapper
                .getTypeFactory()
                .constructFromCanonical(canonicalName);
    }

}
