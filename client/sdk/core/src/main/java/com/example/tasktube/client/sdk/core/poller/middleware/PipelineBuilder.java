package com.example.tasktube.client.sdk.core.poller.middleware;

import com.example.tasktube.client.sdk.core.task.TaskInput;
import com.example.tasktube.client.sdk.core.task.TaskOutput;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class PipelineBuilder {
    private final List<Function<Pipeline, Pipeline>> middlewares = new LinkedList<>();

    @Nonnull
    public PipelineBuilder add(@Nonnull final Middleware middleware) {
        Objects.requireNonNull(middleware);

        middlewares.add((Pipeline next) -> (TaskInput input, TaskOutput output) -> middleware.invoke(input, output, next));

        return this;
    }

    @Nonnull
    public PipelineBuilder add(@Nonnull final List<Middleware> middlewares) {
        Objects.requireNonNull(middlewares);

        middlewares.stream()
                .sorted(Comparator.comparingInt(
                        m -> Arrays.stream(m.getClass().getAnnotationsByType(Order.class)).findFirst().map(Order::value).orElse(0))
                )
                .forEach(this::add);

        return this;
    }

    @Nonnull
    public Pipeline createInstance(@Nonnull final Pipeline runnable) {
        Pipeline pipeline = Objects.requireNonNull(runnable);

        for (int i = middlewares.size() - 1; i >= 0; i--) {
            pipeline = middlewares.get(i).apply(pipeline);
        }

        return pipeline;
    }


}


