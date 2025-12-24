package com.example.tasktube.client.sdk.poller.middleware;

import com.example.tasktube.client.sdk.task.TaskInput;
import com.example.tasktube.client.sdk.task.TaskOutput;
import jakarta.annotation.Nonnull;

public interface Middleware {
    void invoke(@Nonnull TaskInput input, @Nonnull TaskOutput output, @Nonnull Pipeline next);
}
