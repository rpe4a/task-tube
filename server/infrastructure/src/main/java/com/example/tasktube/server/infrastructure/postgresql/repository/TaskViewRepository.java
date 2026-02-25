package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskViewMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Repository
public class TaskViewRepository implements ITaskViewRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskViewRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final TaskViewMapper mapper;

    public TaskViewRepository(
            final NamedParameterJdbcTemplate db,
            final TaskViewMapper mapper
    ) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public List<ParentTaskView> getParentTaskList(
            @Nullable final UUID taskId,
            @Nullable final String taskName,
            @Nullable final String tube,
            @Nullable final Task.Status status,
            @Nullable final Instant createdFrom,
            @Nullable final Instant createdTo,
            final int page,
            final int size
    ) {
        final Map<String, Object> params = new HashMap<>();
        final StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("""
                        SELECT id,
                               name,
                               tube,
                               status,
                               updated_at,
                               created_at,
                               aborted_at,
                               completed_at,
                               handled_by,
                               COUNT(*) OVER() AS total_count
                        FROM tasks
                        WHERE parent_id IS NULL
                """);
        if (taskId != null) {
            sbQuery.append(" AND id = :id");
            params.put("id", taskId.toString());
        } else {
            if (taskName != null) {
                sbQuery.append(" AND name = :name");
                params.put("name", taskName);
            }
            if (tube != null) {
                sbQuery.append(" AND tube = :tube");
                params.put("tube", tube);
            }
            if (status != null) {
                sbQuery.append(" AND status = :status");
                params.put("status", status.name());
            }
            if (createdFrom != null) {
                sbQuery.append(" AND created_at >= :created_at");
                params.put("created_at", Timestamp.from(createdFrom));
            }
            if (createdTo != null) {
                sbQuery.append(" AND created_at <= :created_at");
                params.put("created_at", Timestamp.from(createdTo));
            }
        }
        sbQuery.append(" ORDER BY created_at DESC");

        sbQuery.append(" LIMIT :size OFFSET :skip");
        params.put("size", size);
        params.put("skip", page * size);


        final RowMapper<ParentTaskView> rsMapper = (rs, rowNum) -> {
            try {
                return mapper.getTask(rs);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };

        LOGGER.debug("Lets find parent tasks by params: '{}'.", params);
        final List<ParentTaskView> tasks = db.query(
                sbQuery.toString(),
                params,
                rsMapper
        );
        LOGGER.debug("Got '{}' tasks.", tasks.size());
        return tasks;
    }
}
