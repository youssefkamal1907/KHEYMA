package com.kheyma.dto;

import com.kheyma.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateLocationRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private BigDecimal pricePerNight;
    
    private List<String> tags;
    
    private boolean ticketRequired = true;
    
    private Location.LocationType locationType;
}

