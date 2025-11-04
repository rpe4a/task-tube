package com.example.tasktube.server.api.requests;

import java.time.Instant;

public record FailTaskRequest(String client, Instant failedAt, String failedReason) {
}
