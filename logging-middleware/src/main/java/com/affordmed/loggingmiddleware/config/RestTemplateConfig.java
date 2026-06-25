package com.affordmed.loggingmiddleware.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, LoggingApiProperties loggingApiProperties) {
        return restTemplateBuilder
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    requestFactory.setConnectTimeout(loggingApiProperties.getConnectTimeoutMillis());
                    requestFactory.setReadTimeout(loggingApiProperties.getReadTimeoutMillis());
                    return requestFactory;
                })
                .build();
    }
}
