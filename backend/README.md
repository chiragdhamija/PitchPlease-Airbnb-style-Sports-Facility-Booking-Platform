# PitchPlease Backend

This directory contains the backend services for the PitchPlease application, a microservices-based system for sports facility management and booking.

## Services

The backend consists of the following microservices:
- API Gateway - Main entry point (port 8080)
- User Service - Manages user accounts and authentication
- Facility Management Service - Handles sports facilities information
- Booking Service - Manages facility bookings
- Payment Service - Handles payment processing
- PostgreSQL Database - Stores data for all services

## Prerequisites

- Docker and Docker Compose installed on your system
- Sudo privileges (for running the commands below)

## Running the Application

```bash
mvn clean package -DskipTests
```

To start all services, run the following command from the backend directory:

```bash
sudo docker compose up
```

This will:
1. Build all service images if they don't exist
2. Create and start containers for all services
3. Set up the PostgreSQL database with required schemas
4. Connect all services to the same network

You can add the `-d` flag to run in detached mode:

```bash
sudo docker compose up -d
```

Once started, the API Gateway will be accessible at http://localhost:8080

## Stopping the Application

To stop and remove all containers, networks, and volumes, run:

```bash
sudo docker compose down -v --remove-orphans
```

This command:
- Stops all running containers
- Removes all containers, networks created by docker-compose
- Removes volumes (`-v` flag)
- Removes any containers not defined in the compose file but connected to networks created by it (`--remove-orphans` flag)

## Service Endpoints

- API Gateway: http://localhost:8080
- Facility Management Service: (internal) port 8090
- User Service: (internal) port 8091
- Booking Service: (internal) port 8092
- Payment Service: (internal) port 8093
- Facility Discovery Service: (internal) port 8094
- PostgreSQL: localhost:5432 (credentials: postgres/postgres)
