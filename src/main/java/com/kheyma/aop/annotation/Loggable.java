package com.kheyma.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable method-level logging.
 * When applied to a method, it will log method entry, parameters, and exit.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    
    /**
     * Whether to log method parameters.
     * 
     * @return true if parameters should be logged
     */
    boolean logParameters() default true;
    
    /**
     * Whether to log return value.
     * 
     * @return true if return value should be logged
     */
    boolean logReturnValue() default true;
    
    /**
     * Log level for method entry.
     * 
     * @return log level
     */
    String level() default "DEBUG";
}

