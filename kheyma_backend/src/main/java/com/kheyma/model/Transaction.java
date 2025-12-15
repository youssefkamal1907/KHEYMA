package com.kheyma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    
    @Field("location_id")
    private String locationId;
    
    @Field("user_id")
    private String userId;
    
    @Field("package_type")
    private PackageType packageType;
    
    private BigDecimal amount;
    
    @Field("start_date")
    private LocalDate startDate;
    
    @Field("end_date")
    private LocalDate endDate;
    
    @Field("payment_method")
    private PaymentMethod paymentMethod;
    
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Field("payment_id")
    private String paymentId; // External payment gateway transaction ID
    
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum PackageType {
        BASIC,
        STANDARD,
        PREMIUM
    }
    
    public enum PaymentMethod {
        CARD,
        PAYPAL,
        STRIPE,
        INSTAPAY,
        CASH
    }
    
    public enum TransactionStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        REFUNDED
    }
}

