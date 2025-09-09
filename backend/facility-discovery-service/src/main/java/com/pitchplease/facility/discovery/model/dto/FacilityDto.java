package com.pitchplease.facility.discovery.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    private Long facilityId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String facilityType;
    private BigDecimal hourlyRate;
    private Integer ownerId;
}