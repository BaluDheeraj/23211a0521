package com.afformed.vehicle_scheduler.client;

import com.afformed.vehicle_scheduler.config.AffordmedApiProperties;
import com.afformed.vehicle_scheduler.dto.DepotApiResponse;
import com.afformed.vehicle_scheduler.dto.DepotDto;
import com.afformed.vehicle_scheduler.dto.VehicleApiResponse;
import com.afformed.vehicle_scheduler.dto.VehicleDto;
import com.afformed.vehicle_scheduler.exception.ConfigurationException;
import com.afformed.vehicle_scheduler.exception.ExternalApiException;
import com.afformed.vehicle_scheduler.exception.ExternalApiTimeoutException;
import com.afformed.vehicle_scheduler.exception.ExternalApiUnauthorizedException;
import com.afformed.vehicle_scheduler.logging.LoggingService;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AffordmedEvaluationClient {

    private static final String SERVICE_NAME = "AffordmedEvaluationApi";

    private final RestTemplate restTemplate;
    private final AffordmedApiProperties apiProperties;
    private final LoggingService loggingService;

    public List<DepotDto> fetchDepots() {
        DepotApiResponse response = exchange(buildEvaluationUrl(apiProperties.getEvaluation().getDepotsPath()),
                DepotApiResponse.class, "fetchDepots");
        return response.getDepots() == null ? Collections.emptyList() : response.getDepots();
    }

    public List<VehicleDto> fetchVehicles() {
        VehicleApiResponse response = exchange(buildEvaluationUrl(apiProperties.getEvaluation().getVehiclesPath()),
                VehicleApiResponse.class, "fetchVehicles");
        return response.getVehicles() == null ? Collections.emptyList() : response.getVehicles();
    }

    private <T> T exchange(String url, Class<T> responseType, String operationName) {
        loggingService.logExternalApiRequest("AffordmedEvaluationClient", operationName, url);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, responseType);
            loggingService.logExternalApiResponse("AffordmedEvaluationClient", operationName, url,
                    response.getStatusCode().value());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalApiException(SERVICE_NAME,
                        "Unexpected status from Affordmed evaluation API: " + response.getStatusCode().value());
            }
            if (response.getBody() == null) {
                throw new ExternalApiException(SERVICE_NAME, "Affordmed evaluation API returned an empty response body.");
            }
            return response.getBody();
        } catch (HttpStatusCodeException exception) {
            loggingService.logError("client", "AffordmedEvaluationClient." + operationName,
                    "Affordmed evaluation API returned an error response.", exception,
                    java.util.Map.of("statusCode", exception.getStatusCode().value(), "url", url));
            if (exception.getStatusCode().value() == 401 || exception.getStatusCode().value() == 403) {
                throw new ExternalApiUnauthorizedException(SERVICE_NAME,
                        "Unauthorized while calling Affordmed evaluation API.", exception);
            }
            throw new ExternalApiException(SERVICE_NAME, "Failed to call Affordmed evaluation API.", exception);
        } catch (ResourceAccessException exception) {
            loggingService.logError("client", "AffordmedEvaluationClient." + operationName,
                    "Affordmed evaluation API request timed out or could not be reached.", exception,
                    java.util.Map.of("url", url));
            if (exception.getCause() instanceof SocketTimeoutException) {
                throw new ExternalApiTimeoutException(SERVICE_NAME,
                        "Timed out while calling Affordmed evaluation API.", exception);
            }
            throw new ExternalApiException(SERVICE_NAME,
                    "Unable to reach the Affordmed evaluation API.", exception);
        } catch (ExternalApiException exception) {
            throw exception;
        } catch (Exception exception) {
            loggingService.logError("client", "AffordmedEvaluationClient." + operationName,
                    "Unexpected error while calling Affordmed evaluation API.", exception,
                    java.util.Map.of("url", url));
            throw new ExternalApiException(SERVICE_NAME, "Unexpected failure while calling Affordmed evaluation API.",
                    exception);
        }
    }

    private String buildEvaluationUrl(String path) {
        if (!StringUtils.hasText(apiProperties.getBaseUrl())) {
            throw new ConfigurationException("Affordmed API base URL is not configured.");
        }

        return UriComponentsBuilder.fromHttpUrl(apiProperties.getBaseUrl())
                .path(path)
                .build()
                .toUriString();
    }
}
