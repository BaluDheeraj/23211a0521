package com.affordmed.loggingmiddleware.filter;

import com.affordmed.loggingmiddleware.service.CorrelationIdService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CorrelationIdFilter extends OncePerRequestFilter {

    private final CorrelationIdService correlationIdService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String correlationId = correlationIdService.initializeCorrelationId(
                request.getHeader(correlationIdService.getHeaderName()));
        response.setHeader(correlationIdService.getHeaderName(), correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            correlationIdService.clear();
        }
    }
}
