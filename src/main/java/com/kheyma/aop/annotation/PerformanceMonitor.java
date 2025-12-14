package com.kheyma.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable performance monitoring for methods.
 * Logs execution time and warns if execution exceeds threshold.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceMonitor {
    
    /**
     * Threshold in milliseconds. If execution time exceeds this,
     * a warning will be logged.
     * 
     * @return threshold in milliseconds
     */
    long threshold() default 1000;
    
    /**
     * Whether to log execution time for all invocations.
     * 
     * @return true if execution time should always be logged
     */
    boolean alwaysLog() default true;
}

