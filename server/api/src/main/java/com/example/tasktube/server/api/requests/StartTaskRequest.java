package com.example.tasktube.server.api.requests;

import java.time.Instant;

public record StartTaskRequest(String client, Instant startedAt){
}
