package com.kheyma.service;

import com.kheyma.aop.annotation.Loggable;
import com.kheyma.aop.annotation.PerformanceMonitor;
import com.kheyma.dto.*;
import com.kheyma.model.Location;
import com.kheyma.repository.LocationRepository;
import com.kheyma.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Loggable(logParameters = false, logReturnValue = false)
@PerformanceMonitor(threshold = 2000)
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public PageResponse<LocationDto> getAllPublicLocations(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Location> locations = locationRepository.findAll(pageable);
        
        return convertToPageResponse(locations);
    }
    
    public LocationDto getLocationById(String id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return convertToDto(location);
    }
    
    public PageResponse<LocationDto> searchLocations(String query, Double lat, Double lng, Double radius,
                                                     List<String> tags, Double minPrice, Double maxPrice,
                                                     Integer rating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> locations;
        
        if (query != null && !query.isEmpty()) {
            locations = locationRepository.search(query, pageable);
        } else {
            locations = locationRepository.findAll(pageable);
        }
        
        // Apply additional filters
        List<Location> filtered = locations.getContent().stream()
                .filter(loc -> {
                    if (tags != null && !tags.isEmpty()) {
                        if (!loc.getTags().stream().anyMatch(tags::contains)) return false;
                    }
                    if (minPrice != null && loc.getPricePerNight() != null) {
                        if (loc.getPricePerNight().doubleValue() < minPrice) return false;
                    }
                    if (maxPrice != null && loc.getPricePerNight() != null) {
                        if (loc.getPricePerNight().doubleValue() > maxPrice) return false;
                    }
                    if (rating != null) {
                        if (loc.getAverageRating() == null || loc.getAverageRating() < rating) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        Page<Location> filteredPage = new org.springframework.data.domain.PageImpl<>(
                filtered, pageable, filtered.size());
        
        return convertToPageResponse(filteredPage);
    }
    
    @Transactional
    public LocationDto createLocation(CreateLocationRequest request, String userId) {
        Location location = new Location();
        location.setTitle(request.getTitle());
        location.setDescription(request.getDescription());
        location.setGeoLocation(new Location.GeoLocation(request.getLongitude(), request.getLatitude()));
        location.setPricePerNight(request.getPricePerNight());
        location.setTags(request.getTags() != null ? request.getTags() : List.of());
        location.setTicketRequired(request.isTicketRequired());
        location.setLocationType(request.getLocationType());
        location.setOwnerId(userId);
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        
        location = locationRepository.save(location);
        return convertToDto(location);
    }
    
    @Transactional
    public LocationDto updateLocation(String id, UpdateLocationRequest request, String userId, boolean isAdmin) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        if (!isAdmin && !location.getOwnerId().equals(userId)) {
            throw new RuntimeException("You don't have permission to update this location");
        }
        
        if (request.getTitle() != null) location.setTitle(request.getTitle());
        if (request.getDescription() != null) location.setDescription(request.getDescription());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            location.setGeoLocation(new Location.GeoLocation(request.getLongitude(), request.getLatitude()));
        }
        if (request.getPricePerNight() != null) location.setPricePerNight(request.getPricePerNight());
        if (request.getTags() != null) location.setTags(request.getTags());
        if (request.getLocationType() != null) location.setLocationType(request.getLocationType());
        location.setTicketRequired(request.isTicketRequired());
        location.setTicketAvailable(request.isTicketAvailable());
        location.setUpdatedAt(LocalDateTime.now());
        
        location = locationRepository.save(location);
        return convertToDto(location);
    }
    
    @Transactional
    public void deleteLocation(String id, String userId, boolean isAdmin) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        if (!isAdmin && !location.getOwnerId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this location");
        }
        
        locationRepository.delete(location);
    }
    
    @Transactional
    public LocationDto uploadImage(String id, String imageUrl, String userId, boolean isAdmin) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        if (!isAdmin && !location.getOwnerId().equals(userId)) {
            throw new RuntimeException("You don't have permission to upload images for this location");
        }
        
        location.getImageUrls().add(imageUrl);
        location.setUpdatedAt(LocalDateTime.now());
        location = locationRepository.save(location);
        return convertToDto(location);
    }
    
    @Transactional
    public void updateLocationRating(String locationId) {
        List<com.kheyma.model.Review> reviews = reviewRepository.findByLocationId(locationId);
        if (reviews.isEmpty()) {
            return;
        }
        
        double avgRating = reviews.stream()
                .mapToInt(com.kheyma.model.Review::getRating)
                .average()
                .orElse(0.0);
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        location.setAverageRating(avgRating);
        location.setReviewCount(reviews.size());
        locationRepository.save(location);
    }
    
    private LocationDto convertToDto(Location location) {
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        dto.setTitle(location.getTitle());
        dto.setDescription(location.getDescription());
        if (location.getGeoLocation() != null) {
            dto.setLatitude(location.getGeoLocation().getLatitude());
            dto.setLongitude(location.getGeoLocation().getLongitude());
        }
        dto.setPricePerNight(location.getPricePerNight());
        dto.setTags(location.getTags());
        dto.setImageUrls(location.getImageUrls());
        dto.setTicketRequired(location.isTicketRequired());
        dto.setTicketAvailable(location.isTicketAvailable());
        dto.setLocationType(location.getLocationType());
        dto.setOwnerId(location.getOwnerId());
        dto.setAverageRating(location.getAverageRating());
        dto.setReviewCount(location.getReviewCount());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
        return dto;
    }
    
    private PageResponse<LocationDto> convertToPageResponse(Page<Location> page) {
        List<LocationDto> content = page.getContent().stream()
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

