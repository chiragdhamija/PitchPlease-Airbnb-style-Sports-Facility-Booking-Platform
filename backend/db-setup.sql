-- Create one database for all microservices
CREATE DATABASE pitchplease;

-- Connect to database
\c pitchplease

-- Users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE invalid_tokens (
    ID VARCHAR(36) PRIMARY KEY,
    TOKEN_ID VARCHAR(255),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP
);


-- Facilities table
CREATE TABLE facilities (
    facility_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(50) NOT NULL,
    facility_type VARCHAR(50) NOT NULL, -- e.g., football field, tennis court, etc.
    hourly_rate DECIMAL(10, 2) NOT NULL,
    owner_id INT REFERENCES users(user_id)
);

-- Bookings table
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    booking_group_id BIGINT NOT NULL, -- Groups related time slots together
    facility_id INT NOT NULL REFERENCES facilities(facility_id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(user_id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(1000) NOT NULL DEFAULT 'CONFIRMED', -- confirmed, cancelled
    
    -- Enforce that start time is before end time
    CONSTRAINT valid_booking_times CHECK (start_time < end_time)
);

-- Reviews table
CREATE TABLE reviews (
    review_id SERIAL PRIMARY KEY,
    facility_id INT NOT NULL REFERENCES facilities(facility_id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    user_name VARCHAR(100) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- One user can leave only one review per facility
    CONSTRAINT unique_user_facility_review UNIQUE (user_id, facility_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    booking_group_id INT NOT NULL,
    user_id INT NOT NULL REFERENCES users(user_id),

    user_name VARCHAR(50) NOT NULL,
    facility_id INT NOT NULL,
    facility_name VARCHAR(100) NOT NULL,
    addons_string VARCHAR(200) DEFAULT NULL,

    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- e.g., Credit Card, PayPal, Bank Transfer
    payment_status VARCHAR(1000) NOT NULL, -- e.g., COMPLETED, CANCELLED, REFUNDED
    transaction_id VARCHAR(100), -- External payment processor reference (if applicable)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create basic indexes
CREATE INDEX idx_bookings_facility_id ON bookings(facility_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_booking_group_id ON bookings(booking_group_id);
CREATE INDEX idx_reviews_facility_id ON reviews(facility_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_payments_booking_group_id ON payments(booking_group_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(payment_status);

-- Populate users table
INSERT INTO users (username, email, password_hash) VALUES
('john_doe', 'john@example.com', '$2a$10$XQhF5XeGQWrffCcbJcxDVedoQILBKCPVSClOkCqsYc/cNnRZ9jNzy'), -- 'password123'
('jane_smith', 'jane@example.com', '$2a$10$J5kX1XeGQWrfdCcbJcxDVeiuQILBKCPVSClOkCqsYc/cNnRZ9jNzy'), -- 'securepass'
('sam_wilson', 'sam@example.com', '$2a$10$LQhF5XeGQWrffCcbJcxDVeBuQILBKCPVSClOkCqsYc/cNnRZ9jNzy'); -- 'samspass'

-- Populate facilities table
INSERT INTO facilities (name, description, address, city, facility_type, hourly_rate, owner_id) VALUES
('Central Football Field', 'Professional football field with floodlights', '123 Main St', 'New York', 'football', 100.00, 1),
('Downtown Tennis Court', 'Clay tennis court with amenities', '456 Park Ave', 'New York', 'tennis', 45.00, 1),
('Elite Basketball Arena', 'Indoor basketball court with seating', '789 Broadway', 'Boston', 'basketball', 80.00, 2),
('Riverside Soccer Field', 'Open-air soccer field near the river', '321 River Rd', 'Chicago', 'football', 75.00, 3),
('Community Swimming Pool', 'Olympic-sized swimming pool', '654 Ocean Dr', 'Miami', 'swimming', 60.00, 2);

-- -- Populate bookings table
-- INSERT INTO bookings (facility_id, user_id, start_time, end_time, total_price, status) VALUES
-- (1, 2, '2025-04-20 14:00:00', '2025-04-20 16:00:00', 200.00, 'confirmed'),
-- (2, 3, '2025-04-21 10:00:00', '2025-04-21 12:00:00', 90.00, 'confirmed'),
-- (3, 1, '2025-04-22 18:00:00', '2025-04-22 20:00:00', 160.00, 'pending'),
-- (4, 2, '2025-04-23 16:00:00', '2025-04-23 18:00:00', 150.00, 'confirmed'),
-- (5, 3, '2025-04-24 09:00:00', '2025-04-24 11:00:00', 120.00, 'pending'),
-- (1, 3, '2025-04-25 12:00:00', '2025-04-25 14:00:00', 200.00, 'cancelled');


-- Sample data for reviews
INSERT INTO reviews (facility_id, user_id, user_name, rating, comment, created_at) VALUES
(1, 2, 'jane_smith', 5, 'Excellent football field with great lighting for evening games.', '2025-04-01 15:30:00'),
(1, 3, 'sam_wilson', 4, 'Good facilities, but the changing rooms could be cleaner.', '2025-04-05 18:45:00'),
(2, 1, 'john_doe', 5, 'The tennis court was in perfect condition!', '2025-04-02 12:15:00'),
(2, 3, 'sam_wilson', 3, 'Decent court but expensive for what you get.', '2025-04-07 09:30:00'),
(3, 1, 'john_doe', 4, 'Great basketball court with good acoustics.', '2025-04-03 17:20:00'),
(3, 2, 'jane_smith', 5, 'Professional-level facilities, really impressed!', '2025-04-06 20:10:00'),
(4, 1, 'john_doe', 3, 'The field was muddy in some places.', '2025-04-04 14:45:00'),
(5, 1, 'john_doe', 5, 'Cleanest swimming pool I have ever used!', '2025-04-08 11:25:00'),
(5, 2, 'jane_smith', 4, 'Good temperature and not too crowded.', '2025-04-09 16:50:00');



-- Booking 1: Someone booked the Central Football Field (facility_id 1)
INSERT INTO bookings (booking_group_id, facility_id, user_id, start_time, end_time, total_price, status) VALUES
(10001, 1, 2, '2025-05-01 14:00:00', '2025-05-01 16:00:00', 200.00, 'CONFIRMED');

-- Booking 2: Someone booked the Downtown Tennis Court (facility_id 2)
INSERT INTO bookings (booking_group_id, facility_id, user_id, start_time, end_time, total_price, status) VALUES
(10002, 2, 3, '2025-05-02 10:00:00', '2025-05-02 12:00:00', 90.00, 'CONFIRMED');

-- Now, let's create 2 payment records corresponding to these bookings
INSERT INTO payments (booking_group_id, user_id, user_name, facility_id, facility_name, amount, payment_method, payment_status, transaction_id, created_at, updated_at) VALUES
(10001, 2, 'jane_smith', 1, 'Central Football Field', 200.00, 'Credit Card', 'COMPLETED', 'TXN-FF5678', '2025-04-20 09:15:00', '2025-04-20 09:15:00');

INSERT INTO payments (booking_group_id, user_id, user_name, facility_id, facility_name, amount, payment_method, payment_status, transaction_id, created_at, updated_at) VALUES
(10002, 3, 'sam_wilson', 2, 'Downtown Tennis Court', 90.00, 'PayPal', 'COMPLETED', 'TXN-PP9012', '2025-04-21 11:30:00', '2025-04-21 11:30:00');

-- Additional booking for Central Football Field (facility_id 1)
INSERT INTO bookings (booking_group_id, facility_id, user_id, start_time, end_time, total_price, status) VALUES
(10003, 1, 3, '2025-05-05 18:00:00', '2025-05-05 20:00:00', 200.00, 'CONFIRMED');

-- Additional booking for Downtown Tennis Court (facility_id 2)
INSERT INTO bookings (booking_group_id, facility_id, user_id, start_time, end_time, total_price, status) VALUES
(10004, 2, 2, '2025-05-06 14:00:00', '2025-05-06 16:00:00', 90.00, 'CONFIRMED');

-- Corresponding payment records for these bookings
INSERT INTO payments (booking_group_id, user_id, user_name, facility_id, facility_name, amount, payment_method, payment_status, transaction_id, created_at, updated_at) VALUES
(10003, 3, 'sam_wilson', 1, 'Central Football Field', 200.00, 'Bank Transfer', 'COMPLETED', 'TXN-BT3456', '2025-04-25 14:20:00', '2025-04-25 14:20:00');

INSERT INTO payments (booking_group_id, user_id, user_name, facility_id, facility_name, amount, payment_method, payment_status, transaction_id, created_at, updated_at) VALUES
(10004, 2, 'jane_smith', 2, 'Downtown Tennis Court', 90.00, 'Credit Card', 'COMPLETED', 'TXN-CC7890', '2025-04-26 09:45:00', '2025-04-26 09:45:00');