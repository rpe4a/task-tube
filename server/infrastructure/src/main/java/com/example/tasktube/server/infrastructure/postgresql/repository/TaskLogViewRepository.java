package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.queries.repositories.ITaskLogViewRepository;
import com.example.tasktube.server.application.queries.views.TaskLogView;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskLogViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TaskLogViewRepository implements ITaskLogViewRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLogViewRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final TaskLogViewMapper mapper;

    public TaskLogViewRepository(
            final NamedParameterJdbcTemplate db,
            final TaskLogViewMapper mapper
    ) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public List<TaskLogView> getTaskLogList(final UUID taskId, final int page, final int size) {
        final Map<String, Object> params = new HashMap<>();
        params.put("size", size);
        params.put("skip", page * size);
        params.put("task_id", taskId);

        final String query = """
                        SELECT id,
                               task_id,
                               type,
                               level,
                               timestamp,
                               message,
                               exceptionMessage,
                               exceptionStackTrace,
                               COUNT(*) OVER() AS total_count
                        FROM logs
                        WHERE task_id = :task_id
                        ORDER BY timestamp ASC
                        LIMIT :size OFFSET :skip
                """;

        final RowMapper<TaskLogView> rsMapper = (rs, rowNum) -> mapper.getTaskLogView(rs);

        LOGGER.debug("Lets find logs by params: '{}'.", params);
        final List<TaskLogView> logs = db.query(
                query,
                params,
                rsMapper
        );
        LOGGER.debug("Got '{}' logs.", logs.size());
        return logs;
    }
}
