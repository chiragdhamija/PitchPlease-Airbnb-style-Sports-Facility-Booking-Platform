package com.pitchplease.payment.service;

import com.pitchplease.payment.mapper.PaymentMapper;
import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentDto;
import com.pitchplease.payment.model.entity.Payment;
import com.pitchplease.payment.repository.PaymentRepository;
import com.pitchplease.payment.strategy.PaymentStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for handling payment operations using the Strategy Pattern
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private PaymentStrategyFactory paymentStrategyFactory;

    /**
     * Process a new payment using the appropriate payment strategy
     * @param PaymentDto Payment request data
     * @return Payment response data
     */
    @Transactional
    public PaymentDto processPayment(PaymentDto PaymentDto) {
        logger.info("Processing payment for booking ID: {} with method: {}", 
                PaymentDto.getBookingId(), PaymentDto.getPaymentMethod());
        
        // Get the appropriate payment strategy
        PaymentDto strategyResponse = paymentStrategyFactory
                .getStrategy(PaymentDto.getPaymentMethod())
                .processPayment(PaymentDto);
        
        // Convert request to entity using mapper
        Payment payment = paymentMapper.toEntity(PaymentDto);
        
        // Update with strategy response data
        payment.setPaymentStatus(strategyResponse.getPaymentStatus());
        payment.setTransactionId(strategyResponse.getTransactionId());
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment record created with ID: {}", savedPayment.getPaymentId());
        
        // Convert to response using mapper
        PaymentDto response = paymentMapper.toDto(savedPayment);
        
        return response;
    }

    /**
     * Get payment by ID
     * @param paymentId Payment ID
     * @return Optional of payment DTO
     */
    public Optional<PaymentDto> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(payment -> paymentMapper.toDto(payment));
    }

    /**
     * Get payment by booking ID
     * @param bookingId Booking ID
     * @return Optional of payment DTO
     */
    public Optional<PaymentDto> getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .map(payment -> paymentMapper.toDto(payment));
    }

    /**
     * Get all payments for a user
     * @param userId User ID
     * @return List of payment DTOs
     */
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(payment -> paymentMapper.toDto(payment))
                .collect(Collectors.toList());
    }
    /** 
     * Get payments by facility Id
     * @param facilityId Facility Id
     * @return List of payment DTOs
     */
    public List<PaymentDto> getPaymentsByFacilityId(Long facilityId) {
        return paymentRepository.findByFacilityId(facilityId).stream()
                .map(payment -> paymentMapper.toDto(payment))
                .collect(Collectors.toList());
    }
    /**
     * Update payment status
     * @param paymentId Payment ID
     * @param newStatus New payment status
     * @return Updated payment DTO
     */
    @Transactional
    public Optional<PaymentDto> updatePaymentStatus(Long paymentId, String newStatus) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setPaymentStatus(newStatus);
            payment.setUpdatedAt(LocalDateTime.now());
            
            Payment updatedPayment = paymentRepository.save(payment);
            logger.info("Updated payment {} status to {}", paymentId, newStatus);
            
            return Optional.of(paymentMapper.toDto(updatedPayment));
        }
        
        logger.warn("Payment with ID {} not found for status update", paymentId);
        return Optional.empty();
    }

    /**
     * Refund a payment
     * @param paymentId Payment ID
     * @return Refunded payment DTO
     */
    @Transactional
    public Optional<PaymentDto> refundPayment(Long paymentId) {
        return updatePaymentStatus(paymentId, "REFUNDED");
    }
    
    /**
     * Get all supported payment methods
     * @return List of supported payment method names
     */
    public List<String> getSupportedPaymentMethods() {
        return paymentStrategyFactory.getSupportedMethods();
    }
    /**
     * Updates all payments with a given bookingId to a new status
     * 
     * @param bookingId The booking ID to match
     * @param newStatus The new status to set
     * @return Number of payments updated
     */
    @Transactional
    public int updatePaymentStatusByBookingId(Long bookingId, String newStatus) {
        logger.info("Updating payments for booking ID {} to status {}", bookingId, newStatus);
        
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = paymentRepository.updatePaymentStatusByBookingId(bookingId, newStatus, now);
        
        logger.info("Updated {} payments for booking ID {} to status {}", updatedCount, bookingId, newStatus);
        return updatedCount;
    }
    
    @Transactional
    public int updatePaymentStatusByFacilityId(Long facilityId, String newStatus) {
        logger.info("Updating payments for booking ID {} to status {}", facilityId, newStatus);
        
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = paymentRepository.updatePaymentStatusByFacilityId(facilityId, newStatus, now);
        
        logger.info("Updated {} payments for booking ID {} to status {}", updatedCount, facilityId, newStatus);
        return updatedCount;
    }
    
}