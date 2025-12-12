package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.filters.RestApiLoggingFilter;
import com.example.tasktube.server.api.requests.FailTaskRequest;
import com.example.tasktube.server.api.requests.FinishTaskRequest;
import com.example.tasktube.server.api.requests.ProcessTaskRequest;
import com.example.tasktube.server.api.requests.StartTaskRequest;
import com.example.tasktube.server.application.port.in.ITaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = TaskController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RestApiLoggingFilter.class})})
class TaskControllerValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ITaskService mockTaskService;

    @Test
    void shouldStartTaskWithNullReturnBadRequest() throws Exception {
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldStartTaskWithNullClientReturnBadRequest() throws Exception {
        final StartTaskRequest request = new StartTaskRequest(null, Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldStartTaskWithEmptyClientReturnBadRequest() throws Exception {
        final StartTaskRequest request = new StartTaskRequest("", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldStartTaskWithNullStartedAtReturnBadRequest() throws Exception {
        final StartTaskRequest request = new StartTaskRequest("test-client", null);
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldStartTaskReturnNoContent() throws Exception {
        final StartTaskRequest request = new StartTaskRequest("test-client", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        Mockito.doNothing().when(mockTaskService).startTask(Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(post("/api/v1/task/" + taskId + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldProcessTaskWithNullReturnBadRequest() throws Exception {
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldProcessTaskWithNullClientReturnBadRequest() throws Exception {
        final ProcessTaskRequest request = new ProcessTaskRequest(null, Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldProcessTaskWithEmptyClientReturnBadRequest() throws Exception {
        final ProcessTaskRequest request = new ProcessTaskRequest("", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldProcessTaskWithNullProcessedAtReturnBadRequest() throws Exception {
        final ProcessTaskRequest request = new ProcessTaskRequest("test-client", null);
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldProcessTaskReturnNoContent() throws Exception {
        final ProcessTaskRequest request = new ProcessTaskRequest("test-client", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        Mockito.doNothing().when(mockTaskService).processTask(Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(post("/api/v1/task/" + taskId + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFinishTaskWithNullReturnBadRequest() throws Exception {
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFinishTaskWithNullClientReturnBadRequest() throws Exception {
        final FinishTaskRequest request = new FinishTaskRequest(null, null, null, Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFinishTaskWithEmptyClientReturnBadRequest() throws Exception {
        final FinishTaskRequest request = new FinishTaskRequest(null, null, "", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFinishTaskWithNullFinishedAtReturnBadRequest() throws Exception {
        final FinishTaskRequest request = new FinishTaskRequest(null, null, "test-client", null);
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFinishTaskReturnNoContent() throws Exception {
        final FinishTaskRequest request = new FinishTaskRequest(null, null, "test-client", Instant.now());
        final String taskId = UUID.randomUUID().toString();

        Mockito.doNothing().when(mockTaskService).finishTask(Mockito.any());

        mockMvc.perform(post("/api/v1/task/" + taskId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailTaskWithNullReturnsBadRequest() throws Exception {
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskWithNullClientReturnsBadRequest() throws Exception {
        final FailTaskRequest request = new FailTaskRequest(null, Instant.now(), "fail");
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskWithEmptyClientReturnsBadRequest() throws Exception {
        final FailTaskRequest request = new FailTaskRequest("", Instant.now(), "fail");
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskWithNullFailedAtReturnsBadRequest() throws Exception {
        final FailTaskRequest request = new FailTaskRequest("test-client", null, "fail");
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskWithNullFailedReasonReturnsBadRequest() throws Exception {
        final FailTaskRequest request = new FailTaskRequest("test-client", Instant.now(), null);
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskWithEmptyFailedReasonReturnsBadRequest() throws Exception {
        final FailTaskRequest request = new FailTaskRequest("test-client", Instant.now(), "");
        final String taskId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailTaskReturnsNoContent() throws Exception {
        final FailTaskRequest request = new FailTaskRequest("test-client", Instant.now(), "fail");
        final String taskId = UUID.randomUUID().toString();

        Mockito.doNothing().when(mockTaskService).failTask(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(post("/api/v1/task/" + taskId + "/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
