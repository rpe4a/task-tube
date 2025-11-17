package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.application.port.in.IJobService;
import com.example.tasktube.server.application.services.JobService;
import com.example.tasktube.server.domain.port.out.IJobRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class JobConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IJobService registerJobService(final IJobRepository jobRepository) {
        return new JobService(jobRepository);
    }
}
