package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Set;

public class TaskReturnSetDto extends Task0<Set<RegressDto>> {

    @Nonnull
    @Override
    public Value<Set<RegressDto>> run() throws Exception {
        return constant(
                Set.of(
                        new RegressDto("hello", 1, true),
                        new RegressDto("world", 0, false)
                ),
                new TypeReference<>() {}
        );
    }
}
