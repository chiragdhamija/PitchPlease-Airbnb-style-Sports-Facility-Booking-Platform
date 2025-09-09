package com.pitchplease.payment.strategy;

// import com.pitchplease.payment.model.dto.PaymentRequestDto;
import com.pitchplease.payment.model.dto.PaymentDto;

/**
 * Payment Strategy interface - defines the contract for all payment processors
 */
public interface PaymentStrategy {
    
    /**
     * Process a payment with the specific payment method
     * 
     * @param paymentRequest Payment request details
     * @return Payment response with transaction details
     */
    PaymentDto processPayment(PaymentDto paymentRequest);
    
    /**
     * Get the type of payment method this strategy handles
     * 
     * @return The payment method name
     */
    String getPaymentMethod();
}