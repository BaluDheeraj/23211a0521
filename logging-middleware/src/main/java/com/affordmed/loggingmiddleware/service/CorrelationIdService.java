package com.affordmed.loggingmiddleware.service;

import java.util.UUID;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CorrelationIdService {

    @Getter
    private final String headerName = "X-Correlation-Id";

    private final String mdcKey = "correlationId";

    public String initializeCorrelationId(String incomingCorrelationId) {
        String correlationId = StringUtils.hasText(incomingCorrelationId)
                ? incomingCorrelationId
                : UUID.randomUUID().toString();
        MDC.put(mdcKey, correlationId);
        return correlationId;
    }

    public String getCurrentCorrelationId() {
        String correlationId = MDC.get(mdcKey);
        return StringUtils.hasText(correlationId) ? correlationId : "N/A";
    }

    public void clear() {
        MDC.remove(mdcKey);
    }
}
