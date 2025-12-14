package com.kheyma.repository;

import com.kheyma.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    Page<Review> findByLocationId(String locationId, Pageable pageable);
    Page<Review> findByUserId(String userId, Pageable pageable);
    List<Review> findByLocationId(String locationId);
    Optional<Review> findByLocationIdAndUserId(String locationId, String userId);
}

