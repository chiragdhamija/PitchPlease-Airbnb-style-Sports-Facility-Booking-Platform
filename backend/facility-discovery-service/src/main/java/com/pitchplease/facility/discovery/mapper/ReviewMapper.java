package com.pitchplease.facility.discovery.mapper;

import org.springframework.stereotype.Component;

import com.pitchplease.facility.discovery.model.dto.ReviewDto;
import com.pitchplease.facility.discovery.model.entity.Review;

@Component
public class ReviewMapper {
    
    /**
     * Convert entity to DTO without username
     * 
     * @param entity The Review entity
     * @return The ReviewDto
     */
    public ReviewDto toDto(Review entity) {
        if (entity == null) {
            return null;
        }
        
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(entity.getReviewId());
        dto.setFacilityId(entity.getFacilityId());
        dto.setUserId(entity.getUserId());
        dto.setUserName(entity.getUserName());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }
    
    // /**
    //  * Convert entity to DTO with username
    //  * 
    //  * @param entity The Review entity
    //  * @param userName The username from user service
    //  * @return The ReviewDto
    //  */
    // public ReviewDto toDto(Review entity, String userName) {
    //     ReviewDto dto = toDto(entity);
    //     if (dto != null) {
    //         dto.setUserName(userName);
    //     }
    //     return dto;
    // }
    
    /**
     * Convert DTO to entity
     * 
     * @param dto The ReviewDto
     * @return The Review entity
     */
    public Review toEntity(ReviewDto dto) {
        if (dto == null) {
            return null;
        }
        
        Review entity = new Review();
        entity.setReviewId(dto.getReviewId());
        entity.setFacilityId(dto.getFacilityId());
        entity.setUserId(dto.getUserId());
        entity.setUserName(dto.getUserName());
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        entity.setCreatedAt(dto.getCreatedAt());
        
        return entity;
    }
}