package com.example.tasktube.client.sdk.impl;

import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.dto.PopTaskRequest;
import com.example.tasktube.client.sdk.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.dto.TaskRequest;
import com.example.tasktube.client.sdk.exception.TaskTubeApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class TaskTubeHttpClient implements TaskTubeClient {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final TaskTubeClientSettings settings;

    public TaskTubeHttpClient(
            final ObjectMapper objectMapper,
            final TaskTubeClientSettings settings
    ) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.settings = Objects.requireNonNull(settings);

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(settings.connectionTimeoutSeconds(), ChronoUnit.SECONDS))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public void startTask(final UUID taskId, final StartTaskRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void processTask(final UUID taskId, final ProcessTaskRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void finishTask(final UUID taskId, final FinishTaskRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void failTask(final UUID taskId, final FailTaskRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public UUID pushTask(final String tubeName, final TaskRequest request) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tubeName));
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/tube/%s/push".formatted(tubeName)))
                .POST(getBody(request))
                .build();

        return send(httpRequest, new TypeReference<>() {});
    }

    @Override
    public Optional<PopTaskResponse> popTask(final String tubeName, final PopTaskRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private <T> T send(final HttpRequest httpRequest, final TypeReference<T> typeReference) {
        try {
            final HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new TaskTubeApiException(response.body());
            }

            return objectMapper.readValue(response.body(), typeReference);
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest.Builder getRequestBuilder() {
        return HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json");
    }

    private <T> HttpRequest.BodyPublisher getBody(final T requestBody) {
        try {
            return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getUri(final String url) {
        try {
            return new URI(settings.taskTubeServerApiHost() + url);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
