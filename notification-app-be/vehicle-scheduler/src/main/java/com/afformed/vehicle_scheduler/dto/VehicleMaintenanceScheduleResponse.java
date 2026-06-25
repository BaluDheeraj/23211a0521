package com.afformed.vehicle_scheduler.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleMaintenanceScheduleResponse {

    private final Instant generatedAt;

    @Builder.Default
    private final List<DepotMaintenanceScheduleDto> depotSchedules = new ArrayList<>();
}
