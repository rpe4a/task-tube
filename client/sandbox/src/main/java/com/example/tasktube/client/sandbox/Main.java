package com.example.tasktube.client.sandbox;

import com.example.tasktube.client.sandbox.workflow.ExampleMainTask;
import com.example.tasktube.client.sdk.TaskTubeClient;
import com.example.tasktube.client.sdk.TaskTubeClientSettings;
import com.example.tasktube.client.sdk.impl.TaskTubeHttpClient;
import com.example.tasktube.client.sdk.publisher.TaskTubePublisherFactory;
import com.example.tasktube.client.sdk.slot.SlotValueSerializer;
import com.example.tasktube.client.sdk.task.TaskRecord;
import com.example.tasktube.client.sdk.task.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.UUID;

public class Main {
    public static void main(final String[] args) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final SlotValueSerializer slotValueSerializer = new SlotValueSerializer(objectMapper);
        final TaskTubeClientSettings settings = new TaskTubeClientSettings(30, "http://localhost:8080/");
        final TaskTubeClient taskTubeClient = new TaskTubeHttpClient(objectMapper, settings);
        final TaskTubePublisherFactory publisherFactory = new TaskTubePublisherFactory(taskTubeClient, slotValueSerializer);
//        final TaskTubePublisher publisher = publisherFactory
//                .create(new ExampleMainTask(),"test-task")
//                .settings(TaskSetting.DEFAULT());
//
//        final TaskInfo taskInfo = publisher.pushIn("test-tube");

        final ExampleMainTask exampleMainTask = new ExampleMainTask();
        exampleMainTask.setRecord(
                new TaskRecord.Builder<String>()
                        .setId(UUID.randomUUID())
                        .setName(exampleMainTask.getName())
                        .setTube("tube")
                        .build()
        );
        final Value<String> r = exampleMainTask.run("task");
    }
}