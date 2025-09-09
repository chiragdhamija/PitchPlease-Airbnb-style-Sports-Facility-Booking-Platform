package com.pitchplease.payment.strategy.impl;

// import com.pitchplease.payment.model.dto.PaymentDto;
import com.pitchplease.payment.model.dto.PaymentDto;
import com.pitchplease.payment.strategy.PaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PayPal payment strategy implementation
 */
@Component
public class PayPalPaymentStrategy implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PayPalPaymentStrategy.class);
    
    @Override
    public PaymentDto processPayment(PaymentDto paymentRequest) {
        logger.info("Processing PayPal payment of {} for booking ID: {}", 
                paymentRequest.getAmount(), paymentRequest.getBookingId());
        
        // In a real implementation, this would integrate with PayPal's API
        // Here we're simulating successful payment processing
        
        String transactionId = "pp_" + UUID.randomUUID().toString().substring(0, 10);
        
        PaymentDto response = new PaymentDto();
        response.setBookingId(paymentRequest.getBookingId());
        response.setPaymentStatus("COMPLETED");
        response.setTransactionId(transactionId);
        response.setCreatedAt(LocalDateTime.now());
        
        logger.info("PayPal payment processed successfully with transaction ID: {}", transactionId);
        
        return response;
    }
    
    @Override
    public String getPaymentMethod() {
        return "PayPal";
    }
}