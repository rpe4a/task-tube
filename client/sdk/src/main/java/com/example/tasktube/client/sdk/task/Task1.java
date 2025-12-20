package com.example.tasktube.client.sdk.task;

public abstract class Task1<TResult, TArg0> extends Task<TResult> {

    public abstract Value<TResult> execute(TArg0 arg0) throws Exception;
    
}
