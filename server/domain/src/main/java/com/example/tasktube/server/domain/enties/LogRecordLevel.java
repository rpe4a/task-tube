package com.example.tasktube.server.domain.enties;

public enum LogRecordLevel {
    ERROR("ERROR"),
    WARN("WARN"),
    INFO("INFO"),
    DEBUG("DEBUG"),
    TRACE("TRACE");

    private final String value;

    LogRecordLevel(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
