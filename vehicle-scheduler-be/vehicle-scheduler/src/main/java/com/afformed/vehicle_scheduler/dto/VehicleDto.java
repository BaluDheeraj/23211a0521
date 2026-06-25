package com.afformed.vehicle_scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDto {

    @JsonProperty("TaskID")
    private String taskId;

    // TODO: Add strongly typed fields when the remaining vehicle API contract is available.
    @JsonIgnore
    private final Map<String, Object> additionalAttributes = new LinkedHashMap<>();

    @JsonAnySetter
    public void setAdditionalAttribute(String key, Object value) {
        if (!"TaskID".equalsIgnoreCase(key)) {
            additionalAttributes.put(key, value);
        }
    }
}
