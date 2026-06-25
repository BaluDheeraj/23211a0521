package com.afformed.vehicle_scheduler.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepotSchedule {

    private final Integer depotId;
    private final Integer mechanicHoursLimit;
    private final Integer totalAllocatedMechanicHours;
    private final Double totalOperationalImpactScore;

    @Builder.Default
    private final List<VehicleCandidate> selectedVehicles = new ArrayList<>();
}
