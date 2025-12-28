package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnDouble extends Task0<Double> {

    @Nonnull
    @Override
    public Value<Double> run() throws Exception {
        return constant(1.0d);
    }
}

