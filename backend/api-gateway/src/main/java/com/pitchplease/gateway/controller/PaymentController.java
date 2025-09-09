package com.pitchplease.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.PathVariable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller to handle payment requests.
 * Acts as a gateway to redirect requests to both the booking-service and
 * payment-service microservices.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservice.booking-service.url}")
    private String bookingServiceUrl;

    @Value("${microservice.payment-service.url}")
    private String paymentServiceUrl;

    /**
     * Create a new payment and associated booking
     * 
     * @param paymentData Payment data including booking information
     * @return Created payment details
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPaymentAndBooking(@RequestBody Map<String, Object> paymentData) {
        logger.info("Received request to create payment and booking: {}", paymentData);

        try {
            // Separate booking data from payment data
            logger.info("l55 paymentData = {}", paymentData);

            Map<String, Object> bookingData = extractBookingData(paymentData);
            Map<String, Object> paymentOnlyData = extractPaymentData(paymentData);

            logger.info("l60 paymentOnlyData = {}", paymentOnlyData);

            // Set up HTTP headers with authentication
            HttpHeaders headers = createAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // First, create the booking
            logger.info("Creating booking with data: {}", bookingData);
            HttpEntity<Map<String, Object>> bookingEntity = new HttpEntity<>(bookingData, headers);
            // BOOKING_MICROSERVICE_API_CALL
            ResponseEntity<Map> bookingResponse = restTemplate.exchange(
                    bookingServiceUrl + "/create",
                    HttpMethod.POST,
                    bookingEntity,
                    Map.class);

            if (!bookingResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to create booking. Status: {}", bookingResponse.getStatusCode());
                return ResponseEntity.status(bookingResponse.getStatusCode()).body(bookingResponse.getBody());
            }

            logger.info("Booking created successfully: {}", bookingResponse.getBody());

            // Get the booking Group ID from the response
            Map<String, Object> bookingResponseBody = bookingResponse.getBody();
            Long bookingId = Long.valueOf(bookingResponseBody.get("bookingGroupId").toString());

            // Add booking ID to payment data
            paymentOnlyData.put("bookingGroupId", bookingId);

            // Create the payment
            logger.info("Creating payment with data: {}", paymentOnlyData);
            HttpEntity<Map<String, Object>> paymentEntity = new HttpEntity<>(paymentOnlyData, headers);
            // PAYMENT_MICROSERVICE_API_CALL
            ResponseEntity<Map> paymentResponse = restTemplate.exchange(
                    paymentServiceUrl + "/create",
                    HttpMethod.POST,
                    paymentEntity,
                    Map.class);

            if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to process payment. Status: {}", paymentResponse.getStatusCode());
                // Consider rolling back the booking here
                return ResponseEntity.status(paymentResponse.getStatusCode()).body(paymentResponse.getBody());
            }

            logger.info("Payment processed successfully: {}", paymentResponse.getBody());

            // Combine booking and payment responses
            Map<String, Object> combinedResponse = new HashMap<>(bookingResponseBody);
            combinedResponse.put("payment", paymentResponse.getBody());

            return ResponseEntity.ok(combinedResponse);

        } catch (Exception e) {
            logger.error("Error processing payment and booking: {}", e.getMessage(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * Extract booking-specific data from the combined payment request
     */
    private Map<String, Object> extractBookingData(Map<String, Object> paymentData) {
        Map<String, Object> bookingData = new HashMap<>();

        // Required booking fields
        bookingData.put("userId", paymentData.get("userId"));
        bookingData.put("facilityId", paymentData.get("facilityId"));
        bookingData.put("date", paymentData.get("date"));
        bookingData.put("timeSlots", paymentData.get("timeSlots"));
        bookingData.put("totalHours", paymentData.get("hours"));

        return bookingData;
    }

    /**
     * Extract payment-specific data from the combined payment request
     */
    private Map<String, Object> extractPaymentData(Map<String, Object> paymentData) {
        Map<String, Object> paymentOnlyData = new HashMap<>();

        // Required payment fields
        paymentOnlyData.put("userId", paymentData.get("userId"));
        paymentOnlyData.put("amount", paymentData.get("totalAmount"));
        paymentOnlyData.put("paymentMethod", paymentData.get("paymentMethod"));
        paymentOnlyData.put("paymentStatus", paymentData.get("paymentStatus"));

        paymentOnlyData.put("userName", paymentData.get("userName"));
        paymentOnlyData.put("facilityId", paymentData.get("facilityId"));
        paymentOnlyData.put("facilityName", paymentData.get("facilityName"));
        paymentOnlyData.put("addonsString", paymentData.get("addonsString"));

        return paymentOnlyData;
    }
    /**
     * Get Payments for a specific FacilityId
     * @param FacilityId
     * @return
     */
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<?> getPaymentsByFacilityId(@PathVariable Long facilityId) {
        try {
            // Set up authentication headers
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Forward the request to the payments microservice
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    paymentServiceUrl + "/facility/" + facilityId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Object>>() {});

            logger.info("l180 facility ID: {}", facilityId);

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
     * Fetch payments by user ID from payment-service
     * 
     * @param userId the ID of the user
     * @return List of payment DTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable Long userId) {
        logger.info("Received request to fetch payments for userId: {}", userId);

        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    paymentServiceUrl + "/user/" + userId,
                    HttpMethod.GET,
                    requestEntity,
                    List.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to fetch payments. Status: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }

            logger.info("Successfully fetched payments for userId {}: {}", userId, response.getBody());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            logger.error("Error fetching payments for userId {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to fetch payments: " + e.getMessage());
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