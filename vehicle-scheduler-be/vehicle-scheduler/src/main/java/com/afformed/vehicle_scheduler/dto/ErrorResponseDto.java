package com.afformed.vehicle_scheduler.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String correlationId;
}
