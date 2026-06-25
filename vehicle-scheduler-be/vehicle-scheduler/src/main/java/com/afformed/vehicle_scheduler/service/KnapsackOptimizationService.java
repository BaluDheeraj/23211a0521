package com.afformed.vehicle_scheduler.service;

import com.afformed.vehicle_scheduler.logging.LoggingService;
import com.afformed.vehicle_scheduler.model.DepotCapacity;
import com.afformed.vehicle_scheduler.model.DepotSchedule;
import com.afformed.vehicle_scheduler.model.VehicleCandidate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KnapsackOptimizationService {

    private final LoggingService loggingService;

    public DepotSchedule optimize(DepotCapacity depot, List<VehicleCandidate> vehicles) {
        Map<String, Object> startMetadata = new LinkedHashMap<>();
        startMetadata.put("depotId", depot.getId());
        startMetadata.put("capacity", depot.getMechanicHoursLimit());
        startMetadata.put("vehicleCount", vehicles.size());
        loggingService.logAlgorithmExecution("KnapsackOptimizationService.optimize",
                "Starting knapsack optimization for depot.", startMetadata);

        int capacity = Math.max(depot.getMechanicHoursLimit(), 0);
        int vehicleCount = vehicles.size();
        double[][] dp = new double[vehicleCount + 1][capacity + 1];

        for (int vehicleIndex = 1; vehicleIndex <= vehicleCount; vehicleIndex++) {
            VehicleCandidate candidate = vehicles.get(vehicleIndex - 1);
            int requiredHours = candidate.getMechanicHoursRequired();
            for (int availableHours = 0; availableHours <= capacity; availableHours++) {
                dp[vehicleIndex][availableHours] = dp[vehicleIndex - 1][availableHours];
                if (requiredHours <= availableHours) {
                    double selectedScore = dp[vehicleIndex - 1][availableHours - requiredHours]
                            + candidate.getOperationalImpactScore();
                    if (selectedScore > dp[vehicleIndex][availableHours]) {
                        dp[vehicleIndex][availableHours] = selectedScore;
                    }
                }
            }
        }

        List<VehicleCandidate> selectedVehicles = new ArrayList<>();
        int remainingHours = capacity;
        for (int vehicleIndex = vehicleCount; vehicleIndex > 0; vehicleIndex--) {
            VehicleCandidate candidate = vehicles.get(vehicleIndex - 1);
            int requiredHours = candidate.getMechanicHoursRequired();
            if (requiredHours <= remainingHours
                    && Double.compare(dp[vehicleIndex][remainingHours], dp[vehicleIndex - 1][remainingHours]) > 0) {
                selectedVehicles.add(candidate);
                remainingHours -= requiredHours;
            }
        }

        Collections.reverse(selectedVehicles);

        int totalAllocatedHours = selectedVehicles.stream()
                .mapToInt(VehicleCandidate::getMechanicHoursRequired)
                .sum();
        double totalOperationalImpactScore = selectedVehicles.stream()
                .mapToDouble(VehicleCandidate::getOperationalImpactScore)
                .sum();

        Map<String, Object> endMetadata = new LinkedHashMap<>();
        endMetadata.put("depotId", depot.getId());
        endMetadata.put("selectedVehicleCount", selectedVehicles.size());
        endMetadata.put("allocatedHours", totalAllocatedHours);
        endMetadata.put("totalOperationalImpactScore", totalOperationalImpactScore);
        loggingService.logAlgorithmExecution("KnapsackOptimizationService.optimize",
                "Completed knapsack optimization for depot.", endMetadata);

        return DepotSchedule.builder()
                .depotId(depot.getId())
                .mechanicHoursLimit(capacity)
                .totalAllocatedMechanicHours(totalAllocatedHours)
                .totalOperationalImpactScore(totalOperationalImpactScore)
                .selectedVehicles(selectedVehicles)
                .build();
    }
}
