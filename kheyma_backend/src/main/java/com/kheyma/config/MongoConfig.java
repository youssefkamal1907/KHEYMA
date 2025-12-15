package com.kheyma.config;

import org.springframework.context.annotation.Configuration;

/**
 * MongoDB configuration class.
 * 
 * MongoClient and MongoTemplate are auto-configured by Spring Boot
 * when spring-boot-starter-data-mongodb is on the classpath.
 * This configuration class can be extended for custom MongoDB settings.
 */
@Configuration
public class MongoConfig {
    // MongoClient and MongoTemplate are auto-configured by Spring Boot
    // No explicit bean definition needed unless custom configuration is required
}

