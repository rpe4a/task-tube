package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

public class TaskReturnArrayString extends Task0<String[]> {

    @Nonnull
    @Override
    public Value<String[]> run() throws Exception {
        return constant(
                new String[] {"hello", "world"},
                new TypeReference<>() {}
        );
    }
}
