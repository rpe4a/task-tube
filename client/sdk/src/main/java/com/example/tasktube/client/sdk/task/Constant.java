package com.example.tasktube.client.sdk.task;

import java.lang.reflect.Type;

public class Constant<T> implements Value<T> {
    private final T data;

    public Constant(final T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public Type getType() {
        return data.getClass();
    }
}
