package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.BarrierDataMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskDataMapper;
import com.example.tasktube.server.infrastructure.postgresql.repository.BarrierRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.JobRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TubeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration()
public class PostgresqlConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskDataMapper registerTaskMapper(final ObjectMapper objectMapper) {
        return new TaskDataMapper(objectMapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BarrierDataMapper registerBarrierDataMapper() {
        return new BarrierDataMapper();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskRepository registerTaskRepository(final NamedParameterJdbcTemplate db, final TaskDataMapper mapper) {
        return new TaskRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITubeRepository registerTubeRepository(final NamedParameterJdbcTemplate db, final TaskDataMapper mapper) {
        return new TubeRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IBarrierRepository registerBarrierRepository(final NamedParameterJdbcTemplate db, final BarrierDataMapper mapper) {
        return new BarrierRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IJobRepository registerJobRepository(final NamedParameterJdbcTemplate db, final BarrierDataMapper mapper) {
        return new JobRepository(db, mapper);
    }
}
