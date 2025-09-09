package com.pitchplease.payment.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
}