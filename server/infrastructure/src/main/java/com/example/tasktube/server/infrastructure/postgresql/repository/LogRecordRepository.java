package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.LogRecord;
import com.example.tasktube.server.domain.port.out.ILogRecordRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.LogRecordMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Repository
public class LogRecordRepository implements ILogRecordRepository {

    public static final int LOG_PARTITION_SIZE = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRecordRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final LogRecordMapper mapper;

    public LogRecordRepository(
            final NamedParameterJdbcTemplate db,
            final LogRecordMapper mapper) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    private static String getInsertCommand() {
        return """
                    INSERT INTO logs (
                        id,
                        task_id,
                        type,
                        level,
                        timestamp,
                        message,
                        exceptionMessage,
                        exceptionStackTrace
                    ) VALUES (
                        :id,
                        :task_id,
                        :type,
                        :level,
                        :timestamp,
                        :message,
                        :exceptionMessage,
                        :exceptionStackTrace
                    )
                """;
    }

    @Override
    public void save(final LogRecord log) {
        if (Objects.isNull(log)) {
            throw new ApplicationException("Parameter log cannot be null.");
        }
        LOGGER.debug("Attempting to save log '{}'.", log);

        final String insertCommand = getInsertCommand();

        final int affected = db.update(insertCommand, mapper.getDataDto(log));
        if (affected > 0) {
            LOGGER.debug("Successfully save log with ID: '{}'.", log.getId());
        }
    }

    @Override
    public void save(final List<LogRecord> logs) {
        if (Objects.isNull(logs) || logs.isEmpty()) {
            throw new ApplicationException("Parameter logs cannot be null or empty.");
        }
        LOGGER.debug("Attempting to save list of '{}' logs.", logs.size());

        if (logs.size() == 1) {
            save(logs.getFirst());
            return;
        }

        final String insertCommand = getInsertCommand();

        final Function<List<LogRecord>, Map[]> getLogsBatch = logsBatch ->
                logsBatch
                        .stream()
                        .map(mapper::getDataDto)
                        .toArray(Map[]::new);

        if (logs.size() <= LOG_PARTITION_SIZE) {
            final int[] affected = db.batchUpdate(insertCommand, getLogsBatch.apply(logs));
            LOGGER.debug("Batch pushed '{}' logs. Rows affected: '{}'.", logs.size(), affected.length);
        } else {
            final List<List<LogRecord>> partitions = Lists.partition(logs, LOG_PARTITION_SIZE);
            partitions.forEach(partition -> {
                final int[] affected = db.batchUpdate(insertCommand, getLogsBatch.apply(partition));
                LOGGER.debug("Batch pushed partition of '{}' logs. Rows affected: '{}'.", partition.size(), affected.length);
            });
        }
    }

}
