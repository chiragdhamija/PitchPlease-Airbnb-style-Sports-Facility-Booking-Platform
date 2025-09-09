#!/bin/bash

# PitchPlease Complete Microservices Setup Script - FIXED
# This script creates microservices using Spring Initializr for Java 17
# and sets up all the necessary directory structure

# Create the base backend directory if it doesn't exist
mkdir -p backend
cd backend

# Function to create a Spring Boot project using curl
create_service() {
  SERVICE_NAME=$1
  PACKAGE_PATH=${2//./\/}  # Convert dots to slashes for directory path
  PACKAGE_NAME="com.pitchplease.$2"
  GROUP_ID="com.pitchplease"
  ARTIFACT_ID=$1
  
  echo "Creating $SERVICE_NAME..."
  
  # Use curl to fetch from Spring Initializr
  curl https://start.spring.io/starter.tgz \
    -d type=maven-project \
    -d language=java \
    -d bootVersion=3.3.0 \
    -d baseDir=$SERVICE_NAME \
    -d groupId=$GROUP_ID \
    -d artifactId=$ARTIFACT_ID \
    -d name=$SERVICE_NAME \
    -d packageName=$PACKAGE_NAME \
    -d packaging=jar \
    -d javaVersion=17 \
    -d dependencies=web,data-jpa,postgresql,lombok,validation,security \
    -o temp.tgz
  
  tar -xzf temp.tgz
  rm temp.tgz
  
  # Create standard directory structure
  # Use PACKAGE_PATH to create correct directory structure
  BASE_DIR="$SERVICE_NAME/src/main/java/com/pitchplease/$PACKAGE_PATH"
  
  # Create directory structure
  mkdir -p $BASE_DIR/controller
  mkdir -p $BASE_DIR/service/impl
  mkdir -p $BASE_DIR/repository
  mkdir -p $BASE_DIR/model/entity
  mkdir -p $BASE_DIR/model/dto
  mkdir -p $BASE_DIR/mapper
  mkdir -p $BASE_DIR/exception
  mkdir -p $BASE_DIR/config
  mkdir -p $BASE_DIR/security
  mkdir -p $BASE_DIR/util
  
  # Create application.yml with PostgreSQL configuration
  cat > $SERVICE_NAME/src/main/resources/application.yml << EOF
spring:
  application:
    name: $SERVICE_NAME
  datasource:
    url: jdbc:postgresql://localhost:5432/pitchplease_${2//./\\_}
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  security:
    user:
      name: admin
      password: admin

server:
  port: 0  # Random port assignment, will be registered with discovery service

# Customize application settings
pitchplease:
  app:
    jwtSecret: pitchpleaseSecretKey
    jwtExpirationMs: 86400000
EOF

  # Create empty files for the service
  setup_service_files $SERVICE_NAME $PACKAGE_PATH
  
  echo "$SERVICE_NAME service created successfully!"
}

# Function to create service-specific files
setup_service_files() {
  SERVICE_NAME=$1
  PACKAGE_PATH=$2
  BASE_PATH="$SERVICE_NAME/src/main/java/com/pitchplease/$PACKAGE_PATH"
  
  # Create base files
  
  # Exception files
  touch "$BASE_PATH/exception/ResourceNotFoundException.java"
  touch "$BASE_PATH/exception/GlobalExceptionHandler.java"
  
  # Config files
  touch "$BASE_PATH/config/WebConfig.java"
  touch "$BASE_PATH/config/SecurityConfig.java"
  
  # Security files
  touch "$BASE_PATH/security/JwtTokenProvider.java"
  touch "$BASE_PATH/security/JwtAuthenticationFilter.java"
  
  # Util files
  touch "$BASE_PATH/util/AppConstants.java"
  
  # Create service-specific files based on the service name
  case "$SERVICE_NAME" in
    "user-service")
      # Controller files
      touch "$BASE_PATH/controller/UserController.java"
      touch "$BASE_PATH/controller/AuthController.java"
      
      # Service interface files
      touch "$BASE_PATH/service/UserService.java"
      touch "$BASE_PATH/service/AuthService.java"
      
      # Service implementation files
      touch "$BASE_PATH/service/impl/UserServiceImpl.java"
      touch "$BASE_PATH/service/impl/AuthServiceImpl.java"
      
      # Repository files
      touch "$BASE_PATH/repository/UserRepository.java"
      touch "$BASE_PATH/repository/RoleRepository.java"
      
      # Entity files
      touch "$BASE_PATH/model/entity/User.java"
      touch "$BASE_PATH/model/entity/Role.java"
      
      # Dto files
      touch "$BASE_PATH/model/dto/UserDto.java"
      touch "$BASE_PATH/model/dto/LoginDto.java"
      touch "$BASE_PATH/model/dto/RegisterDto.java"
      touch "$BASE_PATH/model/dto/JwtResponseDto.java"
      
      # Mapper files
      touch "$BASE_PATH/mapper/UserMapper.java"
      ;;
      
    "facility-management-service")
      # Controller files
      touch "$BASE_PATH/controller/FacilityController.java"
      touch "$BASE_PATH/controller/EquipmentController.java"
      
      # Service interface files
      touch "$BASE_PATH/service/FacilityService.java"
      touch "$BASE_PATH/service/EquipmentService.java"
      
      # Service implementation files
      touch "$BASE_PATH/service/impl/FacilityServiceImpl.java"
      touch "$BASE_PATH/service/impl/EquipmentServiceImpl.java"
      
      # Repository files
      touch "$BASE_PATH/repository/FacilityRepository.java"
      touch "$BASE_PATH/repository/EquipmentRepository.java"
      
      # Entity files
      touch "$BASE_PATH/model/entity/Facility.java"
      touch "$BASE_PATH/model/entity/Equipment.java"
      touch "$BASE_PATH/model/entity/Address.java"
      touch "$BASE_PATH/model/entity/AvailabilitySchedule.java"
      
      # Dto files
      touch "$BASE_PATH/model/dto/FacilityDto.java"
      touch "$BASE_PATH/model/dto/FacilityCreateDto.java"
      touch "$BASE_PATH/model/dto/EquipmentDto.java"
      touch "$BASE_PATH/model/dto/AddressDto.java"
      
      # Mapper files
      touch "$BASE_PATH/mapper/FacilityMapper.java"
      touch "$BASE_PATH/mapper/EquipmentMapper.java"
      ;;
      
    "facility-discovery-service")
      # Controller files
      touch "$BASE_PATH/controller/SearchController.java"
      touch "$BASE_PATH/controller/ReviewController.java"
      
      # Service interface files
      touch "$BASE_PATH/service/SearchService.java"
      touch "$BASE_PATH/service/ReviewService.java"
      
      # Service implementation files
      touch "$BASE_PATH/service/impl/SearchServiceImpl.java"
      touch "$BASE_PATH/service/impl/ReviewServiceImpl.java"
      
      # Repository files
      touch "$BASE_PATH/repository/ReviewRepository.java"
      touch "$BASE_PATH/repository/FacilityViewRepository.java"
      
      # Entity files
      touch "$BASE_PATH/model/entity/Review.java"
      touch "$BASE_PATH/model/entity/Rating.java"
      touch "$BASE_PATH/model/entity/FacilityView.java"
      
      # Dto files
      touch "$BASE_PATH/model/dto/ReviewDto.java"
      touch "$BASE_PATH/model/dto/SearchCriteriaDto.java"
      touch "$BASE_PATH/model/dto/FacilityViewDto.java"
      touch "$BASE_PATH/model/dto/RatingDto.java"
      
      # Mapper files
      touch "$BASE_PATH/mapper/ReviewMapper.java"
      touch "$BASE_PATH/mapper/FacilityViewMapper.java"
      ;;
      
    "booking-service")
      # Controller files
      touch "$BASE_PATH/controller/BookingController.java"
      
      # Service interface files
      touch "$BASE_PATH/service/BookingService.java"
      touch "$BASE_PATH/service/NotificationService.java"
      
      # Service implementation files
      touch "$BASE_PATH/service/impl/BookingServiceImpl.java"
      touch "$BASE_PATH/service/impl/NotificationServiceImpl.java"
      
      # Repository files
      touch "$BASE_PATH/repository/BookingRepository.java"
      touch "$BASE_PATH/repository/TimeSlotRepository.java"
      
      # Entity files
      touch "$BASE_PATH/model/entity/Booking.java"
      touch "$BASE_PATH/model/entity/TimeSlot.java"
      touch "$BASE_PATH/model/entity/Reservation.java"
      
      # Dto files
      touch "$BASE_PATH/model/dto/BookingDto.java"
      touch "$BASE_PATH/model/dto/BookingCreateDto.java"
      touch "$BASE_PATH/model/dto/TimeSlotDto.java"
      
      # Mapper files
      touch "$BASE_PATH/mapper/BookingMapper.java"
      touch "$BASE_PATH/mapper/TimeSlotMapper.java"
      ;;
      
    "payment-service")
      # Controller files
      touch "$BASE_PATH/controller/PaymentController.java"
      
      # Service interface files
      touch "$BASE_PATH/service/PaymentService.java"
      touch "$BASE_PATH/service/RefundService.java"
      
      # Service implementation files
      touch "$BASE_PATH/service/impl/PaymentServiceImpl.java"
      touch "$BASE_PATH/service/impl/RefundServiceImpl.java"
      
      # Repository files
      touch "$BASE_PATH/repository/PaymentRepository.java"
      touch "$BASE_PATH/repository/TransactionRepository.java"
      
      # Entity files
      touch "$BASE_PATH/model/entity/Payment.java"
      touch "$BASE_PATH/model/entity/Transaction.java"
      touch "$BASE_PATH/model/entity/PaymentMethod.java"
      
      # Dto files
      touch "$BASE_PATH/model/dto/PaymentDto.java"
      touch "$BASE_PATH/model/dto/PaymentRequestDto.java"
      touch "$BASE_PATH/model/dto/PaymentResponseDto.java"
      touch "$BASE_PATH/model/dto/RefundRequestDto.java"
      
      # Mapper files
      touch "$BASE_PATH/mapper/PaymentMapper.java"
      ;;
      
    "api-gateway")
      # Controller files
      touch "$BASE_PATH/controller/FallbackController.java"
      
      # Config files
      touch "$BASE_PATH/config/RouteConfig.java"
      touch "$BASE_PATH/config/SecurityConfig.java"
      touch "$BASE_PATH/config/FilterConfig.java"
      
      # Filter files
      mkdir -p "$BASE_PATH/filter"
      touch "$BASE_PATH/filter/AuthenticationFilter.java"
      touch "$BASE_PATH/filter/LoggingFilter.java"
      ;;
  esac
}

