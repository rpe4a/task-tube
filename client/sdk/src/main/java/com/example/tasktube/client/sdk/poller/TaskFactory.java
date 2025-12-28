package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.task.Task;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public interface TaskFactory {
    @Nullable
    Task<?> createInstance(@Nonnull String taskName);
}
