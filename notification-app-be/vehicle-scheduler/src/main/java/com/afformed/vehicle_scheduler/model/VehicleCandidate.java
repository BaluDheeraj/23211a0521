package com.afformed.vehicle_scheduler.model;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleCandidate {

    private final String taskId;
    private final Integer depotId;
    private final Integer mechanicHoursRequired;
    private final Double operationalImpactScore;

    @Builder.Default
    private final Map<String, Object> sourceAttributes = new LinkedHashMap<>();
}
