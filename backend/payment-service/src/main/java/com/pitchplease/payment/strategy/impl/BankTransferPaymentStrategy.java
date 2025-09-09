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
 * Bank Transfer payment strategy implementation
 */
@Component
public class BankTransferPaymentStrategy implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(BankTransferPaymentStrategy.class);
    
    @Override
    public PaymentDto processPayment(PaymentDto paymentRequest) {
        logger.info("Processing Bank Transfer payment of {} for booking ID: {}", 
                paymentRequest.getAmount(), paymentRequest.getBookingId());
        
        String transactionId = "bt_" + UUID.randomUUID().toString().substring(0, 10);
        String status = "COMPLETED"; // Bank transfers might start as pending
        
        // Simulate immediate confirmation for this example
        if ("COMPLETED".equals(paymentRequest.getPaymentStatus())) {
            status = "COMPLETED";
        }
        
        PaymentDto response = new PaymentDto();
        response.setBookingId(paymentRequest.getBookingId());
        response.setPaymentStatus(status);
        response.setTransactionId(transactionId);
        response.setCreatedAt(LocalDateTime.now());
        
        logger.info("Bank Transfer payment processed with status {} and transaction ID: {}", 
                status, transactionId);
        
        return response;
    }
    
    @Override
    public String getPaymentMethod() {
        return "Bank Transfer";
    }
}