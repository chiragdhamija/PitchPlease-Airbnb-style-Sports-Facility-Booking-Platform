package com.pitchplease.facility.discovery.mapper;

import org.springframework.stereotype.Component;

import com.pitchplease.facility.discovery.model.dto.FacilityDto;
import com.pitchplease.facility.discovery.model.entity.Facility;

@Component
public class FacilityMapper {
    
    /**
     * Convert Facility entity to FacilityDto
     * 
     * @param entity Facility entity
     * @return FacilityDto
     */
    public FacilityDto toDto(Facility entity) {
        if (entity == null) {
            return null;
        }
        
        return new FacilityDto(
                entity.getFacilityId(),
                entity.getName(),
                entity.getDescription(),
                entity.getAddress(),
                entity.getCity(),
                entity.getFacilityType(),
                entity.getHourlyRate(),
                entity.getOwnerId()
        );
    }
    
    /**
     * Convert FacilityDto to Facility entity
     * 
     * @param dto FacilityDto
     * @return Facility entity
     */
    public Facility toEntity(FacilityDto dto) {
        if (dto == null) {
            return null;
        }
        
        return new Facility(
                dto.getFacilityId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAddress(),
                dto.getCity(),
                dto.getFacilityType(),
                dto.getHourlyRate(),
                dto.getOwnerId()
        );
    }
}