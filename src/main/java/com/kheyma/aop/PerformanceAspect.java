package com.kheyma.aop;

import com.kheyma.aop.annotation.PerformanceMonitor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for performance monitoring.
 * Measures and logs method execution time.
 * 
 * Follows Single Responsibility Principle - handles only performance monitoring.
 */
@Aspect
@Component
public class PerformanceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);
    private static final long DEFAULT_THRESHOLD = 1000; // 1 second
    
    @Around("@annotation(performanceMonitor) || @within(performanceMonitor)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint, PerformanceMonitor performanceMonitor) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        String fullMethodName = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            logExecutionTime(fullMethodName, executionTime, performanceMonitor);
            
            return result;
            
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.warn("Method {} failed after {} ms", fullMethodName, executionTime);
            throw throwable;
        }
    }
    
    private void logExecutionTime(String methodName, long executionTime, PerformanceMonitor performanceMonitor) {
        long threshold = performanceMonitor != null ? performanceMonitor.threshold() : DEFAULT_THRESHOLD;
        boolean alwaysLog = performanceMonitor != null ? performanceMonitor.alwaysLog() : true;
        
        if (executionTime > threshold) {
            logger.warn("Performance Warning: Method {} took {} ms (threshold: {} ms)", 
                methodName, executionTime, threshold);
        } else if (alwaysLog) {
            logger.debug("Method {} executed in {} ms", methodName, executionTime);
        }
    }
}

