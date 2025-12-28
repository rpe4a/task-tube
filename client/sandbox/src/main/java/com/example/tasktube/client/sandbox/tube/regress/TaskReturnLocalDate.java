package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.time.LocalDate;

public class TaskReturnLocalDate extends Task0<LocalDate> {

    @Nonnull
    @Override
    public Value<LocalDate> run() throws Exception {
        return constant(LocalDate.now());
    }
}
