package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.RegressStatus;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnEnum extends Task0<RegressStatus> {

    @Nonnull
    @Override
    public Value<RegressStatus> run() throws Exception {
        return constant(RegressStatus.PROCESSING);
    }
}
