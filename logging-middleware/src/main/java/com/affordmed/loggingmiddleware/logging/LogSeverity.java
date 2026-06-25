package com.affordmed.loggingmiddleware.logging;

import java.util.Arrays;

public enum LogSeverity {
    DEBUG("debug"),
    INFO("info"),
    WARN("warn"),
    ERROR("error"),
    FATAL("fatal");

    private final String value;

    LogSeverity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isSupported(String candidate) {
        return Arrays.stream(values())
                .anyMatch(value -> value.value.equalsIgnoreCase(candidate));
    }
}
