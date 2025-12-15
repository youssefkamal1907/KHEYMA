package com.kheyma.controller;

import com.kheyma.dto.*;
import com.kheyma.service.ReviewService;
import com.kheyma.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewRequest request,
                                           Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            ReviewDto review = reviewService.createReview(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<PageResponse<ReviewDto>> getReviewsByLocation(
            @PathVariable String locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ReviewDto> response = reviewService.getReviewsByLocation(locationId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<ReviewDto>> getReviewsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ReviewDto> response = reviewService.getReviewsByUser(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id,
                                          @Valid @RequestBody UpdateReviewRequest request,
                                          Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            ReviewDto review = reviewService.updateReview(id, request, userId, isAdmin);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id, Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            reviewService.deleteReview(id, userId, isAdmin);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    
    private static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

