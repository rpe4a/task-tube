package com.example.tasktube.client.sdk.task;

import java.util.List;

public class ValueList<T> implements Value<List<T>> {
    private final List<? extends Value<T>> list;

    public ValueList(final List<? extends Value<T>> list) {
        this.list = List.copyOf(list);
    }

    public List<? extends Value<T>> get() {
        return list;
    }
}
