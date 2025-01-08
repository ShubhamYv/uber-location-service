package com.locationservice;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.locationservice.utils.LogMessage;


@SpringBootApplication
public class UberLocationServiceApplication {

    private static final Logger LOGGER = LogManager.getLogger(UberLocationServiceApplication.class);

    public static void main(String[] args) {
        LogMessage.setLogMessagePrefix("UberLocationServiceApplication");
        LOGGER.info("Starting Uber Location Service Application...");
        SpringApplication.run(UberLocationServiceApplication.class, args);
        LOGGER.info("Uber Location Service Application started successfully.");
        LogMessage.close();
    }
}
