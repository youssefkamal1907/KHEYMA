package com.kheyma.aop;

import com.kheyma.aop.annotation.Loggable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for method-level logging.
 * Logs method entry, parameters, and exit based on @Loggable annotation.
 * 
 * Follows Single Responsibility Principle - handles only logging concerns.
 */
@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    @Around("@annotation(loggable) || @within(loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String fullMethodName = className + "." + methodName;
        
        // Log method entry
        logMethodEntry(fullMethodName, loggable);
        
        // Log parameters if enabled
        if (loggable.logParameters()) {
            logMethodParameters(fullMethodName, joinPoint.getArgs(), signature.getParameterNames());
        }
        
        Object result;
        try {
            result = joinPoint.proceed();
            
            // Log return value if enabled
            if (loggable.logReturnValue() && result != null) {
                logMethodReturnValue(fullMethodName, result);
            }
            
            logMethodExit(fullMethodName);
            return result;
            
        } catch (Throwable throwable) {
            logMethodException(fullMethodName, throwable);
            throw throwable;
        }
    }
    
    private void logMethodEntry(String methodName, Loggable loggable) {
        String level = loggable.level().toUpperCase();
        switch (level) {
            case "TRACE":
                logger.trace("Entering method: {}", methodName);
                break;
            case "DEBUG":
                logger.debug("Entering method: {}", methodName);
                break;
            case "INFO":
                logger.info("Entering method: {}", methodName);
                break;
            default:
                logger.debug("Entering method: {}", methodName);
        }
    }
    
    private void logMethodParameters(String methodName, Object[] args, String[] paramNames) {
        if (args == null || args.length == 0) {
            logger.debug("Method {} - No parameters", methodName);
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
            
            // Mask sensitive parameters
            String paramValue = maskSensitiveData(paramName, args[i]);
            params.append(paramName).append("=").append(paramValue);
        }
        
        logger.debug("Method {} - Parameters: [{}]", methodName, params);
    }
    
    private void logMethodReturnValue(String methodName, Object returnValue) {
        String returnValueStr = returnValue.toString();
        
        // Truncate long return values
        if (returnValueStr.length() > 500) {
            returnValueStr = returnValueStr.substring(0, 500) + "... (truncated)";
        }
        
        logger.debug("Method {} - Return value: {}", methodName, returnValueStr);
    }
    
    private void logMethodExit(String methodName) {
        logger.debug("Exiting method: {}", methodName);
    }
    
    private void logMethodException(String methodName, Throwable throwable) {
        logger.error("Exception in method: {} - {}", methodName, throwable.getMessage(), throwable);
    }
    
    private String maskSensitiveData(String paramName, Object value) {
        if (value == null) {
            return "null";
        }
        
        String paramNameLower = paramName.toLowerCase();
        if (paramNameLower.contains("password") || 
            paramNameLower.contains("token") || 
            paramNameLower.contains("secret") ||
            paramNameLower.contains("key")) {
            return "***MASKED***";
        }
        
        return value.toString();
    }
}

