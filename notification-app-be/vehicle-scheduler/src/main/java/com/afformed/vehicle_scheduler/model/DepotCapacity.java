package com.afformed.vehicle_scheduler.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepotCapacity {

    private final Integer id;
    private final Integer mechanicHoursLimit;
}
