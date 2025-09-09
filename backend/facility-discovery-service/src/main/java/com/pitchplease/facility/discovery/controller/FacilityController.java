package com.pitchplease.facility.discovery.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.pitchplease.facility.discovery.model.dto.FacilityDto;
import com.pitchplease.facility.discovery.service.FacilityService;

@RestController
// @RequestMapping("/facilities_discovery")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    /**
     * Get all facilities
     * This is the endpoint that will be called from the API Gateway
     * 
     * @return List of all facilities
     */
    @GetMapping("/all")
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        System.out.println("l36 facility controller.java ");
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        System.out.println("l38 facilities = " + facilities);
        return new ResponseEntity<>(facilities, HttpStatus.OK);
    }

    /**
     * Search facilities based on various criteria
     * 
     * @param city         The city to search in (optional)
     * @param facilityType The type of facility to search for (optional)
     * @param minPrice     Minimum price for filtering (optional)
     * @param maxPrice     Maximum price for filtering (optional)
     * @return List of facilities matching the search criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<FacilityDto>> searchFacilities(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String facilityType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        // String decodedCity = UriDecoder.decode(city, StandardCharsets.UTF_8);
        // String decodedFacilityType = UriUtils.decode(facilityType,
        // StandardCharsets.UTF_8);

        String decodedCity = city != null ? URLDecoder.decode(city, StandardCharsets.UTF_8) : null;
        String decodedFacilityType = facilityType != null ? URLDecoder.decode(facilityType, StandardCharsets.UTF_8)
                : null;

        System.out.println("Original city parameter: " + city);
        System.out.println("Decoded city parameter: " + decodedCity);

        System.out.println("Searching facilities with criteria - city: " + decodedCity
                + ", facilityType: " + decodedFacilityType + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice);

        // Pass the decoded values to the service layer
        List<FacilityDto> results = facilityService.searchFacilities(
                decodedCity, decodedFacilityType, minPrice, maxPrice);

        System.out.println("Found " + results.size() + " matching facilities");

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * Get facility by ID
     * 
     * @param id Facility ID
     * @return Facility if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable Long id) {
        Optional<FacilityDto> facility = facilityService.getFacilityById(id);

        if (facility.isPresent()) {
            return new ResponseEntity<>(facility.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new facility
     * 
     * @param facilityDto Facility to be created
     * @return Created facility
     */
    @PostMapping("/create")
    public ResponseEntity<FacilityDto> createFacility(@RequestBody FacilityDto facilityDto) {
        FacilityDto newFacility = facilityService.createFacility(facilityDto);
        return new ResponseEntity<>(newFacility, HttpStatus.CREATED);
    }

    /**
     * Update an existing facility
     * 
     * @param id          Facility ID
     * @param facilityDto Updated facility details
     * @return Updated facility
     */
    // @PutMapping("/update")
    // public ResponseEntity<FacilityDto> updateFacility(@RequestParam Long id, @RequestBody FacilityDto facilityDto) {
    //     try {
    //         FacilityDto updatedFacility = facilityService.updateFacility(id, facilityDto);
    //         return new ResponseEntity<>(updatedFacility, HttpStatus.OK);
    //     } catch (RuntimeException e) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    // }

    /**
     * Get facilities by user ID
     * 
     * @param userId
     * @return
     */
    @GetMapping("/user_facilities")
    public ResponseEntity<List<FacilityDto>> getFacilitiesByUserId(@RequestParam Long userId) {
        System.out.println("l127 facility controller.java ");
        List<FacilityDto> facilities = facilityService.getFacilitiesByOwnerId(userId);
        System.out.println("l130 facilities = " + facilities);
        return new ResponseEntity<>(facilities, HttpStatus.OK);
    }

    /**
     * Delete a facility
     * 
     * @param facilityId Facility ID
     * @return No content
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFacility(@RequestParam Long facilityId) {
        try {
            facilityService.deleteFacility(facilityId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update a facility by ID
     * 
     * @param id          Facility ID
     * @param facilityDto Updated facility details
     * @return No content
     */
    @PutMapping("/update")
    public ResponseEntity<Void> updateFacility(@RequestBody FacilityDto facilityDto) {
        System.out.println("l152 facility controller.java ");
        try {
            facilityService.updateFacility(facilityDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        

    }
}