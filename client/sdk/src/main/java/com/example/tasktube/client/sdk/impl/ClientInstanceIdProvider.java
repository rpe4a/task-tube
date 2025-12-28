package com.example.tasktube.client.sdk.impl;

import com.example.tasktube.client.sdk.InstanceIdProvider;

import java.util.UUID;

public class ClientInstanceIdProvider implements InstanceIdProvider {
    private final String instanceId = Integer.toHexString(UUID.randomUUID().hashCode());
    private final String appName;

    public ClientInstanceIdProvider(final String appName) {
        this.appName = appName;
    }

    public String get() {
        return appName + "-" + instanceId;
    }
}
