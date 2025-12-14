package com.kheyma.service;

import com.kheyma.dto.*;
import com.kheyma.model.Review;
import com.kheyma.model.User;
import com.kheyma.repository.LocationRepository;
import com.kheyma.repository.ReviewRepository;
import com.kheyma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationService locationService;
    
    @Transactional
    public ReviewDto createReview(CreateReviewRequest request, String userId) {
        // Check if location exists
        locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        // Check if user already reviewed this location
        reviewRepository.findByLocationIdAndUserId(request.getLocationId(), userId)
                .ifPresent(review -> {
                    throw new RuntimeException("You have already reviewed this location");
                });
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Review review = new Review();
        review.setLocationId(request.getLocationId());
        review.setUserId(userId);
        review.setUserName(user.getName());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        review = reviewRepository.save(review);
        
        // Update location rating
        locationService.updateLocationRating(request.getLocationId());
        
        return convertToDto(review);
    }
    
    public PageResponse<ReviewDto> getReviewsByLocation(String locationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByLocationId(locationId, pageable);
        return convertToPageResponse(reviews);
    }
    
    public PageResponse<ReviewDto> getReviewsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);
        return convertToPageResponse(reviews);
    }
    
    @Transactional
    public ReviewDto updateReview(String id, UpdateReviewRequest request, String userId, boolean isAdmin) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new RuntimeException("You don't have permission to update this review");
        }
        
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        
        review = reviewRepository.save(review);
        
        // Update location rating
        locationService.updateLocationRating(review.getLocationId());
        
        return convertToDto(review);
    }
    
    @Transactional
    public void deleteReview(String id, String userId, boolean isAdmin) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this review");
        }
        
        String locationId = review.getLocationId();
        reviewRepository.delete(review);
        
        // Update location rating
        locationService.updateLocationRating(locationId);
    }
    
    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setLocationId(review.getLocationId());
        dto.setUserId(review.getUserId());
        dto.setUserName(review.getUserName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
    
    private PageResponse<ReviewDto> convertToPageResponse(Page<Review> page) {
        List<ReviewDto> content = page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}

