package com.example.tasktube.server.infrastructure.postgresql.mapper;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.values.Lock;
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
public class TaskMapper {
    private final ObjectMapper mapper;

    public TaskMapper(final ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    public Task invoke(final ResultSet rs) throws SQLException, JsonProcessingException {
        final Task task = new Task();
        task.setId(rs.getObject("id", UUID.class));
        task.setName(rs.getString("name"));
        task.setQueue(rs.getString("queue"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        task.setInput(mapper.readValue(rs.getString("input"), new TypeReference<>() { }));
        task.setRoot(rs.getBoolean("is_root"));
        task.setCreateAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
        task.setUpdateAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
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
}
