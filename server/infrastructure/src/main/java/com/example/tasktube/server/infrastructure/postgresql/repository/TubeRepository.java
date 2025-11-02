package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class TubeRepository implements ITubeRepository {
    public static final int TASK_PARTITION_SIZE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(TubeRepository.class);
    private final NamedParameterJdbcTemplate db;
    private final TaskDataMapper mapper;

    public TubeRepository(
            final NamedParameterJdbcTemplate db,
            final TaskDataMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    private static String getInsertCommand() {
        return """
                    INSERT INTO tasks (
                        id,
                        name,
                        tube,
                        status,
                        parent_id,
                        input,
                        output,
                        is_root,
                        start_barrier,
                        finish_barrier,
                        updated_at,
                        created_at,
                        scheduled_at,
                        started_at,
                        heartbeat_at,
                        finished_at,
                        locked_at,
                        locked,
                        locked_by
                    ) VALUES (
                        :id,
                        :name,
                        :tube,
                        :status,
                        :parent_id,
                        :input::jsonb,
                        :output::jsonb,
                        :is_root,
                        :start_barrier,
                        :finish_barrier,
                        :updated_at,
                        :created_at,
                        :scheduled_at,
                        :started_at,
                        :heartbeat_at,
                        :finished_at,
                        :locked_at,
                        :locked,
                        :locked_by
                    )
                """;
    }

    @Override
    public Task push(final Task task) {
        Preconditions.checkNotNull(task);

        final String insertCommand = getInsertCommand();

        db.update(insertCommand, mapper.getDataDto(task));

        return task;
    }

    @Override
    public void push(final List<Task> tasks) {
        Preconditions.checkNotNull(tasks);
        Preconditions.checkArgument(!tasks.isEmpty());
        LOGGER.debug("Tasks count: '{}'.", tasks.size());

        if (tasks.size() == 1) {
            push(tasks.getFirst());
            return;
        }

        final String insertCommand = getInsertCommand();

        final Function<List<Task>, Map[]> insertBatch = tasks1 ->
                tasks1
                        .stream()
                        .map(mapper::getDataDto)
                        .toArray(Map[]::new);

        if (tasks.size() <= TASK_PARTITION_SIZE) {
            db.batchUpdate(insertCommand, insertBatch.apply(tasks));
            return;
        }

        final List<List<Task>> partitions = Lists.partition(tasks, TASK_PARTITION_SIZE);
        partitions.forEach(partition -> db.batchUpdate(insertCommand, insertBatch.apply(partition)));
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
                          AND tube = :tube
                        ORDER BY scheduled_at
                            FOR UPDATE SKIP LOCKED
                        LIMIT 1
                    )
                    UPDATE tasks
                    SET locked = true,
                        locked_by = :locked_by,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_task)
                    RETURNING *
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

        return db.query(queryCommand, Map.of("tube", tube, "locked_by", lockedBy), rsExtractor);
    }
}
