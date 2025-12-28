package com.example.tasktube.client.sdk.task.argument;

import jakarta.annotation.Nonnull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class ListArgument extends Argument {
    public List<Argument> values = new LinkedList<>();

    public ListArgument() {
        super(ArgumentType.LIST);
    }

    public void add(@Nonnull final Argument argument) {
        values.add(Objects.requireNonNull(argument));
    }

    @Nonnull
    public List<Argument> getValues() {
        return values;
    }

    @Nonnull
    public ListArgument setValues(@Nonnull final List<Argument> arguments) {
        values = List.copyOf(Objects.requireNonNull(arguments));
        return this;
    }

    @Nonnull
    @Override
    public Object deserialize(@Nonnull final ArgumentDeserializer argumentDeserializer) {
        return Objects.requireNonNull(argumentDeserializer).deserialize(this);
    }
}
