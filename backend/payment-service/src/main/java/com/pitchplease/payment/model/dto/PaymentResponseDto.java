package com.pitchplease.payment.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private Long bookingId;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime createdAt;
}