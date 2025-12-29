package com.example.tasktube.sandboxspring.tube.regress;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;
import jakarta.annotation.Nonnull;

import java.time.LocalDate;

public class TaskReturnLocalDate extends Task0<LocalDate> {

    @Nonnull
    @Override
    public Value<LocalDate> run() throws Exception {
        return constant(LocalDate.now());
    }
}
