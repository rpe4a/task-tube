package com.example.tasktube.client.sdk.task.argument;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public final class ConstantArgument extends Argument {
    private Object value;
    private String valueReferenceType;

    public ConstantArgument() {
        super(ArgumentType.CONSTANT);
    }

    @Nonnull
    public Object getValue() {
        return value;
    }

    public ConstantArgument setValue(@Nonnull final Object value) {
        this.value = Objects.requireNonNull(value);
        return this;
    }

    @Nonnull
    public String getValueReferenceType() {
        return valueReferenceType;
    }

    public ConstantArgument setValueReferenceType(final String valueTypeReference) {
        this.valueReferenceType = Objects.requireNonNull(valueTypeReference);
        return this;
    }

    @NonNull
    @Override
    public Object deserialize(@Nonnull final ArgumentDeserializer argumentDeserializer) {
        return Objects.requireNonNull(argumentDeserializer).deserialize(this);
    }
}
