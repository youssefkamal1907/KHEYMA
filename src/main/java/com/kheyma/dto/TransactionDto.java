package com.kheyma.dto;

import com.kheyma.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String id;
    private String locationId;
    private String userId;
    private Transaction.PackageType packageType;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Transaction.PaymentMethod paymentMethod;
    private Transaction.TransactionStatus status;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

