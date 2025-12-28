package com.example.tasktube.client.sdk.impl;

import com.example.tasktube.client.sdk.http.TaskTubeClient;
import com.example.tasktube.client.sdk.http.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.http.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.http.dto.PopTaskRequest;
import com.example.tasktube.client.sdk.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.http.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.http.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.exception.TaskTubeApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    public void startTask(@Nonnull final UUID taskId, @Nonnull final StartTaskRequest request) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/task/%s/start".formatted(taskId)))
                .POST(getBody(request))
                .build();

        send(httpRequest);
    }

    @Override
    public void processTask(@Nonnull final UUID taskId, @Nonnull final ProcessTaskRequest request) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/task/%s/process".formatted(taskId)))
                .POST(getBody(request))
                .build();

        send(httpRequest);
    }

    @Override
    public void finishTask(@Nonnull final UUID taskId, @Nonnull final FinishTaskRequest request) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/task/%s/finish".formatted(taskId)))
                .POST(getBody(request))
                .build();

        send(httpRequest);
    }

    @Override
    public void failTask(@Nonnull final UUID taskId, @Nonnull final FailTaskRequest request) {
        Preconditions.checkNotNull(taskId);
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/task/%s/fail".formatted(taskId)))
                .POST(getBody(request))
                .build();

        send(httpRequest);
    }

    @Override
    public Optional<UUID> pushTask(@Nonnull final String tubeName, @Nonnull final TaskRequest request) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tubeName));
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/tube/%s/push".formatted(tubeName)))
                .POST(getBody(request))
                .build();

        return send(httpRequest, new TypeReference<>() {});
    }

    @Override
    public Optional<PopTaskResponse> popTask(@Nonnull final String tubeName, @Nonnull final PopTaskRequest request) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tubeName));
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/tube/%s/pop".formatted(tubeName)))
                .POST(getBody(request))
                .build();

        return send(httpRequest, new TypeReference<>() {});
    }

    @Override
    @Nonnull
    public List<PopTaskResponse> popTasks(@Nonnull final String tubeName, @Nonnull final PopTasksRequest request) {
        Preconditions.checkArgument(StringUtils.isNotBlank(tubeName));
        Preconditions.checkNotNull(request);

        final HttpRequest httpRequest = getRequestBuilder()
                .uri(getUri("api/v1/tube/%s/pop/list".formatted(tubeName)))
                .POST(getBody(request))
                .build();

        final Optional<List<PopTaskResponse>> result = send(httpRequest, new TypeReference<>() {});

        return result.orElse(List.of());
    }

    private void send(final HttpRequest httpRequest) {
        send(httpRequest, null);
    }

    private <T> Optional<T> send(final HttpRequest httpRequest, final TypeReference<T> typeReference) {
        try {
            final HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new TaskTubeApiException(response.body());
            }

            if (response.statusCode() == 204) {
                return Optional.empty();
            }

            return Objects.isNull(typeReference)
                    ? Optional.empty()
                    : Optional.of(objectMapper.readValue(response.body(), typeReference));

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
