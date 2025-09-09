package com.pitchplease.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pitchplease.gateway.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

        private static final List<String> PUBLIC_ENDPOINTS = List.of(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh_token",
                        "/api/auth/logout");

        private final JwtAuthenticationFilter jwtAuthFilter;

        @Value("${microservice.facility-discovery-service.url}")
        private String facilityDiscoveryServiceUrl;

        @Value("${microservice.facility-management-service.url}")
        private String facilityManagementServiceUrl;

        @Value("${microservice.booking-service.url}")
        private String bookingServiceUrl;

        @Value("${microservice.user-service.url}")
        private String userServiceUrl;

        @Value("${microservice.auth-service.url}")
        private String authServiceUrl;

        @Value("${microservice.payment-service.url}")
        private String paymentServiceUrl;

        @Bean
        public RouteLocator routes(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Facility Discovery routes
                                .route("facility_discovery_service", r -> r
                                                .path("/api/facility_details/**", "/api/facilities/**")
                                                .filters(f -> f.filter(jwtAuthFilter
                                                                .apply(new JwtAuthenticationFilter.Config()
                                                                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                                                .uri(facilityDiscoveryServiceUrl))

                                // Other service routes
                                .route("booking_service", r -> r
                                                .path("/api/bookings/**", "/api/payments/create")
                                                .filters(f -> f.filter(jwtAuthFilter
                                                                .apply(new JwtAuthenticationFilter.Config()
                                                                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                                                .uri(bookingServiceUrl))
                                // specifically need /api/payments/create here
                                // when payment is successful, entries are made to both the payment and booking
                                // tables
                                .route("user_service", r -> r
                                                .path("/api/users/**")
                                                .filters(f -> f.filter(jwtAuthFilter
                                                                .apply(new JwtAuthenticationFilter.Config()
                                                                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                                                .uri("lb://user-service"))

                                .route("auth_service", r -> r
                                                .path("/api/auth/**")
                                                .filters(f -> f.filter(jwtAuthFilter
                                                                .apply(new JwtAuthenticationFilter.Config()
                                                                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                                                .uri("lb://auth-service"))

                                .route("payment_service", r -> r
                                                .path("/api/payments/**")
                                                .filters(f -> f.filter(jwtAuthFilter
                                                                .apply(new JwtAuthenticationFilter.Config()
                                                                                .setPublicEndpoints(PUBLIC_ENDPOINTS))))
                                                .uri(paymentServiceUrl))

                                .build();
        }
}