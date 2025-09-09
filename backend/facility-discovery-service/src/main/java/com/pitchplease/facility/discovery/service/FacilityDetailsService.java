package com.pitchplease.facility.discovery.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplease.facility.discovery.mapper.ReviewMapper;
import com.pitchplease.facility.discovery.model.dto.FacilityDetailsDto;
import com.pitchplease.facility.discovery.model.dto.FacilityDto;
import com.pitchplease.facility.discovery.model.dto.ReviewDto;
import com.pitchplease.facility.discovery.model.entity.Review;
import com.pitchplease.facility.discovery.repository.ReviewRepository;

import com.pitchplease.facility.discovery.service.LLMService;
import com.pitchplease.facility.discovery.service.GeminiAdapter;


@Service
public class FacilityDetailsService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private FacilityService facilityService;

    /**
     * Get detailed information for a facility including ratings
     * 
     * @param facilityId The facility ID
     * @return Detailed facility information
     */
    @Transactional(readOnly = true)
    public Optional<FacilityDetailsDto> getFacilityDetails(Long facilityId) {
        // Get basic facility information
        Optional<FacilityDto> facilityOpt = facilityService.getFacilityById(facilityId);

        if (facilityOpt.isEmpty()) {
            return Optional.empty();
        }

        FacilityDto facility = facilityOpt.get();

        // Create detailed DTO
        FacilityDetailsDto detailsDto = new FacilityDetailsDto();

        // Copy basic properties
        detailsDto.setFacilityId(facility.getFacilityId());
        detailsDto.setName(facility.getName());
        detailsDto.setDescription(facility.getDescription());
        detailsDto.setAddress(facility.getAddress());
        detailsDto.setCity(facility.getCity());
        detailsDto.setFacilityType(facility.getFacilityType());
        detailsDto.setHourlyRate(facility.getHourlyRate());
        detailsDto.setOwnerId(facility.getOwnerId());

        // Get rating information
        Double avgRating = reviewRepository.getAverageRatingForFacility(facilityId);
        long reviewCount = reviewRepository.countByFacilityId(facilityId);

        detailsDto.setAverageRating(avgRating);
        detailsDto.setReviewCount((int) reviewCount);

        // Set additional details (in a real implementation these might come from
        // another table)
        detailsDto.setFeatures("WiFi, Changing Rooms, Parking");
        detailsDto.setRules("No smoking, No food on the playing area");
        detailsDto.setAvailability("Monday-Friday: 8am-10pm, Weekends: 9am-8pm");

        // Get owner/agent information if available (simplified for now)
        if (facility.getOwnerId() != null) {
            Map<String, Object> agentInfo = new HashMap<>();
            agentInfo.put("name", "Facility Manager");
            agentInfo.put("phone", "+1 123-456-7890");
            agentInfo.put("email", "manager@example.com");
            agentInfo.put("bio", "Experienced facility manager with over 10 years in sports venue management.");
            agentInfo.put("photo", "assets/img/testimonials/testimonials-2.jpg");

            detailsDto.setAgent(agentInfo);
        }

        return Optional.of(detailsDto);
    }

    /**
     * Get all reviews for a facility
     * 
     * @param facilityId The facility ID
     * @return List of reviews
     */
    @Transactional(readOnly = true)
    public List<ReviewDto> getFacilityReviews(Long facilityId) {
        List<Review> reviews = reviewRepository.findByFacilityIdOrderByCreatedAtDesc(facilityId);
        // I want to create a summary of all the reviews and the average rating for the
        // facility
        List<ReviewDto> reviewDtos = reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());

        Integer totalRating = 0;
        for (ReviewDto review : reviewDtos) {
            totalRating += review.getRating();
        }
        // Create a string having all the reviews
        String reviewsString = "Summarise the following reviews given for a sports facility, output the review only:";
        for (ReviewDto review : reviewDtos) {
            reviewsString += review.getComment() + " \n";
        }
        // Call the AI summarizer to get a summary of the reviews
        GeminiAdapter aiSummarizerAdapter = new GeminiAdapter();
        String summary;

        reviewsString += " \n\n" + "Summarise the following reviews given for a sports facility, output the review only:";
        summary = aiSummarizerAdapter.generateContent(reviewsString);
        
        ReviewDto summaryReview = new ReviewDto();
        summaryReview.setComment(summary);
        summaryReview.setRating(totalRating / reviewDtos.size());
        reviewDtos.add(summaryReview);
        double averageRating = 0.0;
        summaryReview.setUserName("PitchPlease AI Summary");
        
        summaryReview.setCreatedAt(LocalDateTime.now());
        if (!reviews.isEmpty()) {
            averageRating = totalRating / reviews.size();
        }
        System.out.println("Average rating: " + averageRating);
        // Add the average rating to the summary review
        System.out.println("Summary review: " + summaryReview);
        return reviewDtos;
    }

    /**
     * Create a new review
     * 
     * @param reviewDto The review data (with user ID and username provided from
     *                  frontend/gateway)
     * @return The created review
     * @throws IllegalStateException if the user has already reviewed this facility
     */
    @Transactional
    public ReviewDto createReview(ReviewDto reviewDto) {
        // Check if user has already reviewed this facility
        if (reviewRepository.existsByFacilityIdAndUserId(reviewDto.getFacilityId(), reviewDto.getUserId())) {
            throw new IllegalStateException("User has already reviewed this facility");
        }

        // Convert to entity
        Review review = reviewMapper.toEntity(reviewDto);

        // Save the review
        Review savedReview = reviewRepository.save(review);

        // Map back to DTO, preserving the username
        ReviewDto resultDto = reviewMapper.toDto(savedReview);
        resultDto.setUserName(reviewDto.getUserName()); // Use the provided username

        return resultDto;
    }

    /**
     * Delete a review
     * 
     * @param reviewId The review ID
     * @param userId   The ID of the user deleting the review (provided from
     *                 frontend/gateway)
     * @return True if deleted, false otherwise
     */
    @Transactional
    public boolean deleteReview(Long reviewId, Integer userId) {
        long deletedCount = reviewRepository.deleteByReviewIdAndUserId(reviewId, userId);
        return deletedCount > 0;
    }
}