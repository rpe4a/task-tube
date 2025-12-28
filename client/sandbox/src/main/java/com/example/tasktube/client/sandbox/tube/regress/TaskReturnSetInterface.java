package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sandbox.tube.regress.dto.IRegressInterface;
import com.example.tasktube.client.sandbox.tube.regress.dto.RegressInterfaceImpl;
import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Set;

public class TaskReturnSetInterface extends Task0<Set<IRegressInterface>> {

    @Nonnull
    @Override
    public Value<Set<IRegressInterface>> run() throws Exception {
        return constant(
                Set.of(
                        new RegressInterfaceImpl().setValue("hello world 1"),
                        new RegressInterfaceImpl().setValue("hello world 2")
                ),
                new TypeReference<>() {}
        );
    }
}
