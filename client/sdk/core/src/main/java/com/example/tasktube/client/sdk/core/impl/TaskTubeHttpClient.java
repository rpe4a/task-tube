package com.example.tasktube.client.sdk.core.impl;

import com.example.tasktube.client.sdk.core.http.ITaskTubeClient;
import com.example.tasktube.client.sdk.core.http.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.core.http.dto.FailTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.FinishTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.PopTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.PopTaskResponse;
import com.example.tasktube.client.sdk.core.http.dto.PopTasksRequest;
import com.example.tasktube.client.sdk.core.http.dto.ProcessTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.StartTaskRequest;
import com.example.tasktube.client.sdk.core.http.dto.TaskRequest;
import com.example.tasktube.client.sdk.core.exception.TaskTubeApiException;
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

public class TaskTubeHttpClient implements ITaskTubeClient {
    public static final int BAD_REQUEST_HTTP_CODE = 400;
    public static final int NO_CONTENT_HTTP_CODE = 204;
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final TaskTubeClientSettings settings;

    public TaskTubeHttpClient(
            @Nonnull final ObjectMapper objectMapper,
            @Nonnull final TaskTubeClientSettings settings
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

    @Nonnull
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

    @Nonnull
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

    @Nonnull
    @Override
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
            if (response.statusCode() >= BAD_REQUEST_HTTP_CODE) {
                throw new TaskTubeApiException(response.body());
            }

            if (response.statusCode() == NO_CONTENT_HTTP_CODE) {
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
