package com.example.tasktube.client.sdk.task;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract non-sealed class Task1<TResult, TArg0> extends Task<TResult> {

    @Nonnull
    public abstract Value<TResult> run(@Nullable TArg0 arg0) throws Exception;

}
