package com.pitchplease.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller to handle sports facility related requests.
 * Acts as a gateway to redirect requests to the facility-service microservice.
 */
@RestController
@RequestMapping("/facilities")
public class FacilityController {

    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservice.facility-discovery-service.url}")
    private String facilityDiscoveryServiceUrl;
    @Value("${microservice.payment-service.url}")
    private String paymentServiceUrl;

    /**
     * Get all available facilities
     * 
     * @return List of all facilities
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllFacilities() {
        logger.info("Received request to fetch all facilities");

        try {
            System.out.println("l39 gateway/facilitycontroller.java");
            System.out.println("l40 url = " + facilityDiscoveryServiceUrl + "/all");

            // Add HTTP Basic Authentication
            HttpHeaders headers = new HttpHeaders();
            String auth = "postgres:postgres"; // Replace with actual credentials
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the facility microservice
            // ResponseEntity<?> response = restTemplate.getForEntity(
            // facilityDiscoveryServiceUrl + "/facilities_discovery",
            // Object.class);

            // Forward the request to the facility microservice
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    facilityDiscoveryServiceUrl + "/all",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<?>>() {
                    });

            logger.info("Successfully fetched facilities from facility-discovery-service");

            // Return the response from the facility microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching facilities: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch facilities: " + e.getMessage());
        }
    }

    /**
     * Search facilities based on various criteria
     * 
     * @param city
     * @param facilityType
     * @param minPrice
     * @param maxPrice
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFacilities(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String facilityType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        logger.info("Searching facilities with criteria - city: {}, facilityType: {}, minPrice: {}, maxPrice: {}",
                city, facilityType, minPrice, maxPrice);
        // Building url for the query :
        try {

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(facilityDiscoveryServiceUrl + "/search");

            // Add query parameters if present
            if (city != null)
                builder.queryParam("city", city);
            if (facilityType != null)
                builder.queryParam("facilityType", facilityType);
            if (minPrice != null)
                builder.queryParam("minPrice", minPrice);
            if (maxPrice != null)
                builder.queryParam("maxPrice", maxPrice);

            String url = builder.toUriString();
            HttpHeaders headers = new HttpHeaders();
            String auth = "postgres:postgres"; // Replace with actual credentials
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the facility microservice
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<?>>() {
                    });
            logger.info("Successfully fetched facilities from facility-discovery-service");
            // Return the response from the facility microservice

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());
        } catch (Exception e) {
            logger.error("Error while searching facilities: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to search facilities: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFacility(@RequestBody Object facilityDto) {
        try {
            // Set up headers
            logger.info("Creating facility: {}", facilityDto);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            String auth = "postgres:postgres"; // Replace with actual credentials
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);

            // Create HTTP entity with the facility data
            HttpEntity<Object> entity = new HttpEntity<>(facilityDto, headers);

            // Forward the request to the facility microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                    facilityDiscoveryServiceUrl + "/create",
                    HttpMethod.POST,
                    entity,
                    Object.class);

            logger.info("Successfully created facility");

            // Return the response from the facility microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while creating facility: {}", e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to create facility: " + e.getMessage());
        }
    }

    /**
     * Get facilities by user ID
     * 
     * @param userId User ID
     * @return List of facilities owned by the user
     */
    @GetMapping("/user_facilities")
    public ResponseEntity<?> getFacilitiesByUser(@RequestParam Long userId) {
        try {
            logger.info("Fetching facilities for user with ID: {}", userId);
            // Forward the request to the facility microservice
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    facilityDiscoveryServiceUrl + "/user_facilities?userId=" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<?>>() {
                    });
            logger.info("Successfully fetched facilities for user ID: {}", userId);
            // Return the response from the facility microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());
        } catch (Exception e) {
            logger.error("Error while fetching facilities for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch facilities for user ID " + userId + ": " + e.getMessage());
        }

    }
    /**
     * Delete an existing facility
     * 
     * @param id          Facility ID
     * @return Updated facility
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFacility(@RequestParam Long facilityId) {
        try {
            // Forward the request to the facility microservice
            ResponseEntity<Void> response = restTemplate.exchange(
                    facilityDiscoveryServiceUrl + "/delete?facilityId=" + facilityId,
                    HttpMethod.DELETE,
                    null,
                    Void.class);
            
            logger.info("Successfully deleted facility with ID: {}", facilityId);
            // Shailender Stekkd
            // put request to /update_status_by_bookingID in the Payment Controller in payment-service microservice
            // Set up authentication headers
            HttpHeaders headers2 = createAuthHeaders();
            HttpEntity<String> entity2 = new HttpEntity<>(headers2);


            ResponseEntity<Map<String, Object>> response2 = restTemplate.exchange(
                paymentServiceUrl + "/update_status_by_facilityId?facilityId=" + facilityId + "&status=DELISTED_REFUND_PROCESSING",
                HttpMethod.PUT,
                entity2,
                new ParameterizedTypeReference<Map<String, Object>>() {});
 
            // Return the response from the facility microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .build();
        } catch (Exception e) {
            logger.error("Error while deleting facility with ID {}: {}", facilityId, e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(null);
        }
    }
    /**
     * Update an existing facility
     * 
     * @param id          Facility ID
     * @param facilityDto Updated facility details
     * @return Updated facility
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateFacility( @RequestBody Object facilityDto) {
        try {
            // Set up headers
            logger.info("Updating facility: {}", facilityDto);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            String auth = "postgres:postgres"; // Replace with actual credentials
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
            
            // Create HTTP entity with the facility data
            HttpEntity<Object> entity = new HttpEntity<>(facilityDto, headers);
            
            // Forward the request to the facility microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                facilityDiscoveryServiceUrl + "/update" ,
                HttpMethod.PUT,
                entity,
                Object.class);
                
            logger.info("Successfully updated facility");
            
            // Return the response from the facility microservice
            return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
        } catch (Exception e) {
            logger.error("Error while updating facility: {}", e.getMessage());
            return ResponseEntity
                .internalServerError()
                .body("Failed to update facility: " + e.getMessage());
        }
    }

    /**
     * Create HTTP headers with authentication
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = "postgres:postgres"; // Replace with actual credentials from a secure configuration
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }
}
