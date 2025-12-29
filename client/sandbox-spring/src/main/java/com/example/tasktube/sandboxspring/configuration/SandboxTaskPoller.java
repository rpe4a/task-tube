package com.example.tasktube.sandboxspring.configuration;

import com.example.tasktube.client.sdk.core.poller.TaskTubePoller;
import org.springframework.stereotype.Component;

@Component
public class SandboxTaskPoller extends SpringTaskPoller {

    public SandboxTaskPoller(final TaskTubePoller poller) {
        super(poller, "sandbox-tube");
    }
}
