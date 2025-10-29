package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TaskDataMapper {
    private final ObjectMapper jackson;

    public TaskDataMapper(final ObjectMapper jackson) {
        this.jackson = Objects.requireNonNull(jackson);
    }

    public List<Object> getArgs(final Task task) {
        return Lists.newArrayList(
                task.getId(),
                task.getName(),
                task.getTube(),
                task.getStatus().name(),
                toJson(task.getInput()),
                task.isRoot(),
                Timestamp.from(task.getUpdatedAt()),
                Timestamp.from(task.getCreatedAt()),
                task.getScheduledAt() != null ? Timestamp.from(task.getScheduledAt()) : null,
                task.getStartedAt() != null ? Timestamp.from(task.getStartedAt()) : null,
                task.getHeartbeatAt() != null ? Timestamp.from(task.getHeartbeatAt()) : null,
                task.getFinishedAt() != null ? Timestamp.from(task.getFinishedAt()) : null,
                task.getLock().getLockedAt() != null ? Timestamp.from(task.getLock().getLockedAt()) : null,
                task.getLock().isLocked(),
                task.getLock().getLockedBy()
        );
    }

    public Task getTask(final ResultSet rs) throws SQLException, JsonProcessingException {
        final Task task = new Task();
        task.setId(rs.getObject("id", UUID.class));
        task.setName(rs.getString("name"));
        task.setTube(rs.getString("tube"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        task.setInput(fromJson(rs.getString("input"), new TypeReference<>() { }));
        task.setRoot(rs.getBoolean("is_root"));
        task.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        task.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        task.setScheduledAt(rs.getTimestamp("scheduled_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("scheduled_at").getTime())
                : null);
        task.setHeartbeatAt(rs.getTimestamp("heartbeat_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("heartbeat_at").getTime())
                : null);
        task.setFinishedAt(rs.getTimestamp("finished_at") != null
                ? Instant.ofEpochMilli(rs.getTimestamp("finished_at").getTime())
                : null);
        task.setLock(
                new Lock(
                        rs.getTimestamp("locked_at") != null
                                ? Instant.ofEpochMilli(rs.getTimestamp("locked_at").getNanos())
                                : null,
                        rs.getBoolean("locked"),
                        rs.getString("locked_by")
                )
        );
        return task;
    }

    private String toJson(final Object obj) {
        try {
            return jackson.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJson(final String value, final TypeReference<T> clazz) {
        try {
            return jackson.readValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
