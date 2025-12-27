package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public final class ValueList<T> implements Value<List<T>> {
    private final List<? extends Value<T>> list;

    public ValueList(@Nonnull final List<? extends Value<T>> list) {
        this.list = List.copyOf(Objects.requireNonNull(list));
    }

    @Nonnull
    public List<? extends Value<T>> get() {
        return list;
    }

    @Override
    @Nonnull
    public Slot<?> serialize(final SlotValueSerializer serializer) {
        Preconditions.checkNotNull(serializer);

        return serializer.serialize(this);
    }
}
