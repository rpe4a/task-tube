package com.example.tasktube.sandboxspring.configuration;

import com.example.tasktube.client.sdk.core.task.ITaskFactory;
import com.example.tasktube.client.sdk.core.task.Task;
import jakarta.annotation.Nonnull;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

public class SpringTaskFactory implements ITaskFactory {

    private final ApplicationContext context;

    public SpringTaskFactory(@Nonnull final ApplicationContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Nonnull
    @Override
    public Task<?> createInstance(@Nonnull final String taskName) {
        return context.getBean(taskName, Task.class);
    }
}
