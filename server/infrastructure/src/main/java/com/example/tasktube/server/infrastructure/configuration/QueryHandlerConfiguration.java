package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.queries.handlers.ParentTasksQueryHandler;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class QueryHandlerConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ParentTasksQueryHandler registerParentTasksQueryHandler(final ITaskViewRepository repository) {
        return new ParentTasksQueryHandler(repository);
    }

}
