package com.example.tasktube.client.sdk.core.task.argument;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.Objects;

public class ArgumentDeserializer {
    private final ObjectMapper objectMapper;

    public ArgumentDeserializer(@Nonnull final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Nonnull
    public Object deserialize(@Nonnull final ConstantArgument argument) {
        Preconditions.checkNotNull(argument);

        return objectMapper.convertValue(argument.getValue(), getJavaType(argument.getValueReferenceType()));
    }

    @Nonnull
    public Object deserialize(@Nonnull final ListArgument argument) {
        Preconditions.checkNotNull(argument);

        return argument.values
                .stream()
                .map(a -> a.deserialize(this))
                .toList();
    }

    @Nonnull
    public Object deserialize(@Nonnull final Argument argument) {
        Preconditions.checkNotNull(argument);
        return argument.deserialize(this);
    }

    private JavaType getJavaType(final String canonicalName) {
        return objectMapper
                .getTypeFactory()
                .constructFromCanonical(canonicalName);
    }

}
