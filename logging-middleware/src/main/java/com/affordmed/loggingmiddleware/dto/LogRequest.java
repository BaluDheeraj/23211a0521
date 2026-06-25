package com.affordmed.loggingmiddleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogRequest {

    private String stack;
    private String level;

    @JsonProperty("package")
    private String packageName;

    private String message;
}
