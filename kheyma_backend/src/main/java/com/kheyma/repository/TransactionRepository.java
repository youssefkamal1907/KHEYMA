package com.kheyma.repository;

import com.kheyma.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findByUserId(String userId, Pageable pageable);
    Page<Transaction> findByLocationId(String locationId, Pageable pageable);
    List<Transaction> findByUserIdAndStatus(String userId, Transaction.TransactionStatus status);
    List<Transaction> findByLocationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String locationId, LocalDate date1, LocalDate date2);
}

