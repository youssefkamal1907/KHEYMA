package com.kheyma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration class to enable Aspect-Oriented Programming (AOP).
 * 
 * This configuration enables Spring AOP proxy-based aspects.
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // AOP is enabled via @EnableAspectJAutoProxy annotation
    // All @Aspect annotated classes will be automatically detected
}

