package com.example.tasktube.client.sdk.core.task;

import com.example.tasktube.client.sdk.core.task.slot.Slot;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
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

    @Nonnull
    @Override
    public Slot serialize(@Nonnull final SlotValueSerializer serializer) {
        return Objects.requireNonNull(serializer).serialize(this);
    }
}
