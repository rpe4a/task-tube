package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;

public interface ITaskFactory {
    @Nonnull
    Task<?> createInstance(@Nonnull String taskName);
}
