package com.afformed.vehicle_scheduler.service;

import com.afformed.vehicle_scheduler.client.AffordmedEvaluationClient;
import com.afformed.vehicle_scheduler.dto.DepotDto;
import com.afformed.vehicle_scheduler.dto.DepotMaintenanceScheduleDto;
import com.afformed.vehicle_scheduler.dto.ScheduledVehicleDto;
import com.afformed.vehicle_scheduler.dto.VehicleDto;
import com.afformed.vehicle_scheduler.dto.VehicleMaintenanceScheduleResponse;
import com.afformed.vehicle_scheduler.logging.LoggingService;
import com.afformed.vehicle_scheduler.model.DepotCapacity;
import com.afformed.vehicle_scheduler.model.DepotSchedule;
import com.afformed.vehicle_scheduler.model.VehicleCandidate;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleMaintenanceSchedulerService {

    private final AffordmedEvaluationClient affordmedEvaluationClient;
    private final VehicleCandidateMapper vehicleCandidateMapper;
    private final KnapsackOptimizationService knapsackOptimizationService;
    private final LoggingService loggingService;

    public VehicleMaintenanceScheduleResponse generateOptimalSchedules() {
        loggingService.logAlgorithmExecution("VehicleMaintenanceSchedulerService.generateOptimalSchedules",
                "Starting vehicle maintenance scheduling flow.", Map.of());

        List<DepotCapacity> depots = toDepots(affordmedEvaluationClient.fetchDepots());
        List<VehicleDto> vehicles = affordmedEvaluationClient.fetchVehicles();
        Set<Integer> depotIds = depots.stream()
                .map(DepotCapacity::getId)
                .collect(Collectors.toSet());

        List<VehicleCandidate> candidates = vehicleCandidateMapper.mapVehicles(vehicles, depotIds);
        Map<Integer, List<VehicleCandidate>> candidatesByDepot = candidates.stream()
                .collect(Collectors.groupingBy(VehicleCandidate::getDepotId, LinkedHashMap::new, Collectors.toList()));

        List<DepotMaintenanceScheduleDto> depotSchedules = depots.stream()
                .map(depot -> knapsackOptimizationService.optimize(depot,
                        candidatesByDepot.getOrDefault(depot.getId(), List.of())))
                .map(this::toDepotMaintenanceScheduleDto)
                .toList();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("depotCount", depots.size());
        metadata.put("vehicleCount", vehicles.size());
        metadata.put("candidateCount", candidates.size());
        loggingService.logAlgorithmExecution("VehicleMaintenanceSchedulerService.generateOptimalSchedules",
                "Completed vehicle maintenance scheduling flow.", metadata);

        return VehicleMaintenanceScheduleResponse.builder()
                .generatedAt(Instant.now())
                .depotSchedules(depotSchedules)
                .build();
    }

    private List<DepotCapacity> toDepots(List<DepotDto> depots) {
        List<DepotDto> safeDepots = depots == null ? Collections.emptyList() : depots;
        return safeDepots.stream()
                .filter(depot -> depot.getId() != null && depot.getMechanicHours() != null
                        && depot.getMechanicHours() >= 0)
                .map(depot -> DepotCapacity.builder()
                        .id(depot.getId())
                        .mechanicHoursLimit(depot.getMechanicHours())
                        .build())
                .toList();
    }

    private DepotMaintenanceScheduleDto toDepotMaintenanceScheduleDto(DepotSchedule schedule) {
        return DepotMaintenanceScheduleDto.builder()
                .depotId(schedule.getDepotId())
                .mechanicHoursLimit(schedule.getMechanicHoursLimit())
                .totalAllocatedMechanicHours(schedule.getTotalAllocatedMechanicHours())
                .totalOperationalImpactScore(schedule.getTotalOperationalImpactScore())
                .selectedVehicles(schedule.getSelectedVehicles().stream()
                        .map(this::toScheduledVehicleDto)
                        .toList())
                .build();
    }

    private ScheduledVehicleDto toScheduledVehicleDto(VehicleCandidate candidate) {
        return ScheduledVehicleDto.builder()
                .taskId(candidate.getTaskId())
                .depotId(candidate.getDepotId())
                .mechanicHoursRequired(candidate.getMechanicHoursRequired())
                .operationalImpactScore(candidate.getOperationalImpactScore())
                .sourceAttributes(candidate.getSourceAttributes())
                .build();
    }
}
