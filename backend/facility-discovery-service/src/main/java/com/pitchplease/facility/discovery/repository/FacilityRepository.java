package com.pitchplease.facility.discovery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pitchplease.facility.discovery.model.entity.Facility;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    // Custom query methods

    List<Facility> findByCity(String city);

    List<Facility> findByFacilityType(String facilityType);

    List<Facility> findByOwnerId(Long ownerId);

    /**
     * Search facilities by name containing the specified keyword (case insensitive)
     * 
     * @param keyword The keyword to search for in facility names
     * @return List of facilities with names containing the keyword
     */
    @Query("SELECT f FROM Facility f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Facility> searchByNameContainingIgnoreCase(String keyword);

    /**
     * Find facilities by multiple criteria
     * 
     * @param city         The city to filter by (optional)
     * @param facilityType The facility type to filter by (optional)
     * @param minPrice     Minimum price (optional)
     * @param maxPrice     Maximum price (optional)
     * @return List of matching facilities
     */
    @Query("SELECT f FROM Facility f WHERE " +
            "(:city IS NULL OR f.city = :city) AND " +
            "(:facilityType IS NULL OR f.facilityType = :facilityType) AND " +
            "(:minPrice IS NULL OR f.hourlyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR f.hourlyRate <= :maxPrice)")
    List<Facility> findByCriteria(String city, String facilityType, Double minPrice, Double maxPrice);
    /**
     * Update a facility with the given information
     *
     * @param id The facility ID to update
     * @param name The new facility name
     * @param description The new description
     * @param address The new address
     * @param city The new city
     * @param facilityType The new facility type
     * @param hourlyRate The new hourly rate
     * @return Number of records updated (should be 1 if successful)
     */
    @Modifying
    
    @Query("UPDATE Facility f SET " +
           "f.name = :name, " +
           "f.description = :description, " +
           "f.address = :address, " +
           "f.city = :city, " +
           "f.facilityType = :facilityType, " +
           "f.hourlyRate = :hourlyRate " +
           "WHERE f.id = :id")
    int updateFacility(Long id, String name, String description, String address, 
                      String city, String facilityType, Double hourlyRate);
}