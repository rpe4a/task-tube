package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public Optional<Task> get(final UUID id) {
        if (Objects.isNull(id)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        LOGGER.debug("Attempting to get task by ID: '{}'.", id);

        final String queryCommand = """
                    SELECT * FROM tasks
                    WHERE id = :id
                """;

        final ResultSetExtractor<Optional<Task>> rsExtractor = rs -> {
            if (rs.next()) {
                try {
                    final Task task = mapper.getTask(rs);
                    return Optional.of(task);
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
        if (Objects.isNull(taskIdList) || taskIdList.isEmpty()) {
            throw new ApplicationException("Parameter taskIdList cannot be null or empty.");
        }
        LOGGER.debug("Attempting to get '{}' tasks by IDs: '{}'.", taskIdList.size(), taskIdList);

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

        final List<Task> tasks = db.query(queryCommand, Map.of("ids", taskIdList), rsMapper);
        LOGGER.debug("Got '{}' tasks.", tasks.size());

        return tasks;
    }

    @Override
    public void update(final Task task) {
        if (Objects.isNull(task)) {
            throw new ApplicationException("Parameter task cannot be null.");
        }
        LOGGER.debug("Attempting to update task: '{}'.", task);

        final String updateCommand = """
                    UPDATE tasks
                    SET name = :name,
                        tube = :tube,
                        status = :status,
                        parent_id = :parent_id,
                        input = :input::jsonb,
                        output = :output::jsonb,
                        is_root = :is_root,
                        start_barrier = :start_barrier,
                        finish_barrier = :finish_barrier,
                        updated_at = current_timestamp,
                        created_at = :created_at,
                        scheduled_at = :scheduled_at,
                        canceled_at = :canceled_at,
                        started_at = :started_at,
                        heartbeat_at = :heartbeat_at,
                        finished_at = :finished_at,
                        failed_at = :failed_at,
                        aborted_at = :aborted_at,
                        completed_at = :completed_at,
                        failures = :failures,
                        failed_reason = :failed_reason,
                        locked_at = :locked_at,
                        locked = :locked,
                        locked_by = :locked_by,
                        settings = :settings::jsonb
                    WHERE id = :id
                """;

        final int affected = db.update(updateCommand, mapper.getDataDto(task));
        if (affected > 0) {
            LOGGER.info("Task with ID: '{}' updated successfully.", task.getId());
        }
    }
}
