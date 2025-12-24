package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;

@FunctionalInterface
public interface Pipeline {
    void handle(@Nonnull TaskInput input, @Nonnull TaskOutput output);
}

