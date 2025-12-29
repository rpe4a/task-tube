package com.example.tasktube.client.sdk.core;

import jakarta.annotation.Nonnull;

public interface InstanceIdProvider {
    @Nonnull
    String get();
}
