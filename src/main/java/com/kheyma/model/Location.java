package com.kheyma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoLocation geoLocation;
    
    @Field("price_per_night")
    private BigDecimal pricePerNight;
    
    private List<String> tags = new ArrayList<>();
    
    @Field("image_urls")
    private List<String> imageUrls = new ArrayList<>();
    
    @Field("ticket_required")
    private boolean ticketRequired = true;
    
    @Field("ticket_available")
    private boolean ticketAvailable = true;
    
    @Field("location_type")
    private LocationType locationType;
    
    @Field("owner_id")
    private String ownerId;
    
    @Field("average_rating")
    private Double averageRating = 0.0;
    
    @Field("review_count")
    private Integer reviewCount = 0;
    
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLocation {
        private String type = "Point";
        private double[] coordinates; // [longitude, latitude]
        
        public GeoLocation(double longitude, double latitude) {
            this.coordinates = new double[]{longitude, latitude};
        }
        
        public double getLongitude() {
            return coordinates != null && coordinates.length > 0 ? coordinates[0] : 0.0;
        }
        
        public double getLatitude() {
            return coordinates != null && coordinates.length > 1 ? coordinates[1] : 0.0;
        }
    }
    
    public enum LocationType {
        DESERT,
        OASIS,
        PROTECTORATE,
        MOUNTAIN,
        BEACH,
        FOREST,
        OTHER
    }
}

