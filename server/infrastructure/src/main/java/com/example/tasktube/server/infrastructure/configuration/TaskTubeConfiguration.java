package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.port.in.ITaskTubeService;
import com.example.tasktube.server.application.services.TaskTubeService;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import com.example.tasktube.server.domain.port.out.ITaskTubeRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class TaskTubeConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITaskTubeService registerTaskTubeService(
            final ITaskRepository taskRepository,
            final ITaskTubeRepository taskTubeRepository,
            final IEventPublisher eventPublisher
    ) {
        return new TaskTubeService(taskRepository, taskTubeRepository, eventPublisher);
    }
}
