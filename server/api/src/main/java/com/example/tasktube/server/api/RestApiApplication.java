package com.example.tasktube.server.api;

import com.example.tasktube.server.api.controllers.TubeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.tasktube.server.api",
                "com.example.tasktube.server.infrastructure.configuration"
        }
)
public class RestApiApplication {

    public static void main(final String[] args) {
        final ConfigurableApplicationContext constex = SpringApplication.run(RestApiApplication.class, args);
    }

}
