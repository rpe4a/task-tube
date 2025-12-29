package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.example.tasktube.sandboxspring.tube.regress.dto.RegressDto;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

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
