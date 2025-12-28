package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

public class TaskReturnListMap extends Task0<List<Map<String, Object>>> {

    @Nonnull
    @Override
    public Value<List<Map<String, Object>>> run() throws Exception {
        return constant(List.of(Map.of("hello", 1)), new TypeReference<>() {});
    }
}
