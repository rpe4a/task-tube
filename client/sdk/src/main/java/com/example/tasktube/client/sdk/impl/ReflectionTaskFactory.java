package com.example.tasktube.client.sdk.impl;

import com.example.tasktube.client.sdk.poller.TaskFactory;
import com.example.tasktube.client.sdk.task.Task;
import jakarta.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class ReflectionTaskFactory implements TaskFactory {
    @Nullable
    @Override
    public Task<?> createInstance(@Nullable final String taskName) {
        try {
            final Class<?> clazz = Class.forName(Objects.requireNonNull(taskName));
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            return (Task<?>) constructor.newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
