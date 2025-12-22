package com.example.tasktube.client.sdk;

import java.util.UUID;

public class InstanceIdProviderImpl implements InstanceIdProvider {
    private final String instanceId = Integer.toHexString(UUID.randomUUID().hashCode());
    private final String appName;

    public InstanceIdProviderImpl(final String appName) {
        this.appName = appName;
    }

    public String get() {
        return appName + "-" + instanceId;
    }
}
