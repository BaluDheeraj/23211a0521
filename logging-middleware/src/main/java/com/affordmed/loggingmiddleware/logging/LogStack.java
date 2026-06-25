package com.affordmed.loggingmiddleware.logging;

import java.util.Arrays;

public enum LogStack {
    BACKEND("backend"),
    FRONTEND("frontend");

    private final String value;

    LogStack(String value) {
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
