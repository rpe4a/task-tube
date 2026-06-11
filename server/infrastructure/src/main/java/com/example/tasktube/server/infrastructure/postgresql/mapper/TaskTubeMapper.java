package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.TaskTube;
import com.example.tasktube.server.domain.values.Lock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskTubeMapper {

    public TaskTubeMapper() {
    }

    public Map<String, ?> getDataDto(final TaskTube taskTube) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", taskTube.getId());
        map.put("task_id", taskTube.getTaskId());
        map.put("correlation_id", taskTube.getCorrelationId());
        map.put("updated_at", taskTube.getUpdatedAt() != null ? Timestamp.from(taskTube.getUpdatedAt()) : null);
        map.put("created_at", taskTube.getCreatedAt() != null ? Timestamp.from(taskTube.getCreatedAt()) : null);
        map.put("termination_requested", taskTube.isTerminationRequested());
        map.put("recovery_requested", taskTube.isRecoveryRequested());
        if (taskTube.getLock() != null) {
            map.put("locked_at", taskTube.getLock().lockedAt() != null ? Timestamp.from(taskTube.getLock().lockedAt()) : null);
            map.put("locked", taskTube.getLock().locked());
            map.put("locked_by", taskTube.getLock().lockedBy());
        } else {
            map.put("locked_at", null);
            map.put("locked", false);
            map.put("locked_by", null);
        }
        return map;
    }

    public TaskTube getTaskTube(final ResultSet rs) throws SQLException {
        final TaskTube taskTube = new TaskTube();
        taskTube.setId(rs.getObject("id", UUID.class));
        taskTube.setTaskId(rs.getObject("task_id", UUID.class));
        taskTube.setCorrelationId(rs.getString("correlation_id"));
        taskTube.setUpdatedAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
        taskTube.setCreatedAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        taskTube.setTerminationRequested(rs.getBoolean("termination_requested"));
        taskTube.setRecoveryRequested(rs.getBoolean("recovery_requested"));
        taskTube.setLock(
                new Lock(
                        rs.getTimestamp("locked_at") != null
                                ? Instant.ofEpochMilli(rs.getTimestamp("locked_at").getTime())
                                : null,
                        rs.getBoolean("locked"),
                        rs.getString("locked_by")
                )
        );
        return taskTube;
    }

}