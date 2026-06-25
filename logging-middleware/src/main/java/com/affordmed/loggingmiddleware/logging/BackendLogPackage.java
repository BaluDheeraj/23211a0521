package com.affordmed.loggingmiddleware.logging;

import java.util.Arrays;

public enum BackendLogPackage {
    CACHE("cache"),
    CONTROLLER("controller"),
    CRON_JOB("cron_job"),
    DB("db"),
    DOMAIN("domain"),
    HANDLER("handler"),
    REPOSITORY("repository"),
    ROUTE("route"),
    SERVICE("service"),
    AUTH("auth"),
    CONFIG("config"),
    MIDDLEWARE("middleware"),
    UTILS("utils");

    private final String value;

    BackendLogPackage(String value) {
        this.value = value;
    }

    public static boolean isSupported(String candidate) {
        return Arrays.stream(values())
                .anyMatch(value -> value.value.equalsIgnoreCase(candidate));
    }
}
