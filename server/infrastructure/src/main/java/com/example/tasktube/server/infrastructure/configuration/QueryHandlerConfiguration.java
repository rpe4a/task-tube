package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.queries.handlers.ParentTasksQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskArgumentsQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskLogsQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTaskQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeQueryHandler;
import com.example.tasktube.server.application.queries.handlers.TaskTubeTreeNodeQueryHandler;
import com.example.tasktube.server.application.queries.repositories.ITaskLogViewRepository;
import com.example.tasktube.server.application.queries.repositories.ITaskViewRepository;
import com.example.tasktube.server.domain.port.out.IArgumentFiller;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
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

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubeQueryHandler registerTaskTubeQueryHandler(final ITaskViewRepository repository) {
        return new TaskTubeQueryHandler(repository);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubeTaskQueryHandler registerTaskTubeHeadQueryHandler(final ITaskViewRepository repository) {
        return new TaskTubeTaskQueryHandler(repository);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskTubeTreeNodeQueryHandler registerTaskTubeTaskChildrenQueryHandler(final ITaskViewRepository repository) {
        return new TaskTubeTreeNodeQueryHandler(repository);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskLogsQueryHandler registerTaskLogsQueryHandler(final ITaskLogViewRepository repository) {
        return new TaskLogsQueryHandler(repository);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskArgumentsQueryHandler registerTaskArgumentsQueryHandler(final ITaskRepository repository, final IArgumentFiller argumentFiller) {
        return new TaskArgumentsQueryHandler(repository, argumentFiller);
    }

}
