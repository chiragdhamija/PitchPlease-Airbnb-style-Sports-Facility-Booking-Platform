package com.pitchplease.facility.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.pitchplease.facility.discovery.model.entity"})
@EnableJpaRepositories(basePackages = {"com.pitchplease.facility.discovery.repository"})
public class FacilityDiscoveryServiceApplication {
    public static void main(String[] args) {
        // System.setProperty("server.port", "8094");
        SpringApplication.run(FacilityDiscoveryServiceApplication.class, args);
    }
}