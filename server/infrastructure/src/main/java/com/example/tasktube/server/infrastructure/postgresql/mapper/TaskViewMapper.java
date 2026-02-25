package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.domain.enties.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskViewMapper {

    private final ObjectMapper objectMapper;

    public TaskViewMapper(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public ParentTaskView getTask(final ResultSet rs) throws SQLException, JsonProcessingException {
        final ParentTaskView taskView = new ParentTaskView();
        taskView.setId(rs.getObject("id", UUID.class));
        taskView.setName(rs.getString("name"));
        taskView.setTube(rs.getString("tube"));
        taskView.setStatus(Task.Status.valueOf(rs.getString("status")));
        taskView.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        taskView.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        taskView.setAbortedAt(rs.getTimestamp("aborted_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("aborted_at").getTime())
                : null);
        taskView.setCompletedAt(rs.getTimestamp("completed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("completed_at").getTime())
                : null);
        taskView.setHandledBy(rs.getString("handled_by"));
        taskView.setTotalCount(rs.getInt("total_count"));

        return taskView;
    }

    private String toJson(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJson(final String value, final TypeReference<T> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
