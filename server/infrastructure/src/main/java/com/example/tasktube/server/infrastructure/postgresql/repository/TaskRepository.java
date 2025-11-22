package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public Optional<Task> get(final UUID id) {
        Preconditions.checkNotNull(id);

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
    public List<Task> get(final List<UUID> taskIdList) {
        Preconditions.checkNotNull(taskIdList);
        Preconditions.checkArgument(!taskIdList.isEmpty());

        final String queryCommand = """
                    SELECT * FROM tasks
                    WHERE id in (:ids)
                """;

        final RowMapper<Task> rsMapper = (rs, rowNum) -> {
            try {
                return mapper.getTask(rs);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        return db.query(queryCommand, Map.of("ids", taskIdList), rsMapper);
    }

    @Override
    public void schedule(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET locked = :locked,
                        locked_by = :locked_by,
                        locked_at = :locked_at,
                        status = :status,
                        scheduled_at = :scheduled_at,
                        canceled_at = :canceled_at,
                        updated_at = current_timestamp
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void start(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET locked = :locked,
                        locked_by = :locked_by,
                        locked_at = :locked_at,
                        status = :status,
                        started_at = :started_at,
                        updated_at = current_timestamp
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void process(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET locked = :locked,
                        locked_by = :locked_by,
                        locked_at = :locked_at,
                        status = :status,
                        heartbeat_at = :heartbeat_at,
                        updated_at = current_timestamp
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }

    @Override
    public void finish(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET locked = :locked,
                        locked_at = :locked_at,
                        locked_by = :locked_by,
                        status = :status,
                        finished_at = :finished_at,
                        finish_barrier = :finish_barrier,
                        output = :output::jsonb,
                        updated_at = current_timestamp
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
                        updated_at = current_timestamp,
                        scheduled_at = :scheduled_at,
                        started_at = :started_at,
                        heartbeat_at = :heartbeat_at,
                        finished_at = :finished_at,
                        failed_at = :failed_at,
                        failures = :failures,
                        failed_reason = :failed_reason,
                        aborted_at = :aborted_at,
                        completed_at = :completed_at,
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
    public void complete(final Task task) {
        Preconditions.checkNotNull(task);

        final String updateCommand = """
                    UPDATE tasks
                    SET status = :status,
                        completed_at = :completed_at,
                        aborted_at = :aborted_at,
                        locked = false,
                        locked_at = null,
                        locked_by = null
                    WHERE id = :id
                        AND locked_by = :locked_by
                        AND locked = true
                """;

        db.update(updateCommand, mapper.getDataDto(task));
    }
}
