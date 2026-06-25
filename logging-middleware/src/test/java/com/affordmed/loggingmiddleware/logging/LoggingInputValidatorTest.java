package com.affordmed.loggingmiddleware.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LoggingInputValidatorTest {

    private final LoggingInputValidator loggingInputValidator = new LoggingInputValidator();

    @Test
    void shouldAllowValidBackendLog() {
        assertDoesNotThrow(() -> loggingInputValidator.validate("backend", "info", "service", "Valid backend log"));
    }

    @Test
    void shouldAllowValidFrontendLog() {
        assertDoesNotThrow(() -> loggingInputValidator.validate("frontend", "debug", "component",
                "Valid frontend log"));
    }

    @Test
    void shouldRejectInvalidBackendPackage() {
        assertThrows(IllegalArgumentException.class,
                () -> loggingInputValidator.validate("backend", "info", "client", "Invalid backend package"));
    }

    @Test
    void shouldRejectInvalidLevel() {
        assertThrows(IllegalArgumentException.class,
                () -> loggingInputValidator.validate("backend", "trace", "service", "Invalid level"));
    }
}
