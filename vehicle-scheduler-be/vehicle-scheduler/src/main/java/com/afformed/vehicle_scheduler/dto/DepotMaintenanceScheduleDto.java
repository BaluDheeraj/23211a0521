package com.afformed.vehicle_scheduler.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepotMaintenanceScheduleDto {

    private final Integer depotId;
    private final Integer mechanicHoursLimit;
    private final Integer totalAllocatedMechanicHours;
    private final Double totalOperationalImpactScore;

    @Builder.Default
    private final List<ScheduledVehicleDto> selectedVehicles = new ArrayList<>();
}
