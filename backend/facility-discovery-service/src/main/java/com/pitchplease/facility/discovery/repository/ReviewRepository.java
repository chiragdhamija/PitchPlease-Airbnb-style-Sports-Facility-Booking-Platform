package com.pitchplease.facility.discovery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pitchplease.facility.discovery.model.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find all reviews for a specific facility
     * 
     * @param facilityId The facility ID
     * @return List of reviews for the facility
     */
    List<Review> findByFacilityIdOrderByCreatedAtDesc(Long facilityId);
    
    /**
     * Find review by facility ID and user ID
     * 
     * @param facilityId The facility ID
     * @param userId The user ID
     * @return The review if found
     */
    Optional<Review> findByFacilityIdAndUserId(Long facilityId, Integer userId);
    
    /**
     * Check if a user has already reviewed a facility
     * 
     * @param facilityId The facility ID
     * @param userId The user ID
     * @return True if the user has already reviewed the facility
     */
    boolean existsByFacilityIdAndUserId(Long facilityId, Integer userId);
    
    /**
     * Calculate the average rating for a facility
     * 
     * @param facilityId The facility ID
     * @return The average rating, or null if no ratings
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.facilityId = :facilityId")
    Double getAverageRatingForFacility(Long facilityId);
    
    /**
     * Count the number of reviews for a facility
     * 
     * @param facilityId The facility ID
     * @return The number of reviews
     */
    long countByFacilityId(Long facilityId);
    
    /**
     * Delete a review by ID and user ID (for security)
     * 
     * @param reviewId The review ID
     * @param userId The user ID
     * @return Number of reviews deleted
     */
    long deleteByReviewIdAndUserId(Long reviewId, Integer userId);
}