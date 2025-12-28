package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnChar extends Task0<Character> {

    @Nonnull
    @Override
    public Value<Character> run() throws Exception {
        return constant('T');
    }
}
