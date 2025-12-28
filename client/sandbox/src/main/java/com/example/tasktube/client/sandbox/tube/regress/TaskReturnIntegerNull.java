package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnIntegerNull extends Task0<Integer> {

    @Nonnull
    @Override
    public Value<Integer> run() throws Exception {
        return nothing();
    }
}
