package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnFloat extends Task0<Float> {

    @Nonnull
    @Override
    public Value<Float> run() throws Exception {
        return constant(1.0f);
    }
}
