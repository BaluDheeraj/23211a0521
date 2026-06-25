package com.affordmed.loggingmiddleware.exception;

import com.affordmed.loggingmiddleware.dto.ErrorResponse;
import com.affordmed.loggingmiddleware.service.CorrelationIdService;
import com.affordmed.loggingmiddleware.service.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggingService loggingService;
    private final CorrelationIdService correlationIdService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception,
            HttpServletRequest request) {
        loggingService.log("backend", "error", "handler",
                "Invalid request handled by GlobalExceptionHandler: " + exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        loggingService.log("backend", "error", "handler",
                "Unhandled exception caught by GlobalExceptionHandler: " + exception.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Exception exception,
            HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .correlationId(correlationIdService.getCurrentCorrelationId())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
