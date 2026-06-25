package com.affordmed.loggingmiddleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LoggingMiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingMiddlewareApplication.class, args);
    }
}
