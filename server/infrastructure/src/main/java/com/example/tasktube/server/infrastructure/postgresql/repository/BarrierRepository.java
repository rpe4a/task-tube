package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.application.exceptions.ApplicationException;
import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.BarrierDataMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class BarrierRepository implements IBarrierRepository {
    public static final int BARRIER_PARTITION_SIZE = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierRepository.class);

    private final NamedParameterJdbcTemplate db;
    private final BarrierDataMapper mapper;

    public BarrierRepository(
            final NamedParameterJdbcTemplate db,
            final BarrierDataMapper mapper
    ) {
        this.db = Objects.requireNonNull(db);
        this.mapper = Objects.requireNonNull(mapper);
    }

    private static String getInsertCommand() {
        return """
                    INSERT INTO barriers (
                        id,
                        task_id,
                        wait_for,
                        type,
                        released,
                        updated_at,
                        created_at,
                        released_at,
                        locked_at,
                        locked,
                        locked_by
                    ) VALUES (
                        :id,
                        :task_id,
                        :wait_for,
                        :type,
                        :released,
                        :updated_at,
                        :created_at,
                        :released_at,
                        :locked_at,
                        :locked,
                        :locked_by
                    )
                """;
    }

    @Override
    public void save(final Barrier barrier) {
        if (Objects.isNull(barrier)) {
            throw new ApplicationException("Parameter barrier cannot be null.");
        }

        final String insertCommand = getInsertCommand();

        db.update(insertCommand, mapper.getDataDto(barrier, db.getJdbcOperations()));
    }

    @Override
    public Optional<Barrier> get(final UUID barrierId) {
        if (Objects.isNull(barrierId)) {
            throw new ApplicationException("Parameter barrierId cannot be null.");
        }

        final String queryCommand = """
                    SELECT * FROM barriers
                    WHERE id = :id
                """;

        final ResultSetExtractor<Optional<Barrier>> rsExtractor = rs -> {
            if (rs.next()) {
                return Optional.of(mapper.getBarrier(rs));
            } else {
                return Optional.empty();
            }
        };

        return db.query(queryCommand, Map.of("id", barrierId), rsExtractor);
    }

    @Override
    public void update(final Barrier barrier) {
        if (Objects.isNull(barrier)) {
            throw new ApplicationException("parameter barrier cannot be null.");
        }

        final String updateCommand = """
                    UPDATE barriers
                    SET task_id = :task_id,
                        wait_for = :wait_for,
                        type = :type,
                        released = :released,
                        updated_at = current_timestamp,
                        created_at = :created_at,
                        released_at = :released_at,
                        locked = :locked,
                        locked_by = :locked_by,
                        locked_at = :locked_at
                    WHERE id = :id
                """;

        db.update(updateCommand, mapper.getDataDto(barrier, db.getJdbcOperations()));
    }

    @Override
    public void save(final List<Barrier> barriers) {
        if (Objects.isNull(barriers) || barriers.isEmpty()) {
            throw new ApplicationException("Parameter barrier list cannot be null or empty.");
        }
        LOGGER.debug("Barriers count: '{}'.", barriers.size());

        if (barriers.size() == 1) {
            save(barriers.getFirst());
            return;
        }

        final String insertCommand = getInsertCommand();

        final Function<List<Barrier>, Map[]> getBarrierBatch = barriersBatch ->
                barriersBatch
                        .stream()
                        .map(b -> mapper.getDataDto(b, db.getJdbcOperations()))
                        .toArray(Map[]::new);

        if (barriers.size() <= BARRIER_PARTITION_SIZE) {
            db.batchUpdate(insertCommand, getBarrierBatch.apply(barriers));
            return;
        }

        final List<List<Barrier>> partitions = Lists.partition(barriers, BARRIER_PARTITION_SIZE);
        partitions.forEach(partition -> db.batchUpdate(insertCommand, getBarrierBatch.apply(partition)));
    }
}
