package com.affordmed.loggingmiddleware.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.api")
public class LoggingApiProperties {

    private String baseUrl = "http://4.224.186.213/evaluation-service";
    private String endpoint = "/logs";
    private String bearerToken = "";
    private int connectTimeoutMillis = 5000;
    private int readTimeoutMillis = 10000;
}
