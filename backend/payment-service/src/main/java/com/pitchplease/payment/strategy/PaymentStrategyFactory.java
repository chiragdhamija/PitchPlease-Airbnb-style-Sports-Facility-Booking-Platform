package com.pitchplease.payment.strategy;

import com.pitchplease.payment.strategy.impl.BankTransferPaymentStrategy;
import com.pitchplease.payment.strategy.impl.CreditCardPaymentStrategy;
import com.pitchplease.payment.strategy.impl.PayPalPaymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating payment strategy instances based on payment method
 */
@Component
public class PaymentStrategyFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentStrategyFactory.class);
    
    private final Map<String, PaymentStrategy> strategies = new HashMap<>();
    
    /**
     * Constructor that auto-registers all available payment strategies
     * 
     * @param strategyList List of all payment strategy implementations
     */
    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        strategyList.forEach(strategy -> {
            strategies.put(strategy.getPaymentMethod(), strategy);
            logger.info("Registered payment strategy for method: {}", strategy.getPaymentMethod());
        });
    }
    
    /**
     * Get the appropriate payment strategy for the specified payment method
     * 
     * @param paymentMethod The payment method to use
     * @return The corresponding payment strategy
     * @throws IllegalArgumentException if payment method is not supported
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        
        if (strategy == null) {
            logger.error("No payment strategy found for method: {}", paymentMethod);
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        
        return strategy;
    }
    
    /**
     * Get all supported payment methods
     * 
     * @return List of all supported payment methods
     */
    public List<String> getSupportedMethods() {
        return List.copyOf(strategies.keySet());
    }
}