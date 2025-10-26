package com.example.tasktube.server.api.configuration;

import com.example.tasktube.server.core.interfaces.ITaskRepository;
import com.example.tasktube.server.persistance.postgresql.repositories.TaskRepository;
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
    public ITaskRepository registerTaskRepository(final JdbcTemplate db, final ObjectMapper mapper) {
        return new TaskRepository(db, mapper);
    }
}
