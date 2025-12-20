package com.example.tasktube.client.sdk.task;

public abstract class Task2<TResult, TArg0, TArg1> extends Task<TResult> {

    public abstract Value<TResult> execute(TArg0 arg0, TArg1 arg1) throws Exception;

}
