package com.pitchplease.payment.strategy.impl;

import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentDto;
import com.pitchplease.payment.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Credit Card payment strategy implementation
 */
@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(CreditCardPaymentStrategy.class);
    
    @Override
    public PaymentDto processPayment(PaymentDto paymentRequest) {
        logger.info("Processing Credit Card payment of {} for booking ID: {}", 
                paymentRequest.getAmount(), paymentRequest.getBookingId());
        
        // In a real implementation, this would integrate with a payment gateway
        // Here we're simulating successful payment processing
        
        String transactionId = "cc_" + UUID.randomUUID().toString().substring(0, 10);
        
        PaymentDto response = new PaymentDto();
        response.setBookingId(paymentRequest.getBookingId());
        response.setPaymentStatus("COMPLETED");
        response.setTransactionId(transactionId);
        response.setCreatedAt(LocalDateTime.now());
        
        logger.info("Credit Card payment processed successfully with transaction ID: {}", transactionId);
        
        return response;
    }
    
    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }
}