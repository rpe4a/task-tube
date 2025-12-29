package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.Map;

public class TaskReturnMapStringDto extends Task0<Map<String, RegressDto>> {

    @Nonnull
    @Override
    public Value<Map<String, RegressDto>> run() throws Exception {
        return constant(Map.of("hello", new RegressDto("hello", 1, true)), new TypeReference<>() {});
    }
}
