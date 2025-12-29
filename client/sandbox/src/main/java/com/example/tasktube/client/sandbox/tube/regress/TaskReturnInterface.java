package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.IRegressInterface;
import com.example.tasktube.client.sandbox.tube.regress.dto.RegressInterfaceImpl;
import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

public class TaskReturnInterface extends Task0<IRegressInterface> {

    @Nonnull
    @Override
    public Value<IRegressInterface> run() throws Exception {
        return constant(new RegressInterfaceImpl().setValue("hello world"));
    }
}
