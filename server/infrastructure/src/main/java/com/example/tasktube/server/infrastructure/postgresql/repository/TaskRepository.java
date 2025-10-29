package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskRepository implements ITaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepository.class);

    private final JdbcTemplate db;
    private final TaskDataMapper mapper;

    public TaskRepository(
            final JdbcTemplate db,
            final TaskDataMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public Optional<Task> getById(final UUID id) {
        final String queryCommand = """
                    SELECT * FROM tasks
                    WHERE id = ?
                """;

        final ResultSetExtractor<Optional<Task>> rsExtractor = rs -> {
            if (rs.next()) {
                try {
                    return Optional.of(mapper.getTask(rs));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, rsExtractor, id);
    }

    @Override
    public List<Task> getTasksForScheduling(final String client, final int count) {
        final String queryCommand = """
                    WITH locked_task
                    AS (
                        SELECT id
                        FROM tasks
                        WHERE locked = false
                          AND locked_by is NULL
                          AND locked_at is NULL
                          AND status = 'CREATED'
                        ORDER BY created_at
                            FOR UPDATE SKIP LOCKED
                        LIMIT ?
                    )
                    UPDATE tasks
                    SET locked = true,
                        locked_by = ?,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_task)
                    RETURNING *
                """;

        final RowMapper<Task> rsMapper = (rs, rowNum) -> {
            try {
                return mapper.getTask(rs);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        return db.query(queryCommand, rsMapper, count, client);
    }

    @Override
    public void schedule(final List<Task> tasks) {
        Preconditions.checkNotNull(tasks);
        if (tasks.isEmpty()) {
            return;
        }

        final String updateCommand = """
                            UPDATE tasks
                            SET locked = false,
                                locked_by = null,
                                locked_at = null,
                                updated_at = current_timestamp,
                                scheduled_at = current_timestamp,
                                status = ?
                            WHERE id  = ?
                                AND locked = ?
                                AND locked_by = ?
                """;

        final List<Object[]> batch = new ArrayList<>();
        for (final Task task : tasks) {
            final Object[] values = new Object[]{
                    task.getStatus().name(), task.getId(), task.getLock().isLocked(), task.getLock().getLockedBy()
            };
            batch.add(values);
        }

        db.batchUpdate(updateCommand, batch);
    }
}
