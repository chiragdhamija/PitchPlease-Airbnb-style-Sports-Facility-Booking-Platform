package com.pitchplease.booking.mapper;

import org.springframework.stereotype.Component;

import com.pitchplease.booking.model.dto.BookingDto;
import com.pitchplease.booking.model.entity.Booking;

@Component
public class BookingMapper {
    
    /**
     * Convert Booking entity to BookingDto
     * 
     * @param entity Booking entity
     * @return BookingDto
     */
    public BookingDto toDto(Booking entity) {
        if (entity == null) {
            return null;
        }
        
        return new BookingDto(
                entity.getBookingId(),
                entity.getBookingGroupId(),
                entity.getFacilityId(),
                entity.getUserId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getTotalPrice(),
                entity.getStatus()
        );
    }
    
    /**
     * Convert BookingDto to Booking entity
     * 
     * @param dto BookingDto
     * @return Booking entity
     */
    public Booking toEntity(BookingDto dto) {
        if (dto == null) {
            return null;
        }
        
        return new Booking(
                dto.getBookingId(),
                dto.getBookingGroupId(),
                dto.getFacilityId(),
                dto.getUserId(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getTotalPrice(),
                dto.getStatus()
        );
    }
}