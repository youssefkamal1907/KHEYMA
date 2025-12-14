package com.kheyma.controller;

import com.kheyma.aop.annotation.Loggable;
import com.kheyma.aop.annotation.PerformanceMonitor;
import com.kheyma.aop.annotation.SecurityAudit;
import com.kheyma.dto.*;
import com.kheyma.service.LocationService;
import com.kheyma.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@Loggable(logParameters = true, logReturnValue = false)
@PerformanceMonitor(threshold = 1000)
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    @GetMapping("/public/all")
    public ResponseEntity<PageResponse<LocationDto>> getAllPublicLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<LocationDto> response = locationService.getAllPublicLocations(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable String id) {
        try {
            LocationDto location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<PageResponse<LocationDto>> searchLocations(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<LocationDto> response = locationService.searchLocations(
                q, lat, lng, radius, tags, minPrice, maxPrice, rating, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @SecurityAudit(action = "CREATE_LOCATION")
    public ResponseEntity<?> createLocation(@Valid @RequestBody CreateLocationRequest request,
                                            Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            LocationDto location = locationService.createLocation(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(location);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @SecurityAudit(action = "UPDATE_LOCATION")
    public ResponseEntity<?> updateLocation(@PathVariable String id,
                                            @Valid @RequestBody UpdateLocationRequest request,
                                            Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            LocationDto location = locationService.updateLocation(id, request, userId, isAdmin);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @SecurityAudit(action = "DELETE_LOCATION", sensitive = true)
    public ResponseEntity<?> deleteLocation(@PathVariable String id, Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            locationService.deleteLocation(id, userId, isAdmin);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable String id,
                                         @RequestParam String imageUrl,
                                         Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            LocationDto location = locationService.uploadImage(id, imageUrl, userId, isAdmin);
            return ResponseEntity.ok(location);
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

