package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public class TaskReturnUUID extends Task0<UUID> {

    @Nonnull
    @Override
    public Value<UUID> run() throws Exception {
        return constant(UUID.randomUUID());
    }
}
