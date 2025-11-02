package com.example.tasktube.server.api.requests;

import java.time.Instant;

public record ProcessTaskRequest(String client, Instant processedAt){
}
