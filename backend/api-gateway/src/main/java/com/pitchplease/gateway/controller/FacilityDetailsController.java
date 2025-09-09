package com.pitchplease.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller to handle detailed facility information and reviews.
 * Acts as a gateway to redirect requests to the facility-details-service microservice.
 */
@RestController
@RequestMapping("/facility_details")
public class FacilityDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(FacilityDetailsController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservice.facility-discovery-service.url}")
    private String facilityDiscoveryServiceUrl;
    
    @Value("${microservice.user-service.url}")
    private String userServiceUrl;

    /**
     * Get details for a specific facility
     * 
     * @param id The ID of the facility to fetch details for
     * @return Facility details
     */
    @GetMapping("/get_details")
    public ResponseEntity<?> getFacilityDetails(@RequestParam Long id) {
        logger.info("Received request to fetch details for facility with ID: {}", id);

        try {
            // Add HTTP Basic Authentication
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Build URL with ID parameter
            System.out.println("url = " + facilityDiscoveryServiceUrl + "/details");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(facilityDiscoveryServiceUrl + "/details")
                    .queryParam("id", id);
            System.out.println("l59 facilitydetailscontroller.java");

            // Forward the request to the facility-details microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    Object.class);

            System.out.println("l68 facilitydetailscontroller.java");
            logger.info("Successfully fetched details for facility with ID: {}", id);

            // Return the response from the facility-details microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching facility details: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch facility details: " + e.getMessage());
        }
    }

    /**
     * Get reviews for a specific facility
     * 
     * @param facilityId The ID of the facility to fetch reviews for
     * @return List of reviews for the facility
     */
    @GetMapping("/get_reviews")
    public ResponseEntity<?> getFacilityReviews(@RequestParam Long facilityId) {
        logger.info("Received request to fetch reviews for facility with ID: {}", facilityId);

        try {
            // Add HTTP Basic Authentication
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Build URL with facility ID parameter
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(facilityDiscoveryServiceUrl + "/reviews")
                    .queryParam("facilityId", facilityId);

            // Forward the request to the facility-details microservice
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<?>>() {});

            logger.info("Successfully fetched reviews for facility with ID: {}", facilityId);

            // Return the response from the facility-details microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching facility reviews: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch facility reviews: " + e.getMessage());
        }
    }

    /**
     * Create a new review for a facility
     * 
     * @param reviewData The review data
     * @return Created review information
     */
    @PostMapping("/create_review")
    public ResponseEntity<?> createFacilityReview(@RequestBody Map<String, Object> reviewData) {
        logger.info("Creating new review: {}", reviewData);

        try {
            // Extract userId from reviewData
            Integer userId = reviewData.get("user_id") != null ? 
                Integer.valueOf(reviewData.get("user_id").toString()) : null;
                
            if (userId == null) {
                return ResponseEntity
                    .badRequest()
                    .body("User ID is required");
            }
            
            // First, get username from user service
            String username = getUsernameFromUserService(userId);
            logger.info("Retrieved username: {} for userId: {}", username, userId);
            
            // Add username to the review data
            reviewData.put("user_name", username);
            
            // Set up headers
            HttpHeaders headers = createAuthHeaders();
            headers.set("Content-Type", "application/json");
            
            // Create HTTP entity with the augmented review data
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reviewData, headers);
            
            // Forward the request to the facility-details microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                    facilityDiscoveryServiceUrl + "/reviews/create",
                    HttpMethod.POST,
                    entity,
                    Object.class
            );
            
            logger.info("Successfully created review");
            
            // Return the response from the facility-details microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());
                    
        } catch (Exception e) {
            logger.error("Error while creating review: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to create review: " + e.getMessage());
        }
    }

    /**
     * Delete a review
     * 
     * @param reviewId The ID of the review to delete
     * @param userId The ID of the user attempting to delete the review
     * @return Response indicating success or failure
     */
    @DeleteMapping("/delete_review")
    public ResponseEntity<?> deleteFacilityReview(
            @RequestParam Long reviewId,
            @RequestParam Integer userId) {
        logger.info("Deleting review with ID: {} by user ID: {}", reviewId, userId);

        try {
            // First, get username from user service
            // String username = getUsernameFromUserService(userId);
            String username = "john_doe";
            logger.info("Retrieved username: {} for userId: {}", username, userId);
            
            // Add HTTP Basic Authentication
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Build URL with review ID parameter and user ID
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(facilityDiscoveryServiceUrl + "/reviews/delete")
                    .queryParam("reviewId", reviewId)
                    .queryParam("userId", userId)
                    .queryParam("username", username);

            // Forward the request to the facility-details microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.DELETE,
                    entity,
                    Object.class);

            logger.info("Successfully deleted review with ID: {}", reviewId);

            // Return the response from the facility-details microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while deleting review: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to delete review: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to get username from the user service
     * 
     * @param userId The user ID
     * @return The username
     */
    private String getUsernameFromUserService(Integer userId) {
        try {
            logger.info("Fetching username for user ID: {}", userId);
            
            // Add HTTP Basic Authentication
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Build URL to get user details
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userServiceUrl + "/users/get_username")
                    .queryParam("userId", userId);
                    
            // Make request to user service
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getBody() != null && response.getBody().containsKey("username")) {
                return response.getBody().get("username").toString();
            } else {
                logger.warn("User service didn't return a valid username for user ID: {}", userId);
                return "User " + userId; // Fallback
            }
            
        } catch (Exception e) {
            logger.error("Error while fetching username from user service: {}", e.getMessage());
            // Return a default value rather than failing the whole request
            return "User " + userId;
        }
    }
    
    /**
     * Helper method to create authentication headers
     * 
     * @return HttpHeaders with basic authentication
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = "postgres:postgres"; // Replace with actual credentials
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }
}