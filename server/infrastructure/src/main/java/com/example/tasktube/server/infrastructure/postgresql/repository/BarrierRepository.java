package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.Barrier;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.BarrierDataMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        this.db = db;
        this.mapper = mapper;
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
        Preconditions.checkNotNull(barrier);

        final String insertCommand = getInsertCommand();

        db.update(insertCommand, mapper.getDataDto(barrier, db.getJdbcOperations()));
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
