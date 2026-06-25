package com.afformed.vehicle_scheduler.util;

import com.afformed.vehicle_scheduler.config.AffordmedApiProperties;
import com.afformed.vehicle_scheduler.dto.VehicleDto;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class VehicleAttributeResolver {

    private final AffordmedApiProperties apiProperties;

    public Optional<Integer> resolveDepotId(VehicleDto vehicle, Set<Integer> knownDepotIds) {
        Map<String, Object> attributes = vehicle.getAdditionalAttributes();

        Optional<Integer> configuredValue = findIgnoreCase(attributes, apiProperties.getVehicleFieldMapping().getDepotId())
                .flatMap(this::toInteger);
        if (configuredValue.isPresent()) {
            return configuredValue;
        }

        return attributes.entrySet().stream()
                .map(entry -> new MatchCandidate(entry.getKey(), entry.getValue(), scoreDepotField(entry.getKey(), entry.getValue(), knownDepotIds)))
                .filter(candidate -> candidate.score() > 0)
                .sorted(Comparator.comparingInt(MatchCandidate::score).reversed())
                .map(MatchCandidate::value)
                .map(this::toInteger)
                .flatMap(Optional::stream)
                .findFirst();
    }

    public Optional<Integer> resolveMechanicHours(VehicleDto vehicle) {
        Map<String, Object> attributes = vehicle.getAdditionalAttributes();

        Optional<Integer> configuredValue = findIgnoreCase(attributes, apiProperties.getVehicleFieldMapping().getMechanicHours())
                .flatMap(this::toInteger);
        if (configuredValue.isPresent()) {
            return configuredValue;
        }

        return attributes.entrySet().stream()
                .map(entry -> new MatchCandidate(entry.getKey(), entry.getValue(), scoreMechanicHoursField(entry.getKey())))
                .filter(candidate -> candidate.score() > 0)
                .sorted(Comparator.comparingInt(MatchCandidate::score).reversed())
                .map(MatchCandidate::value)
                .map(this::toInteger)
                .flatMap(Optional::stream)
                .findFirst();
    }

    public Optional<Double> resolveOperationalImpactScore(VehicleDto vehicle) {
        Map<String, Object> attributes = vehicle.getAdditionalAttributes();

        Optional<Double> configuredValue = findIgnoreCase(attributes, apiProperties.getVehicleFieldMapping().getOperationalImpactScore())
                .flatMap(this::toDouble);
        if (configuredValue.isPresent()) {
            return configuredValue;
        }

        return attributes.entrySet().stream()
                .map(entry -> new MatchCandidate(entry.getKey(), entry.getValue(), scoreOperationalImpactField(entry.getKey())))
                .filter(candidate -> candidate.score() > 0)
                .sorted(Comparator.comparingInt(MatchCandidate::score).reversed())
                .map(MatchCandidate::value)
                .map(this::toDouble)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<Object> findIgnoreCase(Map<String, Object> attributes, String configuredKey) {
        if (!StringUtils.hasText(configuredKey)) {
            return Optional.empty();
        }

        return attributes.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(configuredKey))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private int scoreDepotField(String fieldName, Object value, Set<Integer> knownDepotIds) {
        Optional<Integer> parsedValue = toInteger(value);
        if (parsedValue.isEmpty()) {
            return 0;
        }

        String normalized = normalize(fieldName);
        if (normalized.contains("depot") && normalized.contains("id")) {
            return 120;
        }
        if (normalized.equals("depot")) {
            return 110;
        }
        if (normalized.contains("assigneddepot")) {
            return 100;
        }
        if (normalized.contains("depot")) {
            return 90;
        }
        if (knownDepotIds.contains(parsedValue.get()) && normalized.equals("id")) {
            return 70;
        }
        if (knownDepotIds.contains(parsedValue.get()) && normalized.endsWith("id") && !normalized.contains("task")
                && !normalized.contains("vehicle")) {
            return 60;
        }
        return 0;
    }

    private int scoreMechanicHoursField(String fieldName) {
        String normalized = normalize(fieldName);
        if (normalized.equals("mechanichours")) {
            return 120;
        }
        if (normalized.equals("maintenancehours")) {
            return 115;
        }
        if (normalized.equals("repairhours")) {
            return 110;
        }
        if (normalized.contains("mechanic") && normalized.contains("hour")) {
            return 100;
        }
        if ((normalized.contains("maintenance") || normalized.contains("repair")) && normalized.contains("hour")) {
            return 95;
        }
        if (normalized.equals("hoursrequired")) {
            return 90;
        }
        if (normalized.equals("hours")) {
            return 60;
        }
        if (normalized.contains("hour")) {
            return 50;
        }
        return 0;
    }

    private int scoreOperationalImpactField(String fieldName) {
        String normalized = normalize(fieldName);
        if (normalized.equals("operationalimpactscore")) {
            return 120;
        }
        if (normalized.equals("impactscore")) {
            return 110;
        }
        if (normalized.contains("operational") && normalized.contains("impact") && normalized.contains("score")) {
            return 100;
        }
        if (normalized.contains("impact") && normalized.contains("score")) {
            return 90;
        }
        if (normalized.equals("operationalimpact")) {
            return 80;
        }
        if (normalized.equals("impact")) {
            return 70;
        }
        if (normalized.contains("impact")) {
            return 60;
        }
        return 0;
    }

    private Optional<Integer> toInteger(Object value) {
        if (value instanceof Number number) {
            return Optional.of(number.intValue());
        }
        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Optional.of(Integer.parseInt(stringValue.trim()));
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<Double> toDouble(Object value) {
        if (value instanceof Number number) {
            return Optional.of(number.doubleValue());
        }
        if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Optional.of(Double.parseDouble(stringValue.trim()));
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private String normalize(String fieldName) {
        return fieldName.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
    }

    private record MatchCandidate(String key, Object value, int score) {
    }
}
