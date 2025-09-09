package com.pitchplease.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller to handle booking related requests.
 * Acts as a gateway to redirect requests to the booking-service microservice.
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservice.booking-service.url}")
    private String bookingServiceUrl;
    @Value("${microservice.payment-service.url}")
    private String paymentServiceUrl;
    /**
     * Get available time slots for a facility on a specific date
     * 
     * @param facilityId The ID of the facility
     * @param date The date in format YYYY-MM-DD
     * @return List of available time slots
     */
    @GetMapping("/available_slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @RequestParam Long facilityId,
            @RequestParam String date) {
        
        logger.info("Received request to fetch available time slots for facility ID: {} on date: {}", 
                facilityId, date);

        try {
            // Build URL with query parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(bookingServiceUrl + "/get_available_slots")
                    .queryParam("facilityId", facilityId)
                    .queryParam("date", date);
            
            // Set up authentication headers
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the booking microservice
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                //     bookingServiceUrl + "/get_available_slots",
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            logger.info("Successfully fetched available time slots from booking-service");

            // Return the response from the booking microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching available time slots: {}", e.getMessage(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch available time slots: " + e.getMessage());
        }
    }

    /**
     * Get all bookings for a user
     * 
     * @param userId The ID of the user
     * @return List of bookings for the user
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserBookings(@RequestParam Integer userId) {
        logger.info("Received request to fetch bookings for user ID: {}", userId);

        try {
            // Build URL with query parameters
            String url = bookingServiceUrl + "/user?userId=" + userId;
            
            // Set up authentication headers
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the booking microservice
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<?>>() {});

            logger.info("Successfully fetched user bookings from booking-service");

            // Return the response from the booking microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching user bookings: {}", e.getMessage(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch user bookings: " + e.getMessage());
        }
    }

    /**
     * Cancel a booking group (all time slots in a booking)
     * 
     * @param bookingGroupId The ID of the booking group to cancel
     * @return Cancellation confirmation
     */
    @DeleteMapping("/cancel-group")
    public ResponseEntity<?> cancelBookingGroup(@RequestParam Long bookingGroupId) {
        logger.info("Received request to cancel booking group ID: {}", bookingGroupId);

        try {
            // Set up authentication headers
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the booking microservice
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    bookingServiceUrl + "/cancel-group?bookingGroupId=" + bookingGroupId,
                    HttpMethod.DELETE,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            logger.info("Successfully cancelled booking group with ID: {}", bookingGroupId);
            // Chainging the status in the payment table
            // Shailender Stekkd
            // put request to /update_status_by_bookingID in the Payment Controller in payment-service microservice
            // Set up authentication headers
            HttpHeaders headers2 = createAuthHeaders();
            HttpEntity<String> entity2 = new HttpEntity<>(headers2);


            ResponseEntity<Map<String, Object>> response2 = restTemplate.exchange(
                paymentServiceUrl + "/update_status_by_bookingID?bookingId=" + bookingGroupId + "&status=CANCELLED",
                HttpMethod.PUT,
                entity2,
                new ParameterizedTypeReference<Map<String, Object>>() {});
                // Return the response from the booking microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while cancelling booking group: {}", e.getMessage(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to cancel booking group: " + e.getMessage());
        }
    }

    /**
     * Get booking details by booking ID
     * 
     * @param bookingId The ID of the booking
     * @return Booking details
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        logger.info("Received request to fetch booking with ID: {}", bookingId);

        try {
            // Set up authentication headers
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the booking microservice
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    bookingServiceUrl + "/" + bookingId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            logger.info("Successfully fetched booking with ID: {}", bookingId);

            // Return the response from the booking microservice
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            logger.error("Error while fetching booking: {}", e.getMessage(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to fetch booking: " + e.getMessage());
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