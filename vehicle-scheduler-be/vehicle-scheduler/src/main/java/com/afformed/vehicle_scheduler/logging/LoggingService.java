package com.afformed.vehicle_scheduler.logging;

import com.afformed.vehicle_scheduler.client.AffordmedLoggingClient;
import com.afformed.vehicle_scheduler.dto.MiddlewareLogRequest;
import com.afformed.vehicle_scheduler.util.CorrelationIdUtil;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoggingService {

    private final AffordmedLoggingClient affordmedLoggingClient;
    private final String applicationName;

    public LoggingService(AffordmedLoggingClient affordmedLoggingClient,
            @Value("${spring.application.name}") String applicationName) {
        this.affordmedLoggingClient = affordmedLoggingClient;
        this.applicationName = applicationName;
    }

    public void logControllerEntry(String controllerName, String methodName) {
        log(
                LogLevel.INFO,
                "controller",
                LogEventType.CONTROLLER_ENTRY,
                controllerName + "." + methodName,
                "Controller entry",
                Map.of());
    }

    public void logControllerExit(String controllerName, String methodName, Map<String, Object> metadata) {
        log(
                LogLevel.INFO,
                "controller",
                LogEventType.CONTROLLER_EXIT,
                controllerName + "." + methodName,
                "Controller exit",
                metadata);
    }

    public void logExternalApiRequest(String clientName, String operationName, String url) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("url", url);
        log(
                LogLevel.INFO,
                "client",
                LogEventType.EXTERNAL_API_REQUEST,
                clientName + "." + operationName,
                "External API request",
                metadata);
    }

    public void logExternalApiResponse(String clientName, String operationName, String url, int statusCode) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("url", url);
        metadata.put("statusCode", statusCode);
        log(
                LogLevel.INFO,
                "client",
                LogEventType.EXTERNAL_API_RESPONSE,
                clientName + "." + operationName,
                "External API response",
                metadata);
    }

    public void logAlgorithmExecution(String action, String message, Map<String, Object> metadata) {
        log(
                LogLevel.INFO,
                "service",
                LogEventType.ALGORITHM_EXECUTION,
                action,
                message,
                metadata);
    }

    public void logError(String layer, String action, String message, Throwable throwable, Map<String, Object> metadata) {
        Map<String, Object> mergedMetadata = new LinkedHashMap<>(safeMetadata(metadata));
        if (throwable != null) {
            mergedMetadata.put("exceptionType", throwable.getClass().getSimpleName());
            mergedMetadata.put("exceptionMessage", throwable.getMessage());
        }

        log(
                LogLevel.ERROR,
                layer,
                throwable == null ? LogEventType.ERROR : LogEventType.EXCEPTION,
                action,
                message,
                mergedMetadata);
    }

    public void log(LogLevel level, String layer, LogEventType eventType, String action, String message,
            Map<String, Object> metadata) {
        MiddlewareLogRequest request = MiddlewareLogRequest.builder()
                .applicationName(applicationName)
                .correlationId(CorrelationIdUtil.currentCorrelationId())
                .layer(layer)
                .eventType(eventType.name())
                .action(action)
                .message(message)
                .level(level.name())
                .timestamp(Instant.now())
                .metadata(new LinkedHashMap<>(safeMetadata(metadata)))
                .build();

        writeLocal(level, request);
        forwardToMiddleware(request);
    }

    private void forwardToMiddleware(MiddlewareLogRequest request) {
        if (!affordmedLoggingClient.isConfigured()) {
            return;
        }

        try {
            affordmedLoggingClient.sendLog(request);
        } catch (Exception exception) {
            log.warn("Failed to forward log to Affordmed middleware. correlationId={} action={} reason={}",
                    request.getCorrelationId(), request.getAction(), exception.getMessage());
        }
    }

    private void writeLocal(LogLevel level, MiddlewareLogRequest request) {
        String logMessage = "layer={} eventType={} action={} correlationId={} message={} metadata={}";
        switch (level) {
            case ERROR -> log.error(logMessage, request.getLayer(), request.getEventType(), request.getAction(),
                    request.getCorrelationId(), request.getMessage(), request.getMetadata());
            case WARN -> log.warn(logMessage, request.getLayer(), request.getEventType(), request.getAction(),
                    request.getCorrelationId(), request.getMessage(), request.getMetadata());
            case INFO -> log.info(logMessage, request.getLayer(), request.getEventType(), request.getAction(),
                    request.getCorrelationId(), request.getMessage(), request.getMetadata());
        }
    }

    private Map<String, Object> safeMetadata(Map<String, Object> metadata) {
        return metadata == null ? Map.of() : metadata;
    }
}
