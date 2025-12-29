package com.example.tasktube.sandboxspring.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SandboxRegistrar.class)
public class SandboxConfiguration extends TaskTubeConfiguration {

    @Override
    protected void configureObjectMapper(final ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
