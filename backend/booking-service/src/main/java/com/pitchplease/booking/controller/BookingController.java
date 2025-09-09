package com.pitchplease.booking.controller;

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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pitchplease.booking.model.dto.BookingDto;
import com.pitchplease.booking.service.BookingService;

@RestController
@RequestMapping("/")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;


    /**
     * Get all available time slots for a facility on a specific date
     * 
     * @param facilityId The facility ID
     * @param date The date to check
     * @return ResponseEntity with available time slots
     */
    @GetMapping("/get_available_slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @RequestParam Long facilityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Fetching available time slots for facility ID: {} on date: {}", 
                facilityId, date);
        
        try {
            // Call the service method to get all available slots
            List<Map<String, Object>> availableSlots = bookingService.getAvailableTimeSlots(facilityId, date);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("facilityId", facilityId);
            response.put("date", date.toString());
            response.put("availableSlots", availableSlots);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching available time slots: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch available time slots: " + e.getMessage());
        }
    }

    /**
     * Get all bookings
     *
     * @return List of all bookings
     */
    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        logger.info("Fetching all bookings");
        List<BookingDto> bookings = bookingService.getAllBookings();
        logger.info("Found {} bookings", bookings.size());
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get booking by ID
     *
     * @param id Booking ID
     * @return Booking if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        logger.info("Fetching booking with ID: {}", id);
        Optional<BookingDto> booking = bookingService.getBookingById(id);

        if (booking.isPresent()) {
            return new ResponseEntity<>(booking.get(), HttpStatus.OK);
        } else {
            logger.warn("Booking with ID: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get bookings by user ID
     *
     * @param userId User ID
     * @return List of bookings made by the user
     */
    @GetMapping("/user")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@RequestParam Integer userId) {
        logger.info("Fetching bookings for user ID: {}", userId);
        List<BookingDto> bookings = bookingService.getBookingsByUserId(userId);
        logger.info("Found {} bookings for user ID: {}", bookings.size(), userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by facility ID
     *
     * @param facilityId Facility ID
     * @return List of bookings for the facility
     */
    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<BookingDto>> getBookingsByFacilityId(@PathVariable Long facilityId) {
        logger.info("Fetching bookings for facility ID: {}", facilityId);
        List<BookingDto> bookings = bookingService.getBookingsByFacilityId(facilityId);
        logger.info("Found {} bookings for facility ID: {}", bookings.size(), facilityId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Create a new booking with multiple time slots
     * 
     * @param requestData Map containing booking data from the gateway
     * @return Created booking details
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> requestData) {
        try {
            logger.info("Received booking request: {}", requestData);
            
            // Generate a shared booking group ID for all time slots
            Long bookingGroupId = System.currentTimeMillis();
            
            // Extract and convert all time slots to BookingDto objects
            List<BookingDto> bookingDtos = convertTimeSlotToBookingDtos(requestData, bookingGroupId);
            
            if (bookingDtos.isEmpty()) {
                logger.warn("No valid time slots provided for booking");
                return ResponseEntity.badRequest().body("No valid time slots provided for booking");
            }
            
            logger.info("Processing {} time slots for booking group {}", bookingDtos.size(), bookingGroupId);
            
            // Save all bookings and collect the results
            List<BookingDto> createdBookings = new ArrayList<>();
            for (BookingDto dto : bookingDtos) {
                BookingDto created = bookingService.createBooking(dto);
                createdBookings.add(created);
                logger.info("Created booking ID: {} in group: {}", created.getBookingId(), created.getBookingGroupId());
            }
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("bookingGroupId", bookingGroupId);
            response.put("userId", bookingDtos.get(0).getUserId());
            response.put("facilityId", bookingDtos.get(0).getFacilityId());
            response.put("status", "COMPLETED");
            response.put("bookings", createdBookings);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to create booking: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Convert time slots from request data to a list of BookingDto objects
     * 
     * @param requestData The request data from API gateway
     * @param bookingGroupId The shared booking group ID
     * @return List of BookingDto objects, one for each time slot
     */
    @SuppressWarnings("unchecked")
    private List<BookingDto> convertTimeSlotToBookingDtos(Map<String, Object> requestData, Long bookingGroupId) {
        List<BookingDto> result = new ArrayList<>();
        
        try {
            // Extract common booking data
            Integer userId = Integer.valueOf(requestData.get("userId").toString());
            Long facilityId = Long.valueOf(requestData.get("facilityId").toString());
            String date = (String) requestData.get("date");
            
            // Get all time slots
            List<Map<String, Object>> timeSlots = (List<Map<String, Object>>) requestData.get("timeSlots");
            if (timeSlots == null || timeSlots.isEmpty()) {
                logger.warn("No time slots found in request data");
                return result;
            }
            
            // Process each time slot
            for (Map<String, Object> slot : timeSlots) {
                BookingDto bookingDto = new BookingDto();
                
                // Set common properties
                bookingDto.setUserId(userId);
                bookingDto.setFacilityId(facilityId);
                bookingDto.setBookingGroupId(bookingGroupId);
                bookingDto.setStatus("COMPLETED");
                
                // Get slot-specific date or use the one from parent object
                String slotDate = (String) slot.getOrDefault("date", date);
                LocalDate bookingDate = LocalDate.parse(slotDate);
                
                // Get start and end hours - safely parse string values
                int startHour = Integer.parseInt(slot.get("startHour").toString());
                int endHour = Integer.parseInt(slot.get("endHour").toString());
                
                // Create start and end times
                LocalDateTime startTime = LocalDateTime.of(bookingDate, LocalTime.of(startHour, 0));
                LocalDateTime endTime = LocalDateTime.of(bookingDate, LocalTime.of(endHour, 0));
                
                bookingDto.setStartTime(startTime);
                bookingDto.setEndTime(endTime);
                
                // Calculate price - assuming a fixed hourly rate for simplicity
                double hourlyRate = 20.0;
                double hours = endHour - startHour;
                bookingDto.setTotalPrice(new java.math.BigDecimal(hours * hourlyRate));
                
                // Add to result list
                result.add(bookingDto);
                logger.info("Created booking DTO for time slot: {} to {} on {}", 
                        startHour, endHour, slotDate);
            }
        } catch (Exception e) {
            logger.error("Error converting time slots: {}", e.getMessage(), e);
        }
        
        return result;
    }

    /**
     * Cancel all bookings in a booking group
     * 
     * @param bookingGroupId Booking group ID
     * @return Response with cancellation details
     */
    @DeleteMapping("/cancel-group")
    public ResponseEntity<?> cancelBookingGroup(@RequestParam Long bookingGroupId) {
        try {
            logger.info("Request to cancel booking group: {}", bookingGroupId);
            int cancelledCount = bookingService.cancelBookingGroup(bookingGroupId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("bookingGroupId", bookingGroupId);
            response.put("cancelledCount", cancelledCount);
            response.put("status", "cancelled");
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error cancelling booking group: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get user bookings by status
     *
     * @param userId User ID
     * @param status Booking status
     * @return List of bookings with the specified status
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<BookingDto>> getBookingsByUserIdAndStatus(
            @PathVariable Integer userId,
            @PathVariable String status) {
        logger.info("Fetching bookings for user ID: {} with status: {}", userId, status);
        List<BookingDto> bookings = bookingService.getBookingsByUserIdAndStatus(userId, status);
        logger.info("Found {} bookings for user ID: {} with status: {}", bookings.size(), userId, status);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by booking group ID
     *
     * @param groupId Booking group ID
     * @return List of bookings in the group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<BookingDto>> getBookingsByGroupId(@PathVariable Long groupId) {
        logger.info("Fetching bookings for group ID: {}", groupId);
        List<BookingDto> bookings = bookingService.getBookingsByGroupId(groupId);
        logger.info("Found {} bookings in group ID: {}", bookings.size(), groupId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}