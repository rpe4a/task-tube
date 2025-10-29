package com.example.tasktube.server.infrastructure.configuration;

import com.example.tasktube.server.infrastructure.services.InstanceIdProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class ServiceConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public InstanceIdProvider registerInstanceIdProvider(
            @Value("${spring.application.name:app}") final String appName
    ) {
        return new InstanceIdProvider(appName);
    }
}
