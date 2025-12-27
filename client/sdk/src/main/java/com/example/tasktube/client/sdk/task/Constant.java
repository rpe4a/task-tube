package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public final class Constant<T> implements Value<T> {
    private final T data;
    private final Type type;

    public Constant(@Nonnull final T data) {
        this(Objects.requireNonNull(data), data.getClass());
    }

    public Constant(@Nullable final T data, @Nonnull final TypeReference<T> typeReference) {
        this(data, Objects.requireNonNull(typeReference).getType());
    }

    public Constant(@Nullable final T data, @Nonnull final Type type) {
        this.data = data;
        this.type = type;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    @Override
    @Nonnull
    public Slot<?>  serialize(@Nonnull final SlotValueSerializer serializer) {
        Preconditions.checkNotNull(serializer);

        return serializer.serialize(this);
    }
}
