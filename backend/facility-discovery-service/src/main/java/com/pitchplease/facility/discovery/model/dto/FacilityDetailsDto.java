package com.pitchplease.facility.discovery.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDetailsDto {
    private Long facilityId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String facilityType;
    private BigDecimal hourlyRate;
    private Integer ownerId;
    
    // Additional details
    private Double averageRating;
    private Integer reviewCount;
    private String features;
    private String rules;
    private String availability;
    
    // Owner/agent details
    private Map<String, Object> agent;
}