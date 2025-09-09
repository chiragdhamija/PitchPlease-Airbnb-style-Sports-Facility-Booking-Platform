package com.pitchplease.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pitchplease.booking.model.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find all bookings for a specific user
     * 
     * @param userId The ID of the user
     * @return List of bookings made by the user
     */
    List<Booking> findByUserId(Integer userId);
    
    /**
     * Find all bookings for a specific facility
     * 
     * @param facilityId The ID of the facility
     * @return List of bookings for the facility
     */
    List<Booking> findByFacilityId(Long facilityId);
    
    /**
     * Find bookings by facility ID and status
     * 
     * @param facilityId The ID of the facility
     * @param status The booking status (e.g., 'pending', 'confirmed', 'cancelled')
     * @return List of bookings matching the criteria
     */
    List<Booking> findByFacilityIdAndStatus(Long facilityId, String status);
    
    /**
     * Find all bookings for a specific user with a specific status
     * 
     * @param userId The ID of the user
     * @param status The booking status
     * @return List of bookings matching the criteria
     */
    List<Booking> findByUserIdAndStatus(Integer userId, String status);
    
    /**
     * Find all bookings by booking group ID
     * 
     * @param bookingGroupId The booking group ID
     * @return List of bookings in the same group
     */
    List<Booking> findByBookingGroupId(Long bookingGroupId);
    
    /**
     * Check if a facility is available for booking during a specific time period
     * 
     * @param facilityId The ID of the facility
     * @param startTime Start time of the proposed booking
     * @param endTime End time of the proposed booking
     * @return List of conflicting bookings (should be empty if facility is available)
     */
    @Query("SELECT b FROM Booking b WHERE b.facilityId = :facilityId " +
           "AND b.status != 'cancelled' " +
           "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
    List<Booking> findConflictingBookings(Long facilityId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Update the status of a booking
     * 
     * @param bookingId The ID of the booking to update
     * @param status The new status value
     * @return Number of records updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.bookingId = :bookingId")
    int updateBookingStatus(Long bookingId, String status);
    @Modifying
    @Query(value = "DELETE FROM bookings WHERE booking_group_id = :groupId", nativeQuery = true)
    int deleteBookingsByGroupId(@Param("groupId") Long groupId);
}