package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;

public class TaskReturnListString extends Task0<List<String>> {

    @Nonnull
    @Override
    public Value<List<String>> run() throws Exception {
        return constant(List.of("hello", "world"), new TypeReference<>() {});
    }
}