# Create API Gateway
create_service "api-gateway" "gateway"

# Create all microservices
create_service "user-service" "user"
create_service "facility-management-service" "facility.management"
create_service "facility-discovery-service" "facility.discovery"
create_service "booking-service" "booking"
create_service "payment-service" "payment"

# Create a parent pom.xml
cat > pom.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.pitchplease</groupId>
    <artifactId>pitchplease-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>PitchPlease Parent</name>
    <description>Parent project for PitchPlease microservices</description>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <modules>
        <module>api-gateway</module>
        <module>user-service</module>
        <module>facility-management-service</module>
        <module>facility-discovery-service</module>
        <module>booking-service</module>
        <module>payment-service</module>
    </modules>
    
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Create a PostgreSQL setup script
cat > db-setup.sql << EOF
-- PitchPlease database setup script

-- Create databases for each microservice
CREATE DATABASE pitchplease_user;
CREATE DATABASE pitchplease_facility_management;
CREATE DATABASE pitchplease_facility_discovery;
CREATE DATABASE pitchplease_booking;
CREATE DATABASE pitchplease_payment;

-- Connect to user database and create schema
\c pitchplease_user
CREATE SCHEMA IF NOT EXISTS user_schema;

-- Connect to facility management database and create schema
\c pitchplease_facility_management
CREATE SCHEMA IF NOT EXISTS facility_schema;

