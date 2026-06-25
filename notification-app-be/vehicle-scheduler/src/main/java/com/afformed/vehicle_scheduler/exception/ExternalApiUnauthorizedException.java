package com.afformed.vehicle_scheduler.exception;

public class ExternalApiUnauthorizedException extends ExternalApiException {

    public ExternalApiUnauthorizedException(String serviceName, String message, Throwable cause) {
        super(serviceName, message, cause);
    }
}
