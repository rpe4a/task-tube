package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressDto;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnDtoNull extends Task0<RegressDto> {

    @Nonnull
    @Override
    public Value<RegressDto> run() throws Exception {
        return nothing();
    }
}
