package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskDataMapper {
    private final ObjectMapper jackson;

    public TaskDataMapper(final ObjectMapper jackson) {
        this.jackson = Objects.requireNonNull(jackson);
    }

    public Map<String, ?> getDataDto(final Task task) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", task.getId());
        map.put("name", task.getName());
        map.put("tube", task.getTube());
        map.put("status", task.getStatus().name());
        map.put("parent_id", task.getParentId());
        map.put("input", task.getInput() != null ? toJson(task.getInput()) : null);
        map.put("output", task.getOutput() != null ? toJson(task.getOutput()) : null);
        map.put("is_root", task.isRoot());
        map.put("start_barrier", task.getStartBarrier());
        map.put("finish_barrier", task.getFinishBarrier());
        map.put("updated_at", task.getUpdatedAt() != null ? Timestamp.from(task.getUpdatedAt()) : null);
        map.put("created_at", task.getCreatedAt() != null ? Timestamp.from(task.getCreatedAt()) : null);
        map.put("canceled_at", task.getCanceledAt() != null ? Timestamp.from(task.getCanceledAt()) : null);
        map.put("scheduled_at", task.getScheduledAt() != null ? Timestamp.from(task.getScheduledAt()) : null);
        map.put("started_at", task.getStartedAt() != null ? Timestamp.from(task.getStartedAt()) : null);
        map.put("heartbeat_at", task.getHeartbeatAt() != null ? Timestamp.from(task.getHeartbeatAt()) : null);
        map.put("finished_at", task.getFinishedAt() != null ? Timestamp.from(task.getFinishedAt()) : null);
        map.put("failed_at", task.getFailedAt() != null ? Timestamp.from(task.getFailedAt()) : null);
        map.put("aborted_at", task.getAbortedAt() != null ? Timestamp.from(task.getAbortedAt()) : null);
        map.put("completed_at", task.getCompletedAt() != null ? Timestamp.from(task.getCompletedAt()) : null);
        map.put("failures", task.getFailures());
        map.put("failed_reason", task.getFailedReason());
        if (task.getLock() != null) {
            map.put("locked_at", task.getLock().lockedAt() != null ? Timestamp.from(task.getLock().lockedAt()) : null);
            map.put("locked", task.getLock().locked());
            map.put("locked_by", task.getLock().lockedBy());
        } else {
            map.put("locked_at", null);
            map.put("locked", false);
            map.put("locked_by", null);
        }
        map.put("settings", task.getSettings() != null ? toJson(task.getSettings()) : null);

        return map;
    }

    public Task getTask(final ResultSet rs) throws SQLException, JsonProcessingException {
        final Task task = new Task();
        task.setId(rs.getObject("id", UUID.class));
        task.setName(rs.getString("name"));
        task.setTube(rs.getString("tube"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        task.setParentId(rs.getObject("parent_id", UUID.class));
        task.setInput(rs.getString("input") != null
                ? fromJson(rs.getString("input"), new TypeReference<>() {
        })
                : null);
        task.setOutput(rs.getString("output") != null
                ? fromJson(rs.getString("output"), new TypeReference<>() {
        })
                : null);
        task.setRoot(rs.getBoolean("is_root"));
        task.setStartBarrier(rs.getObject("start_barrier", UUID.class));
        task.setFinishBarrier(rs.getObject("finish_barrier", UUID.class));
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
        task.setLock(
                new Lock(
                        rs.getTimestamp("locked_at") != null
                                ? Instant.ofEpochMilli(rs.getTimestamp("locked_at").getTime())
                                : null,
                        rs.getBoolean("locked"),
                        rs.getString("locked_by")
                )
        );
        task.setSettings(fromJson(rs.getString("settings"), new TypeReference<>() {
        }));
        return task;
    }

    private String toJson(final Object obj) {
        try {
            return jackson.writeValueAsString(obj);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJson(final String value, final TypeReference<T> clazz) {
        try {
            return jackson.readValue(value, clazz);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
