package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepository implements ITaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final TaskDataMapper mapper;

    public TaskRepository(
            final NamedParameterJdbcTemplate db,
            final TaskDataMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public Optional<Task> getById(final UUID id) {
        final String queryCommand = """
                    SELECT * FROM tasks
                    WHERE id = :id
                """;

        final ResultSetExtractor<Optional<Task>> rsExtractor = rs -> {
            if (rs.next()) {
                try {
                    return Optional.of(mapper.getTask(rs));
                } catch (final JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, Map.of("id", id), rsExtractor);
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
                        LIMIT :count
                    )
                    UPDATE tasks
                    SET locked = true,
                        locked_by = :locked_by,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_task)
                    RETURNING *
                """;

        final RowMapper<Task> rsMapper = (rs, rowNum) -> {
            try {
                return mapper.getTask(rs);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        return db.query(queryCommand, Map.of("locked_by", client, "count", count), rsMapper);
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
                                status = :status
                            WHERE id  = :id
                                AND locked = :locked
                                AND locked_by = :locked_by
                """;

        final List<Map<String, ?>> batch = new ArrayList<>();
        for (final Task task : tasks) {
            batch.add(mapper.getDataDto(task));
        }

        db.batchUpdate(updateCommand, batch.toArray(new Map[0]));
    }

    @Override
    public void start(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET status = 'PROCESSING',
                        started_at = :started_at
                    WHERE id = :id
                        AND locked_by = :locked_by
                        AND locked = true
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void process(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET status = 'PROCESSING',
                        heartbeat_at = :heartbeat_at
                    WHERE id = :id
                        AND locked_by = :locked_by
                        AND locked = true
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void finish(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET status = 'FINISHED',
                        finished_at = :finished_at,
                        finish_barrier = :finish_barrier,
                        output = :output::jsonb,
                        locked = :locked,
                        locked_at = :locked_at,
                        locked_by = :locked_by
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void fail(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET status = :status,
                        scheduled_at = :scheduled_at,
                        started_at = :started_at,
                        heartbeat_at = :heartbeat_at,
                        finished_at = :finished_at,
                        failed_at = :failed_at,
                        failures = :failures,
                        failed_reason = :failed_reason,
                        aborted_at = :aborted_at,
                        finish_barrier = :finish_barrier,
                        output = :output::jsonb,
                        locked = :locked,
                        locked_at = :locked_at,
                        locked_by = :locked_by
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }
}
