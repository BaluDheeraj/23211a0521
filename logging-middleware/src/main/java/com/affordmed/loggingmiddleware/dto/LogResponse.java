package com.affordmed.loggingmiddleware.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogResponse {

    private final Map<String, Object> payload = new LinkedHashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) {
        payload.put(key, value);
    }
}
