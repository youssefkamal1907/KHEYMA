package com.kheyma.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable security auditing for methods.
 * Logs user actions, access attempts, and security-related operations.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityAudit {
    
    /**
     * Action description for audit log.
     * 
     * @return action description
     */
    String action() default "";
    
    /**
     * Whether to log request parameters.
     * 
     * @return true if parameters should be logged
     */
    boolean logParameters() default false;
    
    /**
     * Whether this is a sensitive operation.
     * 
     * @return true if operation is sensitive
     */
    boolean sensitive() default false;
}

