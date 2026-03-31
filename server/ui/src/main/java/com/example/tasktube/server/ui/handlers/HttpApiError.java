package com.example.tasktube.server.ui.handlers;

import java.time.Instant;

public final class HttpApiError {

    private final String code;
    private final String message;
    private final int status;
    private final long timestamp;

    public HttpApiError(final String code, final String message, final int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now().toEpochMilli();
    }
}
