package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

public class TaskReturnArrayDto extends Task0<RegressDto[]> {

    @Nonnull
    @Override
    public Value<RegressDto[]> run() throws Exception {
        return constant(
                new RegressDto[] {new RegressDto("hello", 1, true), new RegressDto("world", 0, false)},
                new TypeReference<>() {}
        );
    }
}
