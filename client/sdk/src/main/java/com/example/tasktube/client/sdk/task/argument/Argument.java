package com.example.tasktube.client.sdk.task.argument;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public abstract sealed class Argument permits ConstantArgument, ListArgument {
    private final ArgumentType type;
    private Map<String, Object> metadata;

    protected Argument(final ArgumentType type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Argument setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public ArgumentType getType() {
        return type;
    }

    @NonNull
    public abstract Object deserialize(@Nonnull final ArgumentDeserializer argumentDeserializer);

    public enum ArgumentType {
        CONSTANT,
        LIST
    }
}

