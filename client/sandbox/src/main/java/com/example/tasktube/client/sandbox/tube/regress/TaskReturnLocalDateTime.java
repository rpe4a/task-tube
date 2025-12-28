package com.example.tasktube.client.sandbox.tube.regress;

import com.example.tasktube.client.sdk.task.Task0;
import com.example.tasktube.client.sdk.task.Value;
import jakarta.annotation.Nonnull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskReturnLocalDateTime extends Task0<LocalDateTime> {

    @Nonnull
    @Override
    public Value<LocalDateTime> run() throws Exception {
        return constant(LocalDateTime.now());
    }
}
