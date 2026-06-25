package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.queries.repositories.ITaskLogViewRepository;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import com.example.tasktube.server.domain.port.out.ILogRecordRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITaskTubeRepository;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.BarrierMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.LogRecordMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskLogViewMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskTubeMapper;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskViewMapper;
import com.example.tasktube.server.infrastructure.postgresql.repository.BarrierRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.JobRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.LogRecordRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskLogViewRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskTubeRepository;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskViewRepository;
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
    public TaskMapper registerTaskMapper(final ObjectMapper objectMapper) {
        return new TaskMapper(objectMapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubeMapper registerTaskTubeMapper() {
        return new TaskTubeMapper();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public BarrierMapper registerBarrierDataMapper() {
        return new BarrierMapper();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public LogRecordMapper registerLogRecordMapper() {
        return new LogRecordMapper();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskViewMapper registerTaskViewMapper(final ObjectMapper objectMapper) {
        return new TaskViewMapper(objectMapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskLogViewMapper registerTaskLogViewMapper() {
        return new TaskLogViewMapper();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskRepository registerTaskRepository(final NamedParameterJdbcTemplate db, final TaskMapper mapper) {
        return new TaskRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskTubeRepository registerTaskTubeRepository(final NamedParameterJdbcTemplate db, final TaskTubeMapper mapper) {
        return new TaskTubeRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITubeRepository registerTubeRepository(final NamedParameterJdbcTemplate db, final TaskMapper mapper) {
        return new TubeRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IBarrierRepository registerBarrierRepository(final NamedParameterJdbcTemplate db, final BarrierMapper mapper) {
        return new BarrierRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IJobRepository registerJobRepository(final NamedParameterJdbcTemplate db, final BarrierMapper barrierMapper, final TaskMapper taskMapper) {
        return new JobRepository(db, barrierMapper, taskMapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ILogRecordRepository registerLogRecordRepository(final NamedParameterJdbcTemplate db, final LogRecordMapper mapper) {
        return new LogRecordRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskViewRepository registerTaskViewRepository(final NamedParameterJdbcTemplate db, final TaskViewMapper mapper) {
        return new TaskViewRepository(db, mapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskLogViewRepository registerTaskLogViewRepository(final NamedParameterJdbcTemplate db, final TaskLogViewMapper mapper) {
        return new TaskLogViewRepository(db, mapper);
    }
}
