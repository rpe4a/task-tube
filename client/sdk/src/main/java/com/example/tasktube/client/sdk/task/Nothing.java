package com.example.tasktube.client.sdk.task;

import java.lang.reflect.Type;

public class Nothing extends Constant<Void> {
    public Nothing() {
        super(null);
    }

    @Override
    public Type getType() {
        return Object.class;
    }
}
