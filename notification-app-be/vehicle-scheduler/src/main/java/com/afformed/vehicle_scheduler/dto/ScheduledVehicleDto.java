package com.afformed.vehicle_scheduler.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduledVehicleDto {

    private final String taskId;
    private final Integer depotId;
    private final Integer mechanicHoursRequired;
    private final Double operationalImpactScore;

    @Builder.Default
    private final Map<String, Object> sourceAttributes = new LinkedHashMap<>();
}
