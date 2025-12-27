package com.example.tasktube.client.sdk.task;

import com.example.tasktube.client.sdk.slot.Slot;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import jakarta.annotation.Nonnull;

public interface Value<T> {

    @Nonnull
    Slot<?> serialize(final SlotValueSerializer serializer);
}
