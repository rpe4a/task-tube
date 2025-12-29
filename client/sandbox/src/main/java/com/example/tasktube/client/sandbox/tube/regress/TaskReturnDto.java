package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnDto extends Task0<RegressDto> {

    @Nonnull
    @Override
    public Value<RegressDto> run() throws Exception {
        return constant(new RegressDto("regress dto", 1, true));
    }
}
