package com.afformed.vehicle_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepotApiResponse {

    private List<DepotDto> depots = new ArrayList<>();
}
