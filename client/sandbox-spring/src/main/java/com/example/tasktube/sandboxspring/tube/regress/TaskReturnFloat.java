package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnFloat extends Task0<Float> {

    @Nonnull
    @Override
    public Value<Float> run() throws Exception {
        return constant(1.0f);
    }
}
