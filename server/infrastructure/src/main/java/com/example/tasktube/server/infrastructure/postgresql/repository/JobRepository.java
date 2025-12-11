package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Task;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Repository
public class JobRepository implements IJobRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepository.class);

    private final NamedParameterJdbcTemplate db;

    public JobRepository(
            final NamedParameterJdbcTemplate db
    ) {
        this.db = Objects.requireNonNull(db);
    }

    @Override
    public List<UUID> lockBarrierIdList(final int count, final String client) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (Strings.isEmpty(client)) {
            throw new ApplicationException("Parameter client cannot be null or empty.");
        }

        final String queryCommand = """
                    WITH locked_barriers
                    AS (
                        SELECT id
                        FROM barriers
                        WHERE locked = false
                          AND locked_by is NULL
                          AND locked_at is NULL
                          AND released_at is NULL
                          AND released = false
                        ORDER BY updated_at
                            FOR UPDATE SKIP LOCKED
                        LIMIT :count
                    )
                    UPDATE barriers
                    SET locked = true,
                        locked_by = :locked_by,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_barriers)
                    RETURNING id
                """;

        final RowMapper<UUID> rsMapper = (rs, rowNum) -> rs.getObject("id", UUID.class);

        return db.query(queryCommand, Map.of("locked_by", client, "count", count), rsMapper);
    }

    @Override
    public List<UUID> lockTaskIdList(final Task.Status status, final int count, final String client) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
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
                          AND status = :status
                        ORDER BY updated_at
                            FOR UPDATE SKIP LOCKED
                        LIMIT :count
                    )
                    UPDATE tasks
                    SET locked = true,
                        locked_by = :locked_by,
                        locked_at = current_timestamp,
                        updated_at = current_timestamp
                    WHERE id IN (SELECT id FROM locked_task)
                    RETURNING id
                """;

        final RowMapper<UUID> rsMapper = (rs, rowNum) -> rs.getObject("id", UUID.class);

        return db.query(
                queryCommand,
                Map.of(
                        "locked_by", client,
                        "count", count,
                        "status", status.name()
                ),
                rsMapper
        );
    }

    @Override
    public List<UUID> getLockedTaskIdList(final int count, final int lockedTimeoutSeconds) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be more than zero.");
        }

        final String queryCommand = """
                    SELECT id
                        FROM tasks
                        WHERE locked_at <= (current_timestamp - (:lockedTimeoutSeconds * interval '1 second'))
                        ORDER BY locked_at
                        LIMIT :count
                """;

        final RowMapper<UUID> rsMapper = (rs, rowNum) -> rs.getObject("id", UUID.class);

        return db.query(
                queryCommand,
                Map.of(
                        "count", count,
                        "lockedTimeoutSeconds", lockedTimeoutSeconds
                ),
                rsMapper
        );
    }

    @Override
    public List<UUID> getLockedBarrierIdList(final int count, final int lockedTimeoutSeconds) {
        if (count <= 0) {
            throw new ApplicationException("Parameter count must be more than zero.");
        }
        if (lockedTimeoutSeconds <= 0) {
            throw new ApplicationException("Parameter lockedTimeoutSeconds must be more than zero.");
        }

        final String queryCommand = """
                    SELECT id
                        FROM barriers
                        WHERE locked_at <= (current_timestamp - (:lockedTimeoutSeconds * interval '1 second'))
                        ORDER BY locked_at
                        LIMIT :count
                """;

        final RowMapper<UUID> rsMapper = (rs, rowNum) -> rs.getObject("id", UUID.class);

        return db.query(
                queryCommand,
                Map.of(
                        "count", count,
                        "lockedTimeoutSeconds", lockedTimeoutSeconds
                ),
                rsMapper
        );
    }
}
