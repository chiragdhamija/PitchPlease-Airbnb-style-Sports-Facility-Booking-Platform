package com.pitchplease.facility.discovery.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private Long facilityId;
    private Integer userId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean isOwnReview;
}