package com.kheyma.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for centralized exception handling and logging.
 * Logs all exceptions thrown from service and controller layers.
 * 
 * Follows Single Responsibility Principle - handles only exception logging.
 */
@Aspect
@Component
public class ExceptionHandlingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAspect.class);
    
    @AfterThrowing(
        pointcut = "execution(* com.kheyma.service.*.*(..)) || execution(* com.kheyma.controller.*.*(..))",
        throwing = "exception"
    )
    public void logException(JoinPoint joinPoint, Exception exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String fullMethodName = className + "." + methodName;
        
        String exceptionType = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();
        
        logger.error("Exception in {}: {} - {}", 
            fullMethodName, exceptionType, exceptionMessage, exception);
        
        // Log method arguments for debugging (excluding sensitive data)
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            logExceptionContext(fullMethodName, joinPoint.getArgs(), signature.getParameterNames());
        }
    }
    
    private void logExceptionContext(String methodName, Object[] args, String[] paramNames) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                context.append(", ");
            }
            String paramName = (paramNames != null && i < paramNames.length) 
                ? paramNames[i] 
                : "arg" + i;
            
            String paramValue = maskSensitiveData(paramName, args[i]);
            context.append(paramName).append("=").append(paramValue);
        }
        
        logger.debug("Exception context for {} - Arguments: [{}]", methodName, context);
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

