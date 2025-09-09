package com.pitchplease.payment.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the Payment microservice
 */
@Configuration
public class PaymentConfig {

    /**
     * Create ModelMapper bean for DTO-Entity conversions
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    /**
     * Create RestTemplate bean for HTTP requests
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}