package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.port.in.ITubeService;
import com.example.tasktube.server.application.services.TubeService;
import com.example.tasktube.server.domain.port.out.ITubeRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class TubeConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ITubeService registerTubeService(final ITubeRepository repository) {
        return new TubeService(repository);
    }
}
