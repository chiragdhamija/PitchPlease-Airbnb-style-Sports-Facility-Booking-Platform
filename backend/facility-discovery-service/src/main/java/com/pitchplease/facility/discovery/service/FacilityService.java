package com.pitchplease.facility.discovery.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pitchplease.facility.discovery.mapper.FacilityMapper;
import com.pitchplease.facility.discovery.model.dto.FacilityDto;
import com.pitchplease.facility.discovery.model.entity.Facility;
import com.pitchplease.facility.discovery.repository.FacilityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private FacilityMapper facilityMapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get all facilities
     * 
     * @return List of all facilities
     */
    @Transactional(readOnly = true)
    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get facility by ID
     * 
     * @param id Facility ID
     * @return Optional containing the facility if found
     */
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityById(Long id) {
        return facilityRepository.findById(id)
                .map(facilityMapper::toDto);
    }
    
    /**
     * Create a new facility
     * 
     * @param facilityDto Facility to be created
     * @return Created facility
     */
    @Transactional
    public FacilityDto createFacility(FacilityDto facilityDto) {
        Facility entity = facilityMapper.toEntity(facilityDto);
        entity.setFacilityId(null); // Ensure we're creating a new entity
        Facility savedEntity = facilityRepository.save(entity);
        return facilityMapper.toDto(savedEntity);
    }
    /**
     * Get facilities by user ID
     * 
     * @param userId User ID
     * @return List of facilities owned by the user
     */
    @Transactional
    public List<FacilityDto> getFacilitiesByOwnerId(Long userId) {
        return facilityRepository.findByOwnerId(userId).stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Update an existing facility
     * 
     * @param id Facility ID
     * @param facilityDto Updated facility details
     * @return Updated facility
     * @throws RuntimeException if facility not found
     */
    // @Transactional
    // public FacilityDto updateFacility(Long id, FacilityDto facilityDto) {
    //     Facility existing = facilityRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Facility not found with id: " + id));
        
    //     // Update fields
    //     existing.setName(facilityDto.getName());
    //     existing.setDescription(facilityDto.getDescription());
    //     existing.setAddress(facilityDto.getAddress());
    //     existing.setCity(facilityDto.getCity());
    //     existing.setFacilityType(facilityDto.getFacilityType());
    //     existing.setHourlyRate(facilityDto.getHourlyRate());
    //     existing.setOwnerId(facilityDto.getOwnerId());
        
    //     Facility updated = facilityRepository.save(existing);
    //     return facilityMapper.toDto(updated);
    // }
    
    /**
     * Delete a facility
     * 
     * @param id Facility ID
     * @throws RuntimeException if facility not found
     */
    @Transactional
    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found with id: " + id));
                
        facilityRepository.delete(facility);
    }
    @Transactional
    public List<FacilityDto> searchFacilities(String city, String facilityType, Double minPrice, Double maxPrice) {
        // Decode the city and facilityType parameters
        // city = UrlUtils.decode(city, StandardCharsets.UTF_8);
        // facilityType = UrlUtils.decode(facilityType, StandardCharsets.UTF_8);

        List<Facility> facilities = facilityRepository.findByCriteria(city, facilityType, minPrice, maxPrice);
        

        return facilities.stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateFacility( FacilityDto facilityDto) {

        System.out.println("Updating facility with ID: " + facilityDto.getFacilityId());
        int updated = facilityRepository.updateFacility(
            facilityDto.getFacilityId(),
            facilityDto.getName(),
            facilityDto.getDescription(),
            facilityDto.getAddress(),
            facilityDto.getCity(),
            facilityDto.getFacilityType(),
            facilityDto.getHourlyRate().doubleValue()
        );
        
        if (updated == 0) {
            throw new RuntimeException("Facility not found with id: " + facilityDto.getFacilityId());
        }
        System.out.println("Updated " + updated + " facility(s).");
        return;
    }

}