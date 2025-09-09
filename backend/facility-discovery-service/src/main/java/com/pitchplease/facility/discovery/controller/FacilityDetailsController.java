package com.pitchplease.facility.discovery.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pitchplease.facility.discovery.model.dto.FacilityDetailsDto;
import com.pitchplease.facility.discovery.model.dto.ReviewDto;
import com.pitchplease.facility.discovery.service.FacilityDetailsService;

/**
 * Controller to handle detailed facility information and reviews.
 */
@RestController
public class FacilityDetailsController {

    @Autowired
    private FacilityDetailsService facilityDetailsService;

    /**
     * Get details for a specific facility
     * 
     * @param id The ID of the facility to fetch details for
     * @return Facility details
     */
    @GetMapping("/details")
    public ResponseEntity<?> getFacilityDetails(@RequestParam Long id) {
        System.out.println("l34 in microservice api controller");
        Optional<FacilityDetailsDto> facilityDetails = facilityDetailsService.getFacilityDetails(id);
        System.out.print("l36 in microservice api controller");
        if (facilityDetails.isPresent()) {
            return new ResponseEntity<>(facilityDetails.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get reviews for a specific facility
     * 
     * @param facilityId The ID of the facility to fetch reviews for
     * @return List of reviews for the facility
     */
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getFacilityReviews(@RequestParam Long facilityId) {
        List<ReviewDto> reviews = facilityDetailsService.getFacilityReviews(facilityId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    /**
     * Create a new review for a facility
     * 
     * @param reviewData Map containing review data, including facilityId, userId, userName, rating, and comment
     * @return Created review information
     */
    @PostMapping("/reviews/create")
    public ResponseEntity<?> createFacilityReview(@RequestBody Map<String, Object> reviewData) {
        try {
            Long facilityId = Long.valueOf(reviewData.get("facility_id").toString());
            Integer userId = Integer.valueOf(reviewData.get("user_id").toString());
            Integer rating = Integer.valueOf(reviewData.get("rating").toString());
            String comment = (String) reviewData.get("comment");
            String userName = (String) reviewData.get("user_name"); // Username from user service
            
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setFacilityId(facilityId);
            reviewDto.setUserId(userId);
            reviewDto.setUserName(userName);
            reviewDto.setRating(rating);
            reviewDto.setComment(comment);
            
            ReviewDto createdReview = facilityDetailsService.createReview(reviewDto);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create review: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a review
     * 
     * @param reviewId The ID of the review to delete
     * @param userId The ID of the user attempting to delete the review
     * @param username The username of the user (from user service)
     * @return Response indicating success or failure
     */
    @DeleteMapping("/reviews/delete")
    public ResponseEntity<?> deleteFacilityReview(
            @RequestParam Long reviewId,
            @RequestParam Integer userId,
            @RequestParam(required = false) String username) {
        try {
            boolean deleted = facilityDetailsService.deleteReview(reviewId, userId);
            
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Review deleted successfully by " + (username != null ? username : "User " + userId));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Review not found or user does not have permission to delete");
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete review: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}