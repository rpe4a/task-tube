package com.example.tasktube.server.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.tasktube.server.ui",
                "com.example.tasktube.server.infrastructure.configuration"
        }
)
public class UiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

}
