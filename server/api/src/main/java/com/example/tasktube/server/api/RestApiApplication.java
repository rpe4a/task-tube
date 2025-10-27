package com.example.tasktube.server.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.tasktube.server.api",
                "com.example.tasktube.server.infrastructure.configuration"
        }
)
public class RestApiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

}
