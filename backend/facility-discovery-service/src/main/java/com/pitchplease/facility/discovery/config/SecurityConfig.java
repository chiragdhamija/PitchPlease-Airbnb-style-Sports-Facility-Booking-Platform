package com.pitchplease.facility.discovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/all", "/search", "/create","/user_facilities","/delete","/update", "/details/**", "/reviews/**", "/reviews/create/**", "/reviews/delete/**").permitAll()
            .anyExchange().authenticated()
            )
            .build();
    }
}