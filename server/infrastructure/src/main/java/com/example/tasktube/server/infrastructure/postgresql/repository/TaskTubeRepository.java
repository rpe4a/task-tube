package com.example.tasktube.server.infrastructure.postgresql.repository;

import com.example.tasktube.server.domain.enties.TaskTube;
import com.example.tasktube.server.domain.port.out.ITaskTubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskTubeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public Optional<TaskTube> find(final String correlationId, final UUID taskId) {
        return Optional.empty();
    }

    @Override
    public void create(final TaskTube taskTube) {

    }

    @Override
    public void update(final TaskTube taskTube) {

    }
}