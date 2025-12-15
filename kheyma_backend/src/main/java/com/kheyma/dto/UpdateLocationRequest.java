package com.kheyma.dto;

import com.kheyma.model.Location;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateLocationRequest {
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerNight;
    private List<String> tags;
    private boolean ticketRequired;
    private boolean ticketAvailable;
    private Location.LocationType locationType;
}

