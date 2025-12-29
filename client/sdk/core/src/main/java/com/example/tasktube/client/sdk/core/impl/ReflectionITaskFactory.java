package com.example.tasktube.client.sdk.core.impl;

import com.example.tasktube.client.sdk.core.task.ITaskFactory;
import com.example.tasktube.client.sdk.core.task.Task;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class ReflectionITaskFactory implements ITaskFactory {

    @Nonnull
    @Override
    public Task<?> createInstance(@Nonnull final String taskName) {
        try {
            final Class<?> clazz = Class.forName(Objects.requireNonNull(taskName));
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            return (Task<?>) constructor.newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
