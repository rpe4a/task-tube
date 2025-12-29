package com.example.tasktube.client.sdk.core.task;

import com.example.tasktube.client.sdk.core.task.slot.Slot;
import com.example.tasktube.client.sdk.core.task.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

public interface Value<T> {

    @Nonnull
    Slot serialize(@Nonnull final SlotValueSerializer serializer);
}
