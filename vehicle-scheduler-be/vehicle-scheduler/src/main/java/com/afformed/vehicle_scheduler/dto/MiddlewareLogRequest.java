package com.afformed.vehicle_scheduler.dto;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MiddlewareLogRequest {

    private final String applicationName;
    private final String correlationId;
    private final String layer;
    private final String eventType;
    private final String action;
    private final String message;
    private final String level;
    private final Instant timestamp;

    @Builder.Default
    private final Map<String, Object> metadata = new LinkedHashMap<>();
}
