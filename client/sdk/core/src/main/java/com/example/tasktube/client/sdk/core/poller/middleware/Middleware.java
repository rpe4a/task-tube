package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;

public interface Middleware {
    void invoke(@Nonnull TaskInput input, @Nonnull TaskOutput output, @Nonnull Pipeline next);
}
