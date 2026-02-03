package com.example.tasktube.sandboxspring.tube.test;

import com.example.tasktube.client.sdk.core.task.Task0;
import com.example.tasktube.client.sdk.core.task.Value;

public class ChildTaskReturnInteger extends Task0<Integer> {

    @Override
    public Value<Integer> run() throws Exception {
        logger().trace("{} thread", Thread.currentThread().getName());
        logger().debug("{} thread", Thread.currentThread().getName());
        logger().info("{} thread", Thread.currentThread().getName());
        logger().error("{} thread", Thread.currentThread().getName(), new RuntimeException("Test exception."));
        return constant(0);
    }
}
