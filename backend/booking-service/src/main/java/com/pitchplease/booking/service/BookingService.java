package com.pitchplease.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplease.booking.mapper.BookingMapper;
import com.pitchplease.booking.model.dto.BookingDto;
import com.pitchplease.booking.model.entity.Booking;
import com.pitchplease.booking.repository.BookingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    /**
     * Get all bookings
     * 
     * @return List of all bookings
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get booking by ID
     * 
     * @param id Booking ID
     * @return Optional containing the booking if found
     */
    @Transactional(readOnly = true)
    public Optional<BookingDto> getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto);
    }

    /**
     * Get bookings by user ID
     * 
     * @param userId User ID
     * @return List of bookings made by the user
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by facility ID
     * 
     * @param facilityId Facility ID
     * @return List of bookings for the facility
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByFacilityId(Long facilityId) {
        return bookingRepository.findByFacilityId(facilityId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new booking
     * 
     * @param bookingDto Booking to be created
     * @return Created booking
     * @throws RuntimeException if the facility is already booked for the requested
     *                          time period
     */
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto) {
        logger.info("Service creating booking for facility ID: {} from {} to {}",
                bookingDto.getFacilityId(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime());

        // Ensure status is set
        if (bookingDto.getStatus() == null) {
            bookingDto.setStatus("pending");
        }

        // If no booking group ID is provided, create one
        if (bookingDto.getBookingGroupId() == null) {
            bookingDto.setBookingGroupId(System.currentTimeMillis());
            logger.info("Generated new booking group ID: {}", bookingDto.getBookingGroupId());
        }

        Booking entity = bookingMapper.toEntity(bookingDto);
        entity.setBookingId(null); // Ensure we're creating a new entity

        try {
            Booking savedEntity = bookingRepository.save(entity);
            logger.info("Created new booking with ID: {} in group: {}",
                    savedEntity.getBookingId(), savedEntity.getBookingGroupId());
            return bookingMapper.toDto(savedEntity);
        } catch (Exception e) {
            logger.error("Error saving booking to database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save booking: " + e.getMessage(), e);
        }
    }

    /**
     * Get all available booking slots for a facility on a specific date
     * 
     * @param facilityId The facility ID
     * @param date       The date to check
     * @return List of time slots with availability information
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableTimeSlots(Long facilityId, LocalDate date) {
        logger.info("Service getting available time slots for facility ID: {} on date: {}",
                facilityId, date);

        try {
            // Get all non-cancelled bookings for this facility on the given date
            List<BookingDto> bookings = getBookingsByFacilityId(facilityId).stream()
                    .filter(booking -> {
                        LocalDate bookingDate = booking.getStartTime().toLocalDate();
                        return bookingDate.equals(date) && !booking.getStatus().equals("cancelled");
                    })
                    .collect(Collectors.toList());

            logger.info("Found {} bookings for facility ID: {} on date: {}",
                    bookings.size(), facilityId, date);

            // Create a boolean array to track which hours are booked
            boolean[] bookedHours = new boolean[24];

            // Mark hours as booked based on existing bookings
            for (BookingDto booking : bookings) {
                LocalDateTime start = booking.getStartTime();
                LocalDateTime end = booking.getEndTime();

                // Calculate the hour indices
                int startHour = start.getHour();
                int endHour = end.getHour();

                // Handle case where booking ends on next day
                if (end.toLocalDate().isAfter(date)) {
                    endHour = 24;
                }

                // Mark all hours covered by this booking as booked
                for (int hour = startHour; hour < endHour; hour++) {
                    bookedHours[hour] = true;
                }
            }

            // Create the list of slots
            List<Map<String, Object>> slots = new ArrayList<>(24);
            for (int hour = 0; hour < 24; hour++) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("startHour", hour);
                slot.put("endHour", hour + 1);
                slot.put("available", !bookedHours[hour]);
                slots.add(slot);
            }

            return slots;

        } catch (Exception e) {
            logger.error("Error determining available time slots: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to determine available time slots: " + e.getMessage(), e);
        }
    }

    /**
     * Get all bookings for a facility on a specific date
     * 
     * @param facilityId The facility ID
     * @param date       The date to check
     * @return List of bookings on that date
     */
    private List<BookingDto> getBookingsForFacilityOnDate(Long facilityId, LocalDate date) {
        // Calculate the start and end of the day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // Get all bookings for this facility
        List<BookingDto> allBookings = getBookingsByFacilityId(facilityId);

        // Filter bookings that fall on the requested date and not cancelled
        return allBookings.stream()
                .filter(booking -> !booking.getStatus().equals("cancelled") &&
                        booking.getStartTime().isEqual(startOfDay)
                        || booking.getStartTime().isAfter(startOfDay) &&
                                booking.getStartTime().isBefore(endOfDay))
                .collect(Collectors.toList());
    }

    /**
     * Check if two time ranges overlap
     * 
     * @param start1 Start time of first range
     * @param end1   End time of first range
     * @param start2 Start time of second range
     * @param end2   End time of second range
     * @return true if the ranges overlap, false otherwise
     */
    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
            LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Cancel all bookings in a booking group
     * 
     * @param bookingGroupId Booking group ID
     * @return Number of bookings cancelled
     * @throws RuntimeException if no bookings found for the group
     */
    @Transactional
    public int cancelBookingGroup(Long bookingGroupId) {
        logger.info("Cancelling all bookings in group: {}", bookingGroupId);

        int cancelledCount = bookingRepository.deleteBookingsByGroupId(bookingGroupId);
    
    if (cancelledCount > 0) {
        logger.info("Cancelled {} bookings in group: {}", cancelledCount, bookingGroupId);
    } else {
        logger.info("No bookings found for group: {}", bookingGroupId);
    }
    
    return cancelledCount;
    }

    /**
     * Get all bookings with a specific status
     * 
     * @param status Booking status (e.g., 'pending', 'confirmed', 'cancelled')
     * @return List of bookings with the specified status
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserIdAndStatus(Integer userId, String status) {
        return bookingRepository.findByUserIdAndStatus(userId, status).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if a facility is available for a specific time period
     * 
     * @param facilityId Facility ID
     * @param startTime  Start time of the proposed booking
     * @param endTime    End time of the proposed booking
     * @return true if the facility is available, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isFacilityAvailable(Long facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                facilityId, startTime, endTime);

        return conflictingBookings.isEmpty();
    }

    /**
     * Get all bookings by booking group ID
     * 
     * @param bookingGroupId Booking group ID
     * @return List of bookings in the group
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByGroupId(Long bookingGroupId) {
        logger.info("Fetching bookings for group ID: {}", bookingGroupId);
        return bookingRepository.findByBookingGroupId(bookingGroupId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
    
}