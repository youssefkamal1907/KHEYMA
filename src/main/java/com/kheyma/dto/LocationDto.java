package com.kheyma.dto;

import com.kheyma.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerNight;
    private List<String> tags;
    private List<String> imageUrls;
    private boolean ticketRequired;
    private boolean ticketAvailable;
    private Location.LocationType locationType;
    private String ownerId;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

