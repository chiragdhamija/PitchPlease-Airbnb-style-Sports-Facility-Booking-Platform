package com.pitchplease.payment.mapper;

import org.springframework.stereotype.Component;

import com.pitchplease.payment.model.dto.PaymentDto;
// import com.pitchplease.payment.model.dto.PaymentRequestDto;
// import com.pitchplease.payment.model.dto.PaymentResponseDto;
import com.pitchplease.payment.model.entity.Payment;

import java.time.LocalDateTime;

@Component
public class PaymentMapper {
    
    /**
     * Convert Payment entity to PaymentDto
     * 
     * @param entity Payment entity
     * @return PaymentDto
     */
    public PaymentDto toDto(Payment entity) {
        if (entity == null) {
            return null;
        }
        
        return new PaymentDto(
                entity.getPaymentId(),
                entity.getBookingId(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getFacilityId(),
                entity.getFacilityName(),
                entity.getAddonsString(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                entity.getPaymentStatus(),
                entity.getTransactionId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert PaymentDto to Payment entity
     * 
     * @param dto PaymentDto
     * @return Payment entity
     */
    public Payment toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }
        
        return new Payment(
                dto.getPaymentId(),
                dto.getBookingId(),
                dto.getUserId(),
                dto.getUserName(),
                dto.getFacilityId(),
                dto.getFacilityName(),
                dto.getAddonsString(),
                dto.getAmount(),
                dto.getPaymentMethod(),
                dto.getPaymentStatus(),
                dto.getTransactionId(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
    
    // /**
    //  * Convert PaymentRequestDto to Payment entity
    //  * Sets default values for fields not in the request DTO
    //  * 
    //  * @param dto PaymentRequestDto
    //  * @return Payment entity
    //  */
    // public Payment toEntityFromRequest(PaymentRequestDto dto) {
    //     if (dto == null) {
    //         return null;
    //     }
        
    //     LocalDateTime now = LocalDateTime.now();
        
    //     Payment payment = new Payment();
    //     payment.setBookingId(dto.getBookingId());
    //     payment.setUserId(dto.getUserId());
    //     payment.setAmount(dto.getAmount());
    //     payment.setPaymentMethod(dto.getPaymentMethod());
    //     payment.setPaymentStatus(dto.getPaymentStatus());
    //     payment.setCreatedAt(now);
    //     payment.setUpdatedAt(now);
        
    //     return payment;
    // }
    
    // /**
    //  * Convert Payment entity to PaymentResponseDto
    //  * 
    //  * @param entity Payment entity
    //  * @return PaymentResponseDto
    //  */
    // public PaymentResponseDto toResponseDto(Payment entity) {
    //     if (entity == null) {
    //         return null;
    //     }
        
    //     PaymentResponseDto responseDto = new PaymentResponseDto();
    //     responseDto.setPaymentId(entity.getPaymentId());
    //     responseDto.setBookingId(entity.getBookingId());
    //     responseDto.setPaymentStatus(entity.getPaymentStatus());
    //     responseDto.setTransactionId(entity.getTransactionId());
    //     responseDto.setCreatedAt(entity.getCreatedAt());
        
    //     return responseDto;
    // }
    
    /**
     * Update an existing Payment entity from a PaymentDto
     * 
     * @param entity Existing Payment entity to update
     * @param dto PaymentDto with new values
     * @return Updated Payment entity
     */
    public Payment updateEntityFromDto(Payment entity, PaymentDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        
        // Only update fields that should be modifiable
        if (dto.getPaymentStatus() != null) {
            entity.setPaymentStatus(dto.getPaymentStatus());
        }
        if (dto.getTransactionId() != null) {
            entity.setTransactionId(dto.getTransactionId());
        }
        
        // Always update the updated timestamp
        entity.setUpdatedAt(LocalDateTime.now());
        
        return entity;
    }
}