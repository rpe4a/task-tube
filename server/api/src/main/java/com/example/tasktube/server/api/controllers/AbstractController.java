package com.example.tasktube.server.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

public abstract class AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    boolean isInvalid(final BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(
                    error -> LOGGER.info("Client error: '{}'.", error.getDefaultMessage())
            );
            return true;
        }
        return false;
    }
}
