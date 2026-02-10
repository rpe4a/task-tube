package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.event.handlers.LogRecordHandler;
import com.example.tasktube.server.domain.port.out.IEventHandler;
import com.example.tasktube.server.domain.port.out.IEventPublisher;
import com.example.tasktube.server.domain.port.out.ILogRecordRepository;
import com.example.tasktube.server.infrastructure.event.ApplicationEventPublisher;
import com.example.tasktube.server.infrastructure.event.EventHandlerRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Configuration()
public class EventPublisherConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<IEventHandler<?>> registerEventHandlers(
            final ILogRecordRepository repository
    ) {
        return List.of(
                new LogRecordHandler(repository)
        );
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IEventPublisher registerEventPublisher(
            final EventHandlerRegistry registry
    ) {
        return new ApplicationEventPublisher(registry);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public EventHandlerRegistry registerEventHandlerRegistry(
            final List<IEventHandler<?>> handlers
    ) {
        return new EventHandlerRegistry(handlers);
    }

}
