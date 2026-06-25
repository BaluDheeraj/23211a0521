package com.affordmed.loggingmiddleware.service;

import com.affordmed.loggingmiddleware.dto.LogRequest;
import com.affordmed.loggingmiddleware.logging.LoggingInputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoggingService {

    private final LoggingInputValidator loggingInputValidator;
    private final AsyncLogDispatcher asyncLogDispatcher;
    private final CorrelationIdService correlationIdService;

    public void log(String stack, String level, String packageName, String message) {
        loggingInputValidator.validate(stack, level, packageName, message);

        LogRequest request = LogRequest.builder()
                .stack(stack.toLowerCase())
                .level(level.toLowerCase())
                .packageName(packageName.toLowerCase())
                .message(message)
                .build();

        writeLocalLog(request);
        asyncLogDispatcher.dispatch(request);
    }

    private void writeLocalLog(LogRequest request) {
        String correlationId = correlationIdService.getCurrentCorrelationId();
        String logMessage = "correlationId={} stack={} level={} package={} message={}";

        switch (request.getLevel()) {
            case "debug" -> log.debug(logMessage, correlationId, request.getStack(), request.getLevel(),
                    request.getPackageName(), request.getMessage());
            case "info" -> log.info(logMessage, correlationId, request.getStack(), request.getLevel(),
                    request.getPackageName(), request.getMessage());
            case "warn" -> log.warn(logMessage, correlationId, request.getStack(), request.getLevel(),
                    request.getPackageName(), request.getMessage());
            case "error", "fatal" -> log.error(logMessage, correlationId, request.getStack(), request.getLevel(),
                    request.getPackageName(), request.getMessage());
            default -> throw new IllegalArgumentException("Unsupported level: " + request.getLevel());
        }
    }
}
