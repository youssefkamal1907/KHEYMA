package com.kheyma.controller;

import com.kheyma.aop.annotation.Loggable;
import com.kheyma.aop.annotation.PerformanceMonitor;
import com.kheyma.aop.annotation.SecurityAudit;
import com.kheyma.dto.*;
import com.kheyma.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Loggable(logParameters = true, logReturnValue = false, level = "INFO")
@PerformanceMonitor(threshold = 500, alwaysLog = true)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    @SecurityAudit(action = "USER_REGISTRATION", logParameters = false, sensitive = true)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @SecurityAudit(action = "USER_LOGIN", logParameters = false, sensitive = true)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    @SecurityAudit(action = "TOKEN_REFRESH")
    public ResponseEntity<?> refreshToken(Authentication authentication) {
        try {
            String email = authentication.getName();
            AuthResponse response = authService.refreshToken(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    @SecurityAudit(action = "GET_USER_PROFILE")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            UserDto user = authService.getCurrentUser(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/me")
    @SecurityAudit(action = "UPDATE_USER_PROFILE")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request,
                                                Authentication authentication) {
        try {
            String email = authentication.getName();
            UserDto user = authService.updateCurrentUser(email, request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // Simple error response class
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

