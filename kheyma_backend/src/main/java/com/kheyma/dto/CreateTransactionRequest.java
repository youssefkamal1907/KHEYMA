package com.kheyma.dto;

import com.kheyma.model.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTransactionRequest {
    @NotBlank(message = "Location ID is required")
    private String locationId;
    
    @NotNull(message = "Package type is required")
    private Transaction.PackageType packageType;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Payment method is required")
    private Transaction.PaymentMethod paymentMethod;
}

