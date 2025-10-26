package com.example.tasktube.server.api.configuration;

import com.example.tasktube.server.core.interfaces.ITaskRepository;
import com.example.tasktube.server.core.services.RunTaskService;
import com.example.tasktube.server.core.services.TaskService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class TaskConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public RunTaskService registerRunTaskService(final ITaskRepository repository) {
        return new RunTaskService(repository);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskService registerTaskService(final ITaskRepository repository) {
        return new TaskService(repository);
    }
}
