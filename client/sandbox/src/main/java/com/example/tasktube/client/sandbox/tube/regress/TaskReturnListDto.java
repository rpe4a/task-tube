package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;

public class TaskReturnListDto extends Task0<List<RegressDto>> {

    @Nonnull
    @Override
    public Value<List<RegressDto>> run() throws Exception {
        return constant(
                List.of(
                        new RegressDto("hello", 1, true),
                        new RegressDto("world", 0, false)
                ),
                new TypeReference<>() {}
        );
    }
}
