package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.port.in.ITaskService;
import com.example.tasktube.server.application.services.TaskService;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class TaskConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskService registerTaskService(final ITaskRepository repository) {
        return new TaskService(repository);
    }
}
