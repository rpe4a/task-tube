package com.example.tasktube.client.sdk.core.task.argument;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

public final class ConstantArgument extends Argument {
    private Object value;
    private String valueReferenceType;

    public ConstantArgument() {
        super(ArgumentType.CONSTANT);
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Nonnull
    public ConstantArgument setValue(@Nullable final Object value) {
        this.value = Objects.requireNonNull(value);
        return this;
    }

    @Nonnull
    public String getValueReferenceType() {
        return valueReferenceType;
    }

    @Nonnull
    public ConstantArgument setValueReferenceType(@Nonnull final String valueTypeReference) {
        this.valueReferenceType = Objects.requireNonNull(valueTypeReference);
        return this;
    }

    @Nonnull
    @Override
    public Object deserialize(@Nonnull final ArgumentDeserializer argumentDeserializer) {
        return Objects.requireNonNull(argumentDeserializer).deserialize(this);
    }
}
