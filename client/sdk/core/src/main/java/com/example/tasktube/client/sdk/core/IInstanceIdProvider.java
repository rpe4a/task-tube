package com.example.tasktube.client.sdk.core;

import jakarta.annotation.Nonnull;

public interface IInstanceIdProvider {
    @Nonnull
    String get();
}
