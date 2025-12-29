package com.example.tasktube.client.sdk.core.poller;

import com.example.tasktube.client.sdk.core.task.Task;
import jakarta.annotation.Nonnull;

public interface TaskFactory {
    @Nonnull
    Task<?> createInstance(@Nonnull String taskName);
}
