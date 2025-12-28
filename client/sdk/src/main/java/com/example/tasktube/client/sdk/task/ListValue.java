package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.task.slot.Slot;
import com.example.tasktube.client.sdk.task.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public final class ListValue<T> implements Value<List<T>> {
    private final List<? extends Value<T>> values;

    ListValue(@Nonnull final List<? extends Value<T>> values) {
        this.values = List.copyOf(Objects.requireNonNull(values));
    }

    @Nonnull
    public List<? extends Value<T>> get() {
        return values;
    }

    @Nonnull
    @Override
    public Slot serialize(@Nonnull final SlotValueSerializer serializer) {
        return Objects.requireNonNull(serializer).serialize(this);
    }
}
