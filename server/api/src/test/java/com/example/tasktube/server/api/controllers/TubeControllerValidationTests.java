package com.example.tasktube.server.api.controllers;

import com.example.tasktube.server.api.filters.RestApiLoggingFilter;
import com.example.tasktube.server.api.requests.PopTaskRequest;
import com.example.tasktube.server.api.requests.PopTasksRequest;
import com.example.tasktube.server.api.requests.TaskRequest;
import com.example.tasktube.server.application.models.PopTaskDto;
import com.example.tasktube.server.application.port.in.ITubeService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(
        value = TubeController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RestApiLoggingFilter.class})})
class TubeControllerValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ITubeService mockTubeService;

    @Test
    void shouldPushWithNullReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithNullIdReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                null,
                "test-task",
                "test-tube",
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithNullNameReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                null,
                "test-tube",
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithBlankNameReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "",
                "test-tube",
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithNullTubeReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                null,
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithBlankTubeReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                "",
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithNullCorrelationIdReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                "test-tube",
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithBlankCorrelationIdReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                "test-tube",
                "",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithNullCreatedAtReturnBadRequest() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                "test-tube",
                "correlationId",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPushWithTaskReturnOk() throws Exception {
        final TaskRequest request = new TaskRequest(
                UUID.randomUUID(),
                "test-task",
                "test-tube",
                "correlationId",
                null,
                null,
                Instant.now(),
                null
        );

        Mockito.when(mockTubeService.push(Mockito.any()))
                .thenReturn(UUID.randomUUID());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldPopWithNullReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPopWithNullClientReturnBadRequest() throws Exception {
        final PopTaskRequest request = new PopTaskRequest(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPopWithClientReturnNoContent() throws Exception {
        final PopTaskRequest request = new PopTaskRequest("client");

        Mockito.when(mockTubeService.pop(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldPopWithClientReturnOk() throws Exception {
        final PopTaskRequest request = new PopTaskRequest("client");

        Mockito.when(mockTubeService.pop(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(new PopTaskDto(UUID.randomUUID(), "", "", "", null)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldPopListWithNullReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPopListWithNullClientReturnBadRequest() throws Exception {
        final PopTasksRequest request = new PopTasksRequest(null, 1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPopListWithBadCountReturnBadRequest() throws Exception {
        final PopTasksRequest request = new PopTasksRequest("client", 0);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldPopListWithClientReturnNoContent() throws Exception {
        final PopTasksRequest request = new PopTasksRequest("client", 10);

        Mockito.when(mockTubeService.popList(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void shouldPopListWithClientReturnOk() throws Exception {
        final PopTasksRequest request = new PopTasksRequest("client", 10);

        Mockito.when(mockTubeService.popList(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(List.of(new PopTaskDto(UUID.randomUUID(), "", "", "", null)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tube/test-tube/pop/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
