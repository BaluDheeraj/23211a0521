package com.afformed.vehicle_scheduler.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "affordmed.api")
public class AffordmedApiProperties {

    private String baseUrl = "";
    private String bearerToken = "";
    private int connectTimeoutMillis = 5000;
    private int readTimeoutMillis = 10000;
    private EvaluationProperties evaluation = new EvaluationProperties();
    private LoggingProperties logging = new LoggingProperties();
    private VehicleFieldMappingProperties vehicleFieldMapping = new VehicleFieldMappingProperties();

    @Getter
    @Setter
    public static class EvaluationProperties {
        private String depotsPath = "/evaluation-service/depots";
        private String vehiclesPath = "/evaluation-service/vehicles";
    }

    @Getter
    @Setter
    public static class LoggingProperties {
        private String endpoint = "";
    }

    @Getter
    @Setter
    public static class VehicleFieldMappingProperties {
        private String depotId = "";
        private String mechanicHours = "";
        private String operationalImpactScore = "";
    }
}