-- Connect to facility discovery database and create schema
\c pitchplease_facility_discovery
CREATE SCHEMA IF NOT EXISTS discovery_schema;

-- Connect to booking database and create schema
\c pitchplease_booking
CREATE SCHEMA IF NOT EXISTS booking_schema;

-- Connect to payment database and create schema
\c pitchplease_payment
CREATE SCHEMA IF NOT EXISTS payment_schema;

-- Note: Actual tables will be created by JPA/Hibernate
-- This script only creates the databases and schemas
EOF

# Create Docker Compose for PostgreSQL (placed in the backend directory)
cat > docker-compose.yml << EOF
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: pitchplease-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_MULTIPLE_DATABASES: "pitchplease_user,pitchplease_facility_management,pitchplease_facility_discovery,pitchplease_booking,pitchplease_payment"
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./db-setup.sql:/docker-entrypoint-initdb.d/db-setup.sql
    networks:
      - pitchplease-network

networks:
  pitchplease-network:
    driver: bridge

volumes:
  postgres-data:
EOF

echo "
PitchPlease microservices structure has been created successfully!

The structure includes:
- API Gateway
- User Service
- Facility Management Service
- Facility Discovery Service
- Booking Service
- Payment Service

Each service follows the layered architecture with:
- Controller layer (API endpoints)
- Service layer (interfaces and implementations)
- Repository layer (data access)
- Model layer (entities and Dtos)
- Mapper layer (entity-Dto conversions)
- Exception handling
- Configuration classes
- Security components

For database setup:
1. A db-setup.sql file has been created in the backend directory
2. A docker-compose.yml file for PostgreSQL has been created in the backend directory
3. Each service has its own database configuration in application.yml

To start the PostgreSQL database:
cd backend
docker-compose up -d

To run a specific service:
cd backend/[service-name]
./mvnw spring-boot:run
"