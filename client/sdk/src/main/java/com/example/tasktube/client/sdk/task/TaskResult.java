package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public class TaskResult<T> implements Value<T> {
    private final UUID id;

    TaskResult(@Nonnull final UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nonnull
    public UUID getId() {
        return id;
    }

    @Override
    @Nonnull
    public Slot serialize(final SlotValueSerializer serializer) {
        Preconditions.checkNotNull(serializer);

        return serializer.serialize(this);
    }
}
