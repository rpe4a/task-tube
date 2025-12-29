package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.Set;

public class TaskReturnSetString extends Task0<Set<String>> {

    @Nonnull
    @Override
    public Value<Set<String>> run() throws Exception {
        return constant(Set.of("hello", "world"), new TypeReference<>() {});
    }
}
