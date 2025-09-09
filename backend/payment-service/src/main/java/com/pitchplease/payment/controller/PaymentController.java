package com.pitchplease.payment.controller;

import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentDto;
import com.pitchplease.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Controller for handling payment-related operations
 */
@RestController
@RequestMapping
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentDto> processPayment(@RequestBody Map<String, Object> paymentData) {
        logger.info("Received request to process payment: {}", paymentData);
        
        try {
            // Convert the incoming Map to a PaymentDto
            PaymentDto PaymentDto = convertToPaymentDto(paymentData);
            
            // Process the payment
            PaymentDto response = paymentService.processPayment(PaymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Convert payment data from the API gateway to a PaymentDto
     * 
     * @param paymentData Map containing payment data from the gateway
     * @return PaymentDto object
     */
    private PaymentDto convertToPaymentDto(Map<String, Object> paymentData) {
        PaymentDto dto = new PaymentDto();
        String amountStr = paymentData.get("amount").toString();

        dto.setUserId(Long.valueOf(paymentData.get("userId").toString()));

        dto.setUserName(paymentData.get("userName").toString());
        dto.setFacilityId(Long.valueOf(paymentData.get("facilityId").toString()));
        dto.setFacilityName(paymentData.get("facilityName").toString());
        dto.setAddonsString(paymentData.get("addonsString").toString());

        dto.setBookingId(Long.valueOf(paymentData.get("bookingGroupId").toString()));
        dto.setAmount(new BigDecimal(amountStr));
        dto.setPaymentMethod(paymentData.get("paymentMethod").toString());
        // dto.setPaymentStatus(paymentData.get("paymentStatus").toString());
        dto.setPaymentStatus("Completed");
        
        logger.info("Converted payment request data to DTO: {}", dto);
        return dto;
    }

    /**
     * Get payment by ID
     *
     * @param paymentId Payment ID
     * @return Payment details
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long paymentId) {
        logger.info("Fetching payment with ID: {}", paymentId);
        
        Optional<PaymentDto> paymentDto = paymentService.getPaymentById(paymentId);
        return paymentDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get payment by booking ID
     *
     * @param bookingId Booking ID
     * @return Payment details
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentDto> getPaymentByBookingId(@PathVariable Long bookingId) {
        logger.info("Fetching payment for booking ID: {}", bookingId);
        
        Optional<PaymentDto> paymentDto = paymentService.getPaymentByBookingId(bookingId);
        return paymentDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByFacilityId(@PathVariable Long facilityId) {
        logger.info("Fetching payments for facility ID: {}", facilityId);

        List<PaymentDto> payments = paymentService.getPaymentsByFacilityId(facilityId);
        System.out.println("l112 controller in payments microservice: Payments: " + payments);
        return ResponseEntity.ok(payments);
    }
    /**
     * Get all payments for a user
     *
     * @param userId User ID
     * @return List of payments
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByUserId(@PathVariable Long userId) {
        logger.info("Fetching payments for user ID: {}", userId);
        
        List<PaymentDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Update payment status
     *
     * @param paymentId  Payment ID
     * @param newStatus  New payment status
     * @return Updated payment details
     */
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam String newStatus) {
        logger.info("Updating payment {} status to {}", paymentId, newStatus);
        
        Optional<PaymentDto> updatedPayment = paymentService.updatePaymentStatus(paymentId, newStatus);
        return updatedPayment
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Refund a payment
     *
     * @param paymentId Payment ID
     * @return Refunded payment details
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDto> refundPayment(@PathVariable Long paymentId) {
        logger.info("Processing refund for payment ID: {}", paymentId);
        
        Optional<PaymentDto> refundedPayment = paymentService.refundPayment(paymentId);
        return refundedPayment
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/update_status_by_bookingID")
    public ResponseEntity<Map<String, Object>> updatePaymentStatusByBookingId(
            @RequestParam Long bookingId, 
            @RequestParam String status) {
        
        int updatedCount = paymentService.updatePaymentStatusByBookingId(bookingId, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment status update completed");
        response.put("updatedCount", updatedCount);
        response.put("bookingId", bookingId);
        response.put("newStatus", status);
        
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update_status_by_facilityId")
    public ResponseEntity<Map<String, Object>> updatePaymentStatusByFacilityId(
            @RequestParam Long facilityId, 
            @RequestParam String status) {
        
        int updatedCount = paymentService.updatePaymentStatusByFacilityId(facilityId, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment status update completed");
        response.put("updatedCount", updatedCount);
        response.put("facilityId", facilityId);
        response.put("newStatus", status);
        
        return ResponseEntity.ok(response);
    }
}