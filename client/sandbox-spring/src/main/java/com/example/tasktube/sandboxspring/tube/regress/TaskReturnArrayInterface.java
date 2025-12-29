package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import com.example.tasktube.sandboxspring.tube.regress.dto.IRegressInterface;
import com.example.tasktube.sandboxspring.tube.regress.dto.RegressInterfaceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

public class TaskReturnArrayInterface extends Task0<IRegressInterface[]> {

    @Nonnull
    @Override
    public Value<IRegressInterface[]> run() throws Exception {
        return constant(
                new IRegressInterface[] {new RegressInterfaceImpl().setValue("hello"), new RegressInterfaceImpl().setValue("world")},
                new TypeReference<>() {}
        );
    }
}
