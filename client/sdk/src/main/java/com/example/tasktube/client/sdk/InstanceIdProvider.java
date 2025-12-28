package com.example.tasktube.client.sdk;

import jakarta.annotation.Nonnull;

public interface InstanceIdProvider {
    @Nonnull
    String get();
}
