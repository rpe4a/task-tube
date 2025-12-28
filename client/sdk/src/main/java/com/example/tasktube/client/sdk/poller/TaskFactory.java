package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.task.Task;
import jakarta.annotation.Nonnull;

public interface TaskFactory {
    @Nonnull
    Task<?> createInstance(@Nonnull String taskName);
}
