package com.example.tasktube.client.sdk.task.argument;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract sealed class Argument permits ConstantArgument, ListArgument {
    private final ArgumentType type;
    private Map<String, Object> metadata;

    protected Argument(@Nonnull final ArgumentType type) {
        this.type = Objects.requireNonNull(type);
    }

    @Nonnull
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Nullable
    public Argument setMetadata(@Nullable final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Nonnull
    public ArgumentType getType() {
        return type;
    }

    @Nonnull
    public abstract Object deserialize(@Nonnull final ArgumentDeserializer argumentDeserializer);

    public enum ArgumentType {
        CONSTANT,
        LIST
    }
}

