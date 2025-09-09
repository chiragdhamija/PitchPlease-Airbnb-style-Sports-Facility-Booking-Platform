package com.pitchplease.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point for the Product service Spring Boot application.
 * This application is a Eureka client that registers itself with a Eureka server.
 * The application is configured with the {@link SpringBootApplication} annotation.
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.pitchplease.userservice.model.user.entity"})
@EnableJpaRepositories(basePackages = {"com.pitchplease.userservice.repository"})
public class UserserviceApplication {

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args Command-line arguments passed during the application startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

}
