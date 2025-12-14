package com.kheyma.aop;

import com.kheyma.aop.annotation.SecurityAudit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspect for security auditing.
 * Logs security-related operations, user actions, and access attempts.
 * 
 * Follows Single Responsibility Principle - handles only security auditing.
 */
@Aspect
@Component
public class SecurityAuditAspect {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");
    
    @Around("@annotation(com.kheyma.aop.annotation.SecurityAudit) || @within(com.kheyma.aop.annotation.SecurityAudit)")
    public Object auditSecurityOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // Always extract annotation explicitly to avoid null binding issues
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SecurityAudit securityAudit = signature.getMethod().getAnnotation(SecurityAudit.class);
        if (securityAudit == null) {
            // Try class-level annotation
            securityAudit = joinPoint.getTarget().getClass().getAnnotation(SecurityAudit.class);
        }
        
        // If still null, skip auditing (shouldn't happen with proper pointcut, but safety check)
        if (securityAudit == null) {
            return joinPoint.proceed();
        }
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String action = securityAudit.action().isEmpty() 
            ? className + "." + methodName 
            : securityAudit.action();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "ANONYMOUS";
        String userRoles = authentication != null 
            ? authentication.getAuthorities().toString() 
            : "NONE";
        
        // Log operation start
        logAuditEntry(action, username, userRoles, securityAudit);
        
        // Log parameters if enabled and not sensitive
        if (securityAudit.logParameters() && !securityAudit.sensitive()) {
            logAuditParameters(action, username, joinPoint.getArgs(), signature.getParameterNames());
        }
        
        Object result;
        try {
            result = joinPoint.proceed();
            
            // Log successful operation
            logAuditSuccess(action, username);
            
            return result;
            
        } catch (Throwable throwable) {
            // Log failed operation
            logAuditFailure(action, username, throwable);
            throw throwable;
        }
    }
    
    private void logAuditEntry(String action, String username, String roles, SecurityAudit securityAudit) {
        String sensitivity = securityAudit.sensitive() ? " [SENSITIVE]" : "";
        auditLogger.info("AUDIT ENTRY{} - Action: {}, User: {}, Roles: {}, Time: {}", 
            sensitivity, action, username, roles, LocalDateTime.now());
    }
    
    private void logAuditParameters(String action, String username, Object[] args, String[] paramNames) {
        if (args == null || args.length == 0) {
            return;
        }
        
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                params.append(", ");
            }
            String paramName = (paramNames != null && i < paramNames.length) 
                ? paramNames[i] 
                : "arg" + i;
            
            // Always mask sensitive data in audit logs
            String paramValue = maskSensitiveData(paramName, args[i]);
            params.append(paramName).append("=").append(paramValue);
        }
        
        auditLogger.debug("AUDIT PARAMS - Action: {}, User: {}, Parameters: [{}]", 
            action, username, params);
    }
    
    private void logAuditSuccess(String action, String username) {
        auditLogger.info("AUDIT SUCCESS - Action: {}, User: {}, Time: {}", 
            action, username, LocalDateTime.now());
    }
    
    private void logAuditFailure(String action, String username, Throwable throwable) {
        auditLogger.error("AUDIT FAILURE - Action: {}, User: {}, Error: {}, Time: {}", 
            action, username, throwable.getMessage(), LocalDateTime.now());
    }
    
    private String maskSensitiveData(String paramName, Object value) {
        if (value == null) {
            return "null";
        }
        
        String paramNameLower = paramName.toLowerCase();
        if (paramNameLower.contains("password") || 
            paramNameLower.contains("token") || 
            paramNameLower.contains("secret") ||
            paramNameLower.contains("key") ||
            paramNameLower.contains("credential")) {
            return "***MASKED***";
        }
        
        String valueStr = value.toString();
        // Truncate long values
        if (valueStr.length() > 200) {
            return valueStr.substring(0, 200) + "... (truncated)";
        }
        
        return valueStr;
    }
}

