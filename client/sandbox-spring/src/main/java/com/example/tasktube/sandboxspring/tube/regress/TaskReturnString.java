package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnString extends Task0<String> {

    @Nonnull
    @Override
    public Value<String> run() throws Exception {
        return constant("Hello world!");
    }
}
