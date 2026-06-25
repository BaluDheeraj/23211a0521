package com.afformed.vehicle_scheduler.controller;

import com.afformed.vehicle_scheduler.dto.VehicleMaintenanceScheduleResponse;
import com.afformed.vehicle_scheduler.logging.LoggingService;
import com.afformed.vehicle_scheduler.service.VehicleMaintenanceSchedulerService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/maintenance-schedules")
@RequiredArgsConstructor
public class VehicleMaintenanceSchedulerController {

    private final VehicleMaintenanceSchedulerService vehicleMaintenanceSchedulerService;
    private final LoggingService loggingService;

    @GetMapping("/optimal")
    public ResponseEntity<VehicleMaintenanceScheduleResponse> getOptimalSchedules() {
        loggingService.logControllerEntry("VehicleMaintenanceSchedulerController", "getOptimalSchedules");
        VehicleMaintenanceScheduleResponse response = vehicleMaintenanceSchedulerService.generateOptimalSchedules();
        loggingService.logControllerExit("VehicleMaintenanceSchedulerController", "getOptimalSchedules",
                Map.of("depotCount", response.getDepotSchedules().size()));
        return ResponseEntity.ok(response);
    }
}
