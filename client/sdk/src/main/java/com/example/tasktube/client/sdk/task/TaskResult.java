package com.example.tasktube.client.sdk.task;

import java.util.Objects;
import java.util.UUID;

public class TaskResult<T> implements Value<T> {
    private final UUID id;

    TaskResult(final UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    public UUID getId() {
        return id;
    }
}
