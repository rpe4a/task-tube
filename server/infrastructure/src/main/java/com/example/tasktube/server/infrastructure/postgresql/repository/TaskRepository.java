package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskRepository implements ITaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepository.class);

    private final JdbcTemplate db;
    private final ObjectMapper mapper;

    public TaskRepository(
            final JdbcTemplate db,
            final ObjectMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override

    public Task create(final Task task) {
        Preconditions.checkNotNull(task);

        final String insertCommand = """
                    INSERT INTO tasks (
                        id,
                        name,
                        queue,
                        status,
                        input,
                        is_root,
                        created_at,
                        updated_at,
                        locked_at,
                        locked,
                        locked_by
                    ) VALUES (?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?, ?)
                """;

        db.update(
                insertCommand,
                task.getId(),
                task.getName(),
                task.getQueue(),
                task.getStatus().name(),
                toJson(task.getInput()),
                task.isRoot(),
                Timestamp.from(task.getCreateAt()),
                Timestamp.from(task.getUpdateAt()),
                task.getLockedAt() != null ? Timestamp.from(task.getLockedAt()) : null,
                task.isLocked(),
                task.getLockedBy()
        );

        return task;
    }

    @Override
    public Optional<Task> getById(final UUID id) {
        final String queryCommand = """
                    SELECT * FROM tasks
                    WHERE id = ?
                """;

        final ResultSetExtractor<Optional<Task>> rsExtractor = rs -> {
            if (rs.next()) {
                final Task task = new Task();
                task.setId(rs.getObject("id", UUID.class));
                task.setName(rs.getString("name"));
                task.setQueue(rs.getString("queue"));
                task.setStatus(Task.Status.valueOf(rs.getString("status")));
                task.setInput(fromJson(rs.getString("input"), new TypeReference<>() { }));
                task.setRoot(rs.getBoolean("is_root"));
                task.setCreateAt(Instant.ofEpochMilli(rs.getTimestamp("created_at").getTime()));
                task.setUpdateAt(Instant.ofEpochMilli(rs.getTimestamp("updated_at").getTime()));
                task.setLockedAt(
                        rs.getTimestamp("locked_at") != null
                                ? Instant.ofEpochMilli(rs.getTimestamp("locked_at").getNanos())
                                : null
                );
                task.setRoot(rs.getBoolean("locked"));
                task.setLockedBy(rs.getString("locked_by"));
                return Optional.of(task);
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, rsExtractor, id);
    }

    private String toJson(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJson(final String value, final TypeReference<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
