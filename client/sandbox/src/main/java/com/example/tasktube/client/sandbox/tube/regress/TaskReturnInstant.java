package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

import java.time.Instant;

public class TaskReturnInstant extends Task0<Instant> {

    @Nonnull
    @Override
    public Value<Instant> run() throws Exception {
        return constant(Instant.now());
    }
}
