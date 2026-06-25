package com.afformed.vehicle_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class VehicleSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleSchedulerApplication.class, args);
	}

}
