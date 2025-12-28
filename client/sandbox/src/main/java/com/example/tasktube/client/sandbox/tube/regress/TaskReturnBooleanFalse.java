package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnBooleanFalse extends Task0<Boolean> {

    @Nonnull
    @Override
    public Value<Boolean> run() throws Exception {
        return constant(false);
    }
}
