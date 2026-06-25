package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.TaskTube;
import com.example.tasktube.server.domain.port.out.ITaskTubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskTubeMapper;
import org.apache.commons.lang3.StringUtils;
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
public class TaskTubeRepository implements ITaskTubeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTubeRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final TaskTubeMapper mapper;

    public TaskTubeRepository(
            final NamedParameterJdbcTemplate db,
            final TaskTubeMapper mapper
    ) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public Optional<TaskTube> get(final UUID id) {
        if (Objects.isNull(id)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        LOGGER.debug("Attempting to find taskTube by id: '{}'.", id);

        final String queryCommand = """
                    SELECT * FROM tasktubes
                    WHERE id = :id
                """;

        final ResultSetExtractor<Optional<TaskTube>> rsExtractor = rs -> {
            if (rs.next()) {
                return Optional.of(mapper.getTaskTube(rs));
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, Map.of("id", id), rsExtractor);
    }

    @Override
    public Optional<TaskTube> find(final String correlationId, final UUID taskId) {
        if (StringUtils.isEmpty(correlationId)) {
            throw new ApplicationException("Parameter correlationId cannot be null or empty.");
        }
        if (Objects.isNull(taskId)) {
            throw new ApplicationException("Parameter taskId cannot be null.");
        }
        LOGGER.debug("Attempting to find taskTube by correlationId: '{}' and taskId: '{}'.", correlationId, taskId);

        final String queryCommand = """
                    SELECT * FROM tasktubes
                    WHERE correlation_id = :correlation_id 
                      AND task_id = :task_id
                """;

        final ResultSetExtractor<Optional<TaskTube>> rsExtractor = rs -> {
            if (rs.next()) {
                return Optional.of(mapper.getTaskTube(rs));
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, Map.of("correlation_id", correlationId, "task_id", taskId), rsExtractor);
    }

    @Override
    public void create(final TaskTube taskTube) {
        if (Objects.isNull(taskTube)) {
            throw new ApplicationException("Parameter taskTube cannot be null.");
        }
        LOGGER.debug("Attempting to create taskTube: '{}'.", taskTube);

        final String insertCommand = """
                    INSERT INTO tasktubes (
                           id,
                           task_id,
                           correlation_id, 
                           termination_requested,
                           recovery_requested,
                           updated_at, 
                           created_at,
                           locked_at,
                           locked, 
                           locked_by
                    ) VALUES (
                          :id,
                          :task_id, 
                          :correlation_id,
                          :termination_requested,
                          :recovery_requested,
                          :updated_at, 
                          :created_at,
                          :locked_at,
                          :locked, 
                          :locked_by
                    )
                """;

        db.update(insertCommand, mapper.getDataDto(taskTube));
        LOGGER.debug("TaskTube with ID: '{}' created successfully.", taskTube.getId());
    }

    @Override
    public void update(final TaskTube taskTube) {
        if (Objects.isNull(taskTube)) {
            throw new ApplicationException("Parameter taskTube cannot be null.");
        }
        LOGGER.debug("Attempting to update taskTube: '{}'.", taskTube);

        final String updateCommand = """
                    UPDATE tasktubes
                    SET task_id = :task_id,
                        correlation_id = :correlation_id,
                        termination_requested = :termination_requested,
                        recovery_requested = :recovery_requested,
                        updated_at = current_timestamp,
                        created_at = :created_at,
                        locked_at = :locked_at,
                        locked = :locked,
                        locked_by = :locked_by
                    WHERE id = :id
                """;

        final int affected = db.update(updateCommand, mapper.getDataDto(taskTube));
        if (affected > 0) {
            LOGGER.debug("TaskTube with ID: '{}' updated successfully.", taskTube.getId());
        }
    }

    @Override
    public List<UUID> terminate(final TaskTube taskTube, final String client) {
        if (StringUtils.isEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }
        if (Objects.isNull(taskTube)) {
            throw new ApplicationException("Parameter taskTube cannot be null.");
        }
        LOGGER.debug("Attempting to terminate tasks by correlationId: '{}'.", taskTube.getCorrelationId());

        final String queryCommand = """
                    UPDATE tasks
                    SET status = 'TERMINATED',
                        terminated_at = current_timestamp,
                        updated_at = current_timestamp,
                        handled_by = :client,
                        locked = false,
                        locked_by = null,
                        locked_at = null
                    WHERE correlation_id = :correlation_id
                      AND status IN ('SCHEDULED', 'PROCESSING')
                    RETURNING id
                """;

        final RowMapper<UUID> rsMapper = (rs, rowNum) -> rs.getObject("id", UUID.class);

        final List<UUID> terminatedTasks = db.query(
                queryCommand,
                Map.of(
                        "correlation_id", taskTube.getCorrelationId(),
                        "client", client
                ),
                rsMapper
        );
        LOGGER.debug("Got '{}' terminated tasks.", terminatedTasks.size());

        return terminatedTasks;
    }
}