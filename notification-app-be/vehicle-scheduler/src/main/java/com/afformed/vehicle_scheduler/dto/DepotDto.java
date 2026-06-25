package com.afformed.vehicle_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepotDto {

    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("MechanicHours")
    private Integer mechanicHours;
}
