package com.afformed.vehicle_scheduler.exception;

public class ExternalApiTimeoutException extends ExternalApiException {

    public ExternalApiTimeoutException(String serviceName, String message, Throwable cause) {
        super(serviceName, message, cause);
    }
}
