package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnInteger extends Task0<Integer> {

    @Nonnull
    @Override
    public Value<Integer> run() throws Exception {
        return constant(1);
    }
}
