package com.afformed.vehicle_scheduler.util;

import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

public final class CorrelationIdUtil {

    public static final String HEADER_NAME = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    private CorrelationIdUtil() {
    }

    public static String getOrCreate(String existingCorrelationId) {
        return StringUtils.hasText(existingCorrelationId) ? existingCorrelationId : UUID.randomUUID().toString();
    }

    public static String currentCorrelationId() {
        return MDC.get(MDC_KEY);
    }
}
