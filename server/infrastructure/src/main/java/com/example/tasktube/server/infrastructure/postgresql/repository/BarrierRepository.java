package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.BarrierDataMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class BarrierRepository implements IBarrierRepository {
    public static final int BARRIER_PARTITION_SIZE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(BarrierRepository.class);
    private final JdbcTemplate db;
    private final BarrierDataMapper mapper;

    public BarrierRepository(
            final JdbcTemplate db,
            final BarrierDataMapper mapper
    ) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public void save(final List<Barrier> barriers) {
        Preconditions.checkNotNull(barriers);
        Preconditions.checkArgument(!barriers.isEmpty());
        LOGGER.debug("Barriers count: '{}'.", barriers.size());


        if (barriers.size() == 1) {
            save(barriers.getFirst());
            return;
        }

        final String insertCommand = getInsertCommand();

        final BiFunction<List<Barrier>, PreparedStatement, Object> insertBatch = (final List<Barrier> localBarriers, final PreparedStatement ps) -> {
            try {
                for (final Barrier barrier : localBarriers) {
                    mapper.fillByDataDto(barrier, ps);
                    ps.addBatch();
                }
                return ps.executeBatch();
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        };

        if (barriers.size() <= BARRIER_PARTITION_SIZE) {
            db.execute(insertCommand, (PreparedStatementCallback<Object>) ps -> insertBatch.apply(barriers, ps));
            return;
        }

        db.execute(insertCommand, (PreparedStatementCallback<Object>) (ps) -> {
            final List<List<Barrier>> partitions = Lists.partition(barriers, BARRIER_PARTITION_SIZE);
            partitions.forEach(partition -> insertBatch.apply(partition, ps));
            return null;
        });
    }

    @Override
    public void save(final Barrier barrier) {
        Preconditions.checkNotNull(barrier);

        final String insertCommand = getInsertCommand();

        db.execute(
                insertCommand,
                (PreparedStatementCallback<Boolean>) ps -> {
                    mapper.fillByDataDto(barrier, ps);
                    return ps.execute();
                }
        );
    }

    private String getInsertCommand() {
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
                        ?, --:id,
                        ?, --:task_id,
                        ?, --:wait_for,
                        ?, --:type,
                        ?, --:released,
                        ?, --:updated_at,
                        ?, --:created_at,
                        ?, --:released_at,
                        ?, --:locked_at,
                        ?, --:locked,
                        ? --:locked_by
                    )
                """;
    }
}
