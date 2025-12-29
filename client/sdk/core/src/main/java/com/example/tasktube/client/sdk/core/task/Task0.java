package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;

public abstract non-sealed class Task0<TResult> extends Task<TResult> {

    @Nonnull
    public abstract Value<TResult> run() throws Exception;

}
