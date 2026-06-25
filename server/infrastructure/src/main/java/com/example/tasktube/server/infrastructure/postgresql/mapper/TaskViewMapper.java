package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import com.example.tasktube.server.domain.enties.Task;
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

    public ParentTaskView getTask(final ResultSet rs) throws SQLException {
        final ParentTaskView taskView = new ParentTaskView();
        taskView.setId(rs.getObject("id", UUID.class));
        taskView.setName(rs.getString("name"));
        taskView.setTube(rs.getString("tube"));
        taskView.setCorrelationId(rs.getString("correlation_id"));
        taskView.setStatus(Task.Status.valueOf(rs.getString("status")));
        taskView.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        taskView.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        taskView.setAbortedAt(rs.getTimestamp("aborted_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("aborted_at").getTime())
                : null);
        taskView.setCancelledAt(rs.getTimestamp("canceled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("canceled_at").getTime())
                : null);
        taskView.setCompletedAt(rs.getTimestamp("completed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("completed_at").getTime())
                : null);
        taskView.setHandledBy(rs.getString("handled_by"));
        taskView.setTerminationRequested(rs.getBoolean("termination_requested"));
        taskView.setRecoveryRequested(rs.getBoolean("recovery_requested"));
        taskView.setTotalCount(rs.getInt("total_count"));

        return taskView;
    }

    public TaskTubeView getTaskTube(final ResultSet rs) throws SQLException {
        final TaskTubeView taskView = new TaskTubeView();
        taskView.setId(rs.getObject("id", UUID.class));
        taskView.setName(rs.getString("name"));
        taskView.setParentId(rs.getObject("parent_Id", UUID.class));
        taskView.setStatus(Task.Status.valueOf(rs.getString("status")));
        taskView.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        taskView.setAbortedAt(rs.getTimestamp("aborted_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("aborted_at").getTime())
                : null);
        taskView.setCompletedAt(rs.getTimestamp("completed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("completed_at").getTime())
                : null);
        return taskView;
    }

    public TaskTubeTaskView getTaskTubeViewHead(final ResultSet rs) throws SQLException {
        final TaskTubeTaskView task = new TaskTubeTaskView();
        task.setId(rs.getObject("id", UUID.class));
        task.setName(rs.getString("name"));
        task.setTube(rs.getString("tube"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        task.setCorrelationId(rs.getString("correlation_id"));
        task.setParentId(rs.getObject("parent_id", UUID.class));
        task.setInput(rs.getString("input") != null
                ? fromJson(rs.getString("input"), new TypeReference<>() {
        })
                : null);
        task.setOutput(rs.getString("output") != null
                ? fromJson(rs.getString("output"), new TypeReference<>() {
        })
                : null);
        task.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        task.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        task.setCanceledAt(rs.getTimestamp("canceled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("canceled_at").getTime())
                : null);
        task.setScheduledAt(rs.getTimestamp("scheduled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("scheduled_at").getTime())
                : null);
        task.setStartedAt(rs.getTimestamp("started_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("started_at").getTime())
                : null);
        task.setHeartbeatAt(rs.getTimestamp("heartbeat_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("heartbeat_at").getTime())
                : null);
        task.setFinishedAt(rs.getTimestamp("finished_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("finished_at").getTime())
                : null);
        task.setFailedAt(rs.getTimestamp("failed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("failed_at").getTime())
                : null);
        task.setAbortedAt(rs.getTimestamp("aborted_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("aborted_at").getTime())
                : null);
        task.setCompletedAt(rs.getTimestamp("completed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("completed_at").getTime())
                : null);
        task.setFailures(rs.getInt("failures"));
        task.setFailedReason(rs.getString("failed_reason"));
        task.setSettings(fromJson(rs.getString("settings"), new TypeReference<>() {
        }));
        task.setHandledBy(rs.getString("handled_by"));
        task.setCountChildren(rs.getInt("count_children"));

        return task;
    }

    public TaskTubeTreeNodeView getTaskTubeTreeNode(final ResultSet rs) throws SQLException {
        final TaskTubeTreeNodeView taskTubeTreeNodeView = new TaskTubeTreeNodeView();
        taskTubeTreeNodeView.setId(rs.getObject("id", UUID.class));
        taskTubeTreeNodeView.setName(rs.getString("name"));
        taskTubeTreeNodeView.setParentId(rs.getObject("parent_Id", UUID.class));
        taskTubeTreeNodeView.setStatus(Task.Status.valueOf(rs.getString("status")));
        taskTubeTreeNodeView.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        taskTubeTreeNodeView.setScheduledAt(rs.getTimestamp("scheduled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("scheduled_at").getTime())
                : null);
        taskTubeTreeNodeView.setCanceledAt(rs.getTimestamp("canceled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("canceled_at").getTime())
                : null);
        taskTubeTreeNodeView.setStartedAt(rs.getTimestamp("started_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("started_at").getTime())
                : null);
        taskTubeTreeNodeView.setFinishedAt(rs.getTimestamp("finished_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("finished_at").getTime())
                : null);
        taskTubeTreeNodeView.setAbortedAt(rs.getTimestamp("aborted_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("aborted_at").getTime())
                : null);
        taskTubeTreeNodeView.setCompletedAt(rs.getTimestamp("completed_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("completed_at").getTime())
                : null);
        taskTubeTreeNodeView.setChildrenCount(rs.getInt("count_children"));
        return taskTubeTreeNodeView;
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
