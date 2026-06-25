package com.affordmed.loggingmiddleware.logging;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LoggingInputValidator {

    public void validate(String stack, String level, String packageName, String message) {
        validateStack(stack);
        validateLevel(level);
        validatePackageName(stack, packageName);
        validateMessage(message);
    }

    private void validateStack(String stack) {
        if (!StringUtils.hasText(stack) || !LogStack.isSupported(stack)) {
            throw new IllegalArgumentException("Invalid stack. Allowed values: backend, frontend.");
        }
    }

    private void validateLevel(String level) {
        if (!StringUtils.hasText(level) || !LogSeverity.isSupported(level)) {
            throw new IllegalArgumentException("Invalid level. Allowed values: debug, info, warn, error, fatal.");
        }
    }

    private void validatePackageName(String stack, String packageName) {
        if (!StringUtils.hasText(packageName)) {
            throw new IllegalArgumentException("Package name must not be blank.");
        }

        if ("backend".equalsIgnoreCase(stack) && !BackendLogPackage.isSupported(packageName)) {
            throw new IllegalArgumentException(
                    "Invalid backend package. Allowed values: cache, controller, cron_job, db, domain, handler, repository, route, service, auth, config, middleware, utils.");
        }
    }

    private void validateMessage(String message) {
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Message must not be blank.");
        }
    }
}
