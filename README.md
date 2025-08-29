# PitchPlease – Sports Facility Booking Platform

*PitchPlease* is a modular sports facility booking platform that connects players and facility providers seamlessly. This project follows a microservices-based architecture with support for secure payments, real-time availability, role-based access, and performance monitoring.


## Project Overview

### Key Features

•⁠  ⁠User registration, login/logout, and profile management
•⁠  ⁠Facility listings  and calendar-based pricing
•⁠  ⁠Filtering of facilities
•⁠  ⁠Booking creation, cancellation, and tracking
•⁠  ⁠Secure payments
•⁠  ⁠Modular microservices built using Spring Boot (Java)


##  Prerequisites

Before running the project, ensure the following tools are installed on your system:

•⁠  ⁠Java 
•⁠  ⁠Maven 
•⁠  ⁠Docker & Docker Compose



## Running the Project

To build and launch the complete backend with Docker:

### Step 1: Navigate to the Backend Folder

⁠ bash
cd backend
 ⁠

### Step 2: Run the Startup Script

⁠ bash
./run.sh
 ⁠

This script performs the following:

1.⁠ ⁠Detects your operating system (for compatibility with macOS/Linux)
2.⁠ ⁠Builds all microservices using Maven
3.⁠ ⁠Shuts down any existing Docker containers
4.⁠ ⁠Rebuilds and spins up the services via Docker Compose

	⁠*Note:* On Linux systems, ⁠ sudo ⁠ is automatically used with Docker.



## Skipping Tests

The Maven build skips running tests using ⁠ -DskipTests ⁠ for faster builds. To run with tests, modify the script:

⁠ bash
mvn clean package
 ⁠



##  Troubleshooting

•⁠  ⁠Ensure Docker Daemon is running
•⁠  ⁠Use ⁠ docker compose logs ⁠ to inspect service startup issues
•⁠  ⁠Clean volumes if PostgreSQL causes issues:
  ⁠ bash
  docker volume rm pitchplease_postgres-data
   ⁠



##  Directory Structure


backend/
├── booking-service/
├── user-service/
├── facility-discovery-service/
├── payment-service/
├── api-gateway/
├── docker-compose.yml
├── auth-service/
├── eurekaserver/
└── run.sh
