package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.port.in.IBarrierService;
import com.example.tasktube.server.application.services.BarrierService;
import com.example.tasktube.server.domain.port.out.IBarrierRepository;
import com.example.tasktube.server.domain.port.out.ITaskRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class BarrierConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IBarrierService registerBarrierService(final IBarrierRepository barrierRepository, final ITaskRepository taskRepository) {
        return new BarrierService(barrierRepository, taskRepository);
    }
}
