package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;

@FunctionalInterface
public interface Pipeline {
    void handle(@Nonnull TaskInput input, @Nonnull TaskOutput output);
}

