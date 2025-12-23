package com.example.tasktube.client.sdk.task;

import jakarta.annotation.Nonnull;

public abstract class Task0<TResult> extends Task<TResult> {

    public abstract Value<TResult> run() throws Exception;

}
