package com.example.tasktube.server.workers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.tasktube.server.workers",
                "com.example.tasktube.server.infrastructure.configuration"
        }
)
@EnableScheduling
public class WorkersApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WorkersApplication.class, args);
    }

}
