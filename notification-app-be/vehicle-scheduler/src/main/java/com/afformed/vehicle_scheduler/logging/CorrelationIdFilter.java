package com.afformed.vehicle_scheduler.logging;

import com.afformed.vehicle_scheduler.util.CorrelationIdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = CorrelationIdUtil.getOrCreate(request.getHeader(CorrelationIdUtil.HEADER_NAME));
        MDC.put(CorrelationIdUtil.MDC_KEY, correlationId);
        response.setHeader(CorrelationIdUtil.HEADER_NAME, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CorrelationIdUtil.MDC_KEY);
        }
    }
}
