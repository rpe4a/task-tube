package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class TubeRepository implements ITubeRepository {
    public static final int TASK_PARTITION_SIZE = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(TubeRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final TaskDataMapper mapper;

    public TubeRepository(
            final NamedParameterJdbcTemplate db,
            final TaskDataMapper mapper
    ) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
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
                        canceled_at,
                        started_at,
                        heartbeat_at,
                        finished_at,
                        failed_at,
                        aborted_at,
                        completed_at,
                        failures,
                        failed_reason,
                        locked_at,
                        locked,
                        locked_by,
                        settings
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
                        :canceled_at,
                        :started_at,
                        :heartbeat_at,
                        :finished_at,
                        :failed_at,
                        :aborted_at,
                        :completed_at,
                        :failures,
                        :failed_reason,
                        :locked_at,
                        :locked,
                        :locked_by,
                        :settings::jsonb
                    )
                """;
    }

    @Override
    public Task push(final Task task) {
        if (Objects.isNull(task)) {
            throw new ApplicationException("Parameter task cannot be null.");
        }

        final String insertCommand = getInsertCommand();

        db.update(insertCommand, mapper.getDataDto(task));

        return task;
    }

    @Override
    public void push(final List<Task> tasks) {
        if (Objects.isNull(tasks) || tasks.isEmpty()) {
            throw new ApplicationException("Parameter tasks cannot be null or empty.");
        }
        LOGGER.debug("Tasks count: '{}'.", tasks.size());

        if (tasks.size() == 1) {
            push(tasks.getFirst());
            return;
        }

        final String insertCommand = getInsertCommand();

        final Function<List<Task>, Map[]> getTasksBatch = tasksBatch ->
                tasksBatch
                        .stream()
                        .map(mapper::getDataDto)
                        .toArray(Map[]::new);

        if (tasks.size() <= TASK_PARTITION_SIZE) {
            db.batchUpdate(insertCommand, getTasksBatch.apply(tasks));
            return;
        }

        final List<List<Task>> partitions = Lists.partition(tasks, TASK_PARTITION_SIZE);
        partitions.forEach(partition -> db.batchUpdate(insertCommand, getTasksBatch.apply(partition)));
    }

    @Override
    public Optional<Task> pop(final String tube, final String client) {
        if (Strings.isEmpty(tube)) {
            throw new ApplicationException("Paramter tube cannot be null or empty.");
        }
        if (Strings.isEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }

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
                          AND scheduled_at <= current_timestamp
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

        return db.query(queryCommand, Map.of("tube", tube, "locked_by", client), rsExtractor);
    }
}
