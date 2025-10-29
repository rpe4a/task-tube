package com.example.tasktube.server.infrastructure.services;

import java.util.UUID;

public class InstanceIdProvider {
    private final String instanceId = Integer.toHexString(UUID.randomUUID().hashCode());
    private final String appName;

    public InstanceIdProvider(final String appName) {
        this.appName = appName;
    }

    public String get() {
        return appName + "-" + instanceId;
    }
}
