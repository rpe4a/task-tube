package com.example.tasktube.client.sdk.core.task;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract non-sealed class Task6<TResult, TArg0, TArg1, TArg2, TArg3, TArg4, TArg5> extends Task<TResult> {

    @Nonnull
    public abstract Value<TResult> run(
            @Nullable TArg0 arg0,
            @Nullable TArg1 arg1,
            @Nullable TArg2 arg2,
            @Nullable TArg3 arg3,
            @Nullable TArg4 arg4,
            @Nullable TArg5 arg5
    ) throws Exception;

}
