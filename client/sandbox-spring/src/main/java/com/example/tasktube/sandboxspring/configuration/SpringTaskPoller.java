package com.example.tasktube.sandboxspring.configuration;

import com.example.tasktube.client.sdk.core.poller.TaskTubePoller;
import org.springframework.context.SmartLifecycle;

import java.util.Objects;

public abstract class SpringTaskPoller implements SmartLifecycle {

    private final TaskTubePoller poller;
    private final String tube;
    private volatile boolean running = false;

    public SpringTaskPoller(
            final TaskTubePoller poller,
            final String tube
    ) {
        this.poller = Objects.requireNonNull(poller);
        this.tube = Objects.requireNonNull(tube);
    }

    @Override
    public void start() {
        running = true;
        poller.start(tube);
    }

    @Override
    public void stop() {
        running = false;
        poller.stop();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}