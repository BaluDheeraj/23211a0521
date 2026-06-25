package com.afformed.vehicle_scheduler.service;

import com.afformed.vehicle_scheduler.dto.VehicleDto;
import com.afformed.vehicle_scheduler.logging.LoggingService;
import com.afformed.vehicle_scheduler.model.VehicleCandidate;
import com.afformed.vehicle_scheduler.util.VehicleAttributeResolver;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class VehicleCandidateMapper {

    private final VehicleAttributeResolver vehicleAttributeResolver;
    private final LoggingService loggingService;

    public List<VehicleCandidate> mapVehicles(List<VehicleDto> vehicles, Set<Integer> knownDepotIds) {
        List<VehicleCandidate> candidates = new ArrayList<>();
        for (VehicleDto vehicle : vehicles) {
            toVehicleCandidate(vehicle, knownDepotIds).ifPresent(candidates::add);
        }
        return candidates;
    }

    private Optional<VehicleCandidate> toVehicleCandidate(VehicleDto vehicle, Set<Integer> knownDepotIds) {
        if (!StringUtils.hasText(vehicle.getTaskId())) {
            loggingService.logError("service", "VehicleCandidateMapper.toVehicleCandidate",
                    "Skipping vehicle because TaskID is missing.", null, Map.of());
            return Optional.empty();
        }

        Optional<Integer> depotId = vehicleAttributeResolver.resolveDepotId(vehicle, knownDepotIds);
        Optional<Integer> mechanicHours = vehicleAttributeResolver.resolveMechanicHours(vehicle);
        Optional<Double> impactScore = vehicleAttributeResolver.resolveOperationalImpactScore(vehicle);

        if (depotId.isEmpty() || mechanicHours.isEmpty() || impactScore.isEmpty()) {
            loggingService.logError("service", "VehicleCandidateMapper.toVehicleCandidate",
                    "Skipping vehicle because required optimization fields could not be resolved.", null,
                    buildVehicleMetadata(vehicle));
            return Optional.empty();
        }

        if (!knownDepotIds.contains(depotId.get())) {
            loggingService.logError("service", "VehicleCandidateMapper.toVehicleCandidate",
                    "Skipping vehicle because the resolved depot does not exist in the depot response.", null,
                    buildVehicleMetadata(vehicle));
            return Optional.empty();
        }

        if (mechanicHours.get() <= 0 || impactScore.get() < 0) {
            loggingService.logError("service", "VehicleCandidateMapper.toVehicleCandidate",
                    "Skipping vehicle because mechanic hours or impact score is invalid.", null,
                    buildVehicleMetadata(vehicle));
            return Optional.empty();
        }

        return Optional.of(VehicleCandidate.builder()
                .taskId(vehicle.getTaskId())
                .depotId(depotId.get())
                .mechanicHoursRequired(mechanicHours.get())
                .operationalImpactScore(impactScore.get())
                .sourceAttributes(new LinkedHashMap<>(vehicle.getAdditionalAttributes()))
                .build());
    }

    private Map<String, Object> buildVehicleMetadata(VehicleDto vehicle) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("taskId", vehicle.getTaskId());
        metadata.put("attributes", vehicle.getAdditionalAttributes());
        return metadata;
    }
}
