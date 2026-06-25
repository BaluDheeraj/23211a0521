package com.afformed.vehicle_scheduler.client;

import com.afformed.vehicle_scheduler.config.AffordmedApiProperties;
import com.afformed.vehicle_scheduler.dto.MiddlewareLogRequest;
import com.afformed.vehicle_scheduler.exception.ConfigurationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AffordmedLoggingClient {

    private final RestTemplate restTemplate;
    private final AffordmedApiProperties apiProperties;

    public boolean isConfigured() {
        String endpoint = apiProperties.getLogging().getEndpoint();
        if (!StringUtils.hasText(endpoint)) {
            return false;
        }

        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return true;
        }

        return StringUtils.hasText(apiProperties.getBaseUrl());
    }

    public void sendLog(MiddlewareLogRequest request) {
        restTemplate.exchange(
                buildLoggingUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class);
    }

    private String buildLoggingUrl() {
        String endpoint = apiProperties.getLogging().getEndpoint();
        if (!StringUtils.hasText(endpoint)) {
            throw new ConfigurationException("Affordmed logging endpoint is not configured.");
        }

        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return endpoint;
        }

        if (!StringUtils.hasText(apiProperties.getBaseUrl())) {
            throw new ConfigurationException("Affordmed API base URL is not configured.");
        }

        return UriComponentsBuilder.fromHttpUrl(apiProperties.getBaseUrl())
                .path(endpoint)
                .build()
                .toUriString();
    }
}
