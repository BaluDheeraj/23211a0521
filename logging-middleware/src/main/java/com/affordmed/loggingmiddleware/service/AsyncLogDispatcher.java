package com.affordmed.loggingmiddleware.service;

import com.affordmed.loggingmiddleware.client.LoggingClient;
import com.affordmed.loggingmiddleware.dto.LogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncLogDispatcher {

    private final LoggingClient loggingClient;

    @Async("loggingTaskExecutor")
    public void dispatch(LogRequest request) {
        loggingClient.postLog(request);
    }
}
