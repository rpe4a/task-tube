package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.example.tasktube.sandboxspring.tube.regress.dto.RegressStatus;
import jakarta.annotation.Nonnull;

public class TaskReturnEnum extends Task0<RegressStatus> {

    @Nonnull
    @Override
    public Value<RegressStatus> run() throws Exception {
        return constant(RegressStatus.PROCESSING);
    }
}
