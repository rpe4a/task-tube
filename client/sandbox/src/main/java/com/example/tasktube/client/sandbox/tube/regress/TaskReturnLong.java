package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnLong extends Task0<Long> {

    @Nonnull
    @Override
    public Value<Long> run() throws Exception {
        return constant(1L);
    }
}
