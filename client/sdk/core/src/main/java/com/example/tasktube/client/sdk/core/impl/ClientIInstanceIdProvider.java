package com.example.tasktube.client.sdk.core.impl;

import com.example.tasktube.client.sdk.core.IInstanceIdProvider;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.UUID;

public class ClientIInstanceIdProvider implements IInstanceIdProvider {
    private final String instanceId = Integer.toHexString(UUID.randomUUID().hashCode());
    private final String client;

    public ClientIInstanceIdProvider(@Nonnull final String client) {
        this.client = Objects.requireNonNull(client);
    }

    @Nonnull
    public String get() {
        return client + "-" + instanceId;
    }
}
