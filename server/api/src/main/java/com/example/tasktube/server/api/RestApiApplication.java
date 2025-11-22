package com.example.tasktube.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.tasktube.server.api",
                "com.example.tasktube.server.infrastructure.configuration"
        }
)
public class RestApiApplication {

    private static final Logger log = LoggerFactory.getLogger(RestApiApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

}
