package com.kheyma.controller;

import com.kheyma.dto.*;
import com.kheyma.service.TransactionService;
import com.kheyma.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionRequest request,
                                               Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            TransactionDto transaction = transactionService.createTransaction(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<PageResponse<TransactionDto>> getUserTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
        PageResponse<TransactionDto> response = transactionService.getUserTransactions(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id, Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            boolean isAdmin = SecurityUtil.isAdmin(authentication);
            TransactionDto transaction = transactionService.getTransactionById(id, userId, isAdmin);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable String id, Authentication authentication) {
        try {
            String userId = SecurityUtil.getUserIdFromAuthentication(authentication);
            TransactionDto transaction = transactionService.cancelTransaction(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    
    private static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

