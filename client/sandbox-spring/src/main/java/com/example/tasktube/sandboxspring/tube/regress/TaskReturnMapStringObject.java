package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.Map;

public class TaskReturnMapStringObject extends Task0<Map<String, Object>> {

    @Nonnull
    @Override
    public Value<Map<String, Object>> run() throws Exception {
        return constant(Map.of("i", 1, "b", true, "s", "hello"), new TypeReference<>() {});
    }
}
