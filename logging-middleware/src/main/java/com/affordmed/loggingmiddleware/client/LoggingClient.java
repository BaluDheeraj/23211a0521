package com.affordmed.loggingmiddleware.client;

import com.affordmed.loggingmiddleware.config.LoggingApiProperties;
import com.affordmed.loggingmiddleware.dto.LogRequest;
import com.affordmed.loggingmiddleware.dto.LogResponse;
import com.affordmed.loggingmiddleware.service.CorrelationIdService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingClient {

    private final RestTemplate restTemplate;
    private final LoggingApiProperties loggingApiProperties;
    private final CorrelationIdService correlationIdService;

    public Optional<LogResponse> postLog(LogRequest request) {
        try {
            String url = UriComponentsBuilder.fromUriString(loggingApiProperties.getBaseUrl())
                    .path(loggingApiProperties.getEndpoint())
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (StringUtils.hasText(loggingApiProperties.getBearerToken())) {
                headers.setBearerAuth(loggingApiProperties.getBearerToken());
            }

            ResponseEntity<LogResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    LogResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.getBody());
            }

            log.warn("correlationId={} Failed to send log to Affordmed. status={} stack={} level={} package={} message={}",
                    correlationIdService.getCurrentCorrelationId(),
                    response.getStatusCode().value(),
                    request.getStack(),
                    request.getLevel(),
                    request.getPackageName(),
                    request.getMessage());
            return Optional.empty();
        } catch (RestClientResponseException exception) {
            log.warn(
                    "correlationId={} Logging API returned error. status={} stack={} level={} package={} message={} reason={}",
                    correlationIdService.getCurrentCorrelationId(),
                    exception.getStatusCode().value(),
                    request.getStack(),
                    request.getLevel(),
                    request.getPackageName(),
                    request.getMessage(),
                    exception.getResponseBodyAsString());
            return Optional.empty();
        } catch (ResourceAccessException exception) {
            log.warn("correlationId={} Logging API is unavailable. stack={} level={} package={} message={} reason={}",
                    correlationIdService.getCurrentCorrelationId(),
                    request.getStack(),
                    request.getLevel(),
                    request.getPackageName(),
                    request.getMessage(),
                    exception.getMessage());
            return Optional.empty();
        } catch (Exception exception) {
            log.warn("correlationId={} Unexpected logging client failure. stack={} level={} package={} message={} reason={}",
                    correlationIdService.getCurrentCorrelationId(),
                    request.getStack(),
                    request.getLevel(),
                    request.getPackageName(),
                    request.getMessage(),
                    exception.getMessage());
            return Optional.empty();
        }
    }
}
