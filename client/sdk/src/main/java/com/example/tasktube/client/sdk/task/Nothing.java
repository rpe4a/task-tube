package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Type;

public class Nothing<T> implements Value<T> {

    @Nullable
    public T getNull() {
        return null;
    }

    @Nonnull
    public Type getType() {
        return Object.class;
    }

    @Override
    @Nonnull
    public Slot serialize(final SlotValueSerializer serializer) {
        Preconditions.checkNotNull(serializer);

        return serializer.serialize(this);
    }
}
