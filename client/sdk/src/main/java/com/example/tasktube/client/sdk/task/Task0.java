package com.example.tasktube.client.sdk.task;

public abstract class Task0<TResult> extends Task<TResult> {

    public abstract Value<TResult> execute() throws Exception;

}
