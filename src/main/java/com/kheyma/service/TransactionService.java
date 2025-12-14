package com.kheyma.service;

import com.kheyma.dto.*;
import com.kheyma.model.Transaction;
import com.kheyma.repository.LocationRepository;
import com.kheyma.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Transactional
    public TransactionDto createTransaction(CreateTransactionRequest request, String userId) {
        // Verify location exists
        locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        // Check for date conflicts (simplified - can be enhanced)
        // In a real system, you'd check if the location is already booked for these dates
        
        Transaction transaction = new Transaction();
        transaction.setLocationId(request.getLocationId());
        transaction.setUserId(userId);
        transaction.setPackageType(request.getPackageType());
        transaction.setAmount(request.getAmount());
        transaction.setStartDate(request.getStartDate());
        transaction.setEndDate(request.getEndDate());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setPaymentId(UUID.randomUUID().toString()); // In production, use actual payment gateway ID
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // In a real system, you would integrate with payment gateway here
        // For now, we'll set it to CONFIRMED after creation
        transaction.setStatus(Transaction.TransactionStatus.CONFIRMED);
        
        transaction = transactionRepository.save(transaction);
        return convertToDto(transaction);
    }
    
    public PageResponse<TransactionDto> getUserTransactions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return convertToPageResponse(transactions);
    }
    
    public TransactionDto getTransactionById(String id, String userId, boolean isAdmin) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!isAdmin && !transaction.getUserId().equals(userId)) {
            throw new RuntimeException("You don't have permission to view this transaction");
        }
        
        return convertToDto(transaction);
    }
    
    @Transactional
    public TransactionDto cancelTransaction(String id, String userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUserId().equals(userId)) {
            throw new RuntimeException("You don't have permission to cancel this transaction");
        }
        
        if (transaction.getStatus() == Transaction.TransactionStatus.CANCELLED) {
            throw new RuntimeException("Transaction is already cancelled");
        }
        
        if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed transaction");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
        transaction.setUpdatedAt(LocalDateTime.now());
        
        transaction = transactionRepository.save(transaction);
        return convertToDto(transaction);
    }
    
    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setLocationId(transaction.getLocationId());
        dto.setUserId(transaction.getUserId());
        dto.setPackageType(transaction.getPackageType());
        dto.setAmount(transaction.getAmount());
        dto.setStartDate(transaction.getStartDate());
        dto.setEndDate(transaction.getEndDate());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        dto.setPaymentId(transaction.getPaymentId());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        return dto;
    }
    
    private PageResponse<TransactionDto> convertToPageResponse(Page<Transaction> page) {
        List<TransactionDto> content = page.getContent().stream()
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

