package com.afformed.vehicle_scheduler.exception;

import com.afformed.vehicle_scheduler.dto.ErrorResponseDto;
import com.afformed.vehicle_scheduler.logging.LoggingService;
import com.afformed.vehicle_scheduler.util.CorrelationIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggingService loggingService;

    @ExceptionHandler(ExternalApiUnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(ExternalApiUnauthorizedException exception,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception, request);
    }

    @ExceptionHandler(ExternalApiTimeoutException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeout(ExternalApiTimeoutException exception,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, exception, request);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponseDto> handleExternalApiFailure(ExternalApiException exception,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, exception, request);
    }

    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<ErrorResponseDto> handleConfiguration(ConfigurationException exception,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, request);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, Exception exception,
            HttpServletRequest request) {
        loggingService.logError("exception", "GlobalExceptionHandler.buildErrorResponse",
                "Request handling failed.", exception,
                Map.of("path", request.getRequestURI(), "status", status.value()));

        ErrorResponseDto response = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .correlationId(CorrelationIdUtil.currentCorrelationId())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
