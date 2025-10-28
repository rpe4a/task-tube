package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.infrastructure.postgresql.mapper.TaskMapper;
import com.example.tasktube.server.infrastructure.postgresql.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration()
public class PostgresqlConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskMapper registerTaskMapper(final ObjectMapper objectMapper) {
        return new TaskMapper(objectMapper);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskRepository registerTaskRepository(final JdbcTemplate db, final ObjectMapper objectMapper, final TaskMapper mapper) {
        return new TaskRepository(db, objectMapper, mapper);
    }
}
