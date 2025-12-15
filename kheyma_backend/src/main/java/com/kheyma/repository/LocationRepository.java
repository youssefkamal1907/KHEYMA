package com.kheyma.repository;

import com.kheyma.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    Page<Location> findByTicketRequired(boolean ticketRequired, Pageable pageable);
    
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Page<Location> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    @Query("{ 'tags': { $in: ?0 } }")
    Page<Location> findByTagsIn(List<String> tags, Pageable pageable);
    
    @Query("{ 'locationType': ?0 }")
    Page<Location> findByLocationType(Location.LocationType locationType, Pageable pageable);
    
    @Query("{ 'pricePerNight': { $gte: ?0, $lte: ?1 } }")
    Page<Location> findByPricePerNightBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    Page<Location> findByOwnerId(String ownerId, Pageable pageable);
    
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'tags': { $in: [?0] } } ] }")
    Page<Location> search(String query, Pageable pageable);
}

