package com.pitchplease.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for RestTemplate bean
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Create a RestTemplate bean for making HTTP requests to microservices
     * 
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}