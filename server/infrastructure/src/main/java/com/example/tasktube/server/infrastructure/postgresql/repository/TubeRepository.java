package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TubeRepository implements ITubeRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TubeRepository.class);

    private final JdbcTemplate db;
    private final TaskDataMapper mapper;

    public TubeRepository(
            final JdbcTemplate db,
            final TaskDataMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public Task push(final Task task) {
        Preconditions.checkNotNull(task);

        final String insertCommand = """
                    INSERT INTO tasks (
                        id,
                        name,
                        tube,
                        status,
                        input,
                        is_root,
                        updated_at,
                        created_at,
                        scheduled_at,
                        started_at,
                        heartbeat_at,
                        finished_at,
                        locked_at,
                        locked,
                        locked_by
                    ) VALUES (?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        final List<Object> args = mapper.getArgs(task);

        db.update(insertCommand, args.toArray(new Object[0]));

        return task;
    }

    @Override
    public Optional<Task> pop(final String tube, final String lockedBy) {
        final String queryCommand = """
                    WITH locked_task
                    AS (
                        SELECT id
                        FROM tasks
                        WHERE locked = false
                          AND locked_by is NULL
                          AND locked_at is NULL
                          AND status = 'SCHEDULED'
                          AND tube = ?
                        ORDER BY scheduled_at
                            FOR UPDATE SKIP LOCKED
                        LIMIT 1
                    )
                    UPDATE tasks
                    SET locked = true,
                        locked_by = ?,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_task)
                    RETURNING *
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

        return db.query(queryCommand, rsExtractor, tube, lockedBy);
    }
}
