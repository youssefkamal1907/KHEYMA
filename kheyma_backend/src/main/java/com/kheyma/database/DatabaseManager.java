package com.kheyma.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Singleton Database Manager for MongoDB operations.
 * Provides centralized database access and connection management.
 * 
 * This class follows the Singleton design pattern and ensures
 * thread-safe database operations.
 * 
 * Note: Spring beans are singletons by default, but this class
 * implements explicit singleton pattern for demonstration.
 */
@Component
public class DatabaseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    private static volatile DatabaseManager instance;
    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;
    private String databaseName;
    
    public DatabaseManager(MongoTemplate mongoTemplate, MongoClient mongoClient) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
    }
    
    @PostConstruct
    public void initialize() {
        this.databaseName = mongoTemplate.getDb().getName();
        
        // Thread-safe singleton initialization
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = this;
                logger.info("DatabaseManager singleton instance initialized. Database: {}", databaseName);
            }
        }
    }
    
    /**
     * Get the singleton instance of DatabaseManager.
     * 
     * @return DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Ensure it's a Spring bean.");
        }
        return instance;
    }
    
    /**
     * Get the MongoTemplate for database operations.
     * 
     * @return MongoTemplate instance
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
    
    /**
     * Get the MongoClient for direct MongoDB operations.
     * 
     * @return MongoClient instance
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
    /**
     * Get the database instance.
     * 
     * @return MongoDatabase instance
     */
    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase(databaseName);
    }
    
    /**
     * Get the database name.
     * 
     * @return database name
     */
    public String getDatabaseName() {
        return databaseName;
    }
    
    /**
     * Check if database connection is healthy.
     * 
     * @return true if connection is healthy, false otherwise
     */
    public boolean isConnectionHealthy() {
        try {
            mongoClient.getDatabase(databaseName).listCollectionNames().first();
            return true;
        } catch (Exception e) {
            logger.error("Database connection health check failed", e);
            return false;
        }
    }
    
    /**
     * Get collection count for monitoring purposes.
     * 
     * @param collectionName collection name
     * @return document count
     */
    public long getCollectionCount(String collectionName) {
        try {
            return mongoTemplate.getCollection(collectionName).countDocuments();
        } catch (Exception e) {
            logger.error("Failed to get count for collection: {}", collectionName, e);
            return 0;
        }
    }
}

