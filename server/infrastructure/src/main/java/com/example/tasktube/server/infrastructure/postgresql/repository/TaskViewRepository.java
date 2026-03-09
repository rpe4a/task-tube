package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.application.queries.views.ParentTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeTreeNodeView;
import com.example.tasktube.server.application.queries.views.TaskTubeTaskView;
import com.example.tasktube.server.application.queries.views.TaskTubeView;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskViewMapper;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
                               correlation_id,
                               updated_at,
                               created_at,
                               aborted_at,
                               canceled_at,
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
                sbQuery.append(" AND name like '%:name%'");
                params.put("name", taskName);
            }
            if (tube != null) {
                sbQuery.append(" AND tube like '%:tube%'");
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

        final RowMapper<ParentTaskView> rsMapper = (rs, rowNum) -> mapper.getTask(rs);

        LOGGER.debug("Lets find parent tasks by params: '{}'.", params);
        final List<ParentTaskView> tasks = db.query(
                sbQuery.toString(),
                params,
                rsMapper
        );
        LOGGER.debug("Got '{}' tasks.", tasks.size());
        return tasks;
    }

    @Override
    public List<TaskTubeView> getTaskTube(@Nullable final String correlationId) {
        Objects.requireNonNull(correlationId);

        final Map<String, Object> params = Map.of("correlation_id", correlationId);
        final String sbQuery = """
                        SELECT id,
                               name,
                               status,
                               parent_id,
                               created_at,
                               aborted_at,
                               completed_at
                        FROM tasks
                        WHERE correlation_id = :correlation_id
                        ORDER BY created_at
                """;

        final RowMapper<TaskTubeView> rsMapper = (rs, rowNum) -> {
            return mapper.getTaskTube(rs);
        };

        LOGGER.debug("Lets find tasktube by params: '{}'.", params);
        final List<TaskTubeView> tasks = db.query(
                sbQuery,
                params,
                rsMapper
        );
        LOGGER.debug("Got '{}' tasks.", tasks.size());
        return tasks;
    }

    @Override
    public Optional<TaskTubeTaskView> getTaskTubeTask(final String correlationId, @Nullable final UUID taskId) {
        Objects.requireNonNull(taskId);
        Objects.requireNonNull(correlationId);

        final Map<String, Object> params = Map.of("task_id", taskId, "correlation_id", correlationId);
        final String sbQuery = """
                        SELECT t1.id,
                               t1.name,
                               t1.tube,
                               t1.status,
                               t1.correlation_id,
                               t1.parent_id,
                               t1.input,
                               t1.output,
                               t1.is_root,
                               t1.updated_at,
                               t1.created_at,
                               t1.canceled_at,
                               t1.scheduled_at,
                               t1.started_at,
                               t1.heartbeat_at,
                               t1.finished_at,
                               t1.failed_at,
                               t1.aborted_at,
                               t1.completed_at,
                               t1.failures,
                               t1.failed_reason,
                               t1.settings,
                               t1.handled_by,
                               (SELECT COUNT(t2.id)  FROM tasks t2 WHERE t2.parent_id = t1.id) AS count_children
                        FROM tasks t1
                        WHERE t1.id = :task_id AND t1.correlation_id = :correlation_id
                """;

        final ResultSetExtractor<Optional<TaskTubeTaskView>> rsExtractor = rs -> {
            if (rs.next()) {
                final TaskTubeTaskView taskTubeTaskView = mapper.getTaskTubeViewHead(rs);
                return Optional.of(taskTubeTaskView);
            } else {
                return Optional.empty();
            }
        };

        LOGGER.debug("Lets find tasktube head by params: '{}'.", params);
        final Optional<TaskTubeTaskView> task = db.query(
                sbQuery,
                params,
                rsExtractor
        );

        return task;

    }

    @Override
    public List<TaskTubeTreeNodeView> getTaskTubeTreeNode(final String correlationId, final UUID taskId) {
        Objects.requireNonNull(correlationId);
        Objects.requireNonNull(taskId);

        final Map<String, Object> params = Map.of("correlation_id", correlationId, "task_id", taskId);
        final String query = """
                        SELECT t1.id,
                               t1.name,
                               t1.status,
                               t1.parent_id,
                               t1.created_at,
                               t1.scheduled_at,
                               t1.started_at,
                               t1.finished_at,
                               t1.canceled_at,
                               t1.aborted_at,
                               t1.completed_at,
                               (SELECT COUNT(t2.id)  FROM tasks t2 WHERE t2.parent_id = t1.id) AS count_children
                        FROM tasks t1
                        WHERE t1.correlation_id = :correlation_id
                          AND 
                            t1.id = :task_id -- get parent
                            OR 
                            t1.parent_id = :task_id -- get children 
                        ORDER BY t1.created_at
                """;

        final RowMapper<TaskTubeTreeNodeView> rsMapper = (rs, rowNum) -> {
            return mapper.getTaskTubeTreeNode(rs);
        };

        LOGGER.debug("Lets find some tree nodes by params: '{}'.", params);
        final List<TaskTubeTreeNodeView> tasks = db.query(
                query,
                params,
                rsMapper
        );
        LOGGER.debug("Got '{}' nodes.", tasks.size());
        return tasks;

    }
}
