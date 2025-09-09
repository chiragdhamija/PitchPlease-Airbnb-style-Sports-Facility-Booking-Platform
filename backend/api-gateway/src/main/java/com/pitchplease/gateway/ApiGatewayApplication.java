package com.pitchplease.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ApiGatewayApplication {
    public static void main(String[] args) {
        System.setProperty("server.port", "8080");
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}