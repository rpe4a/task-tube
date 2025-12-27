package com.example.tasktube.client.sdk.poller;

import com.example.tasktube.client.sdk.task.Task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionTaskFactory implements TaskFactory {
    @Override
    public Task<?> createInstance(final String taskName) {
        try {
            final Class<?> clazz = Class.forName(taskName);
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            return (Task<?>) constructor.newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
