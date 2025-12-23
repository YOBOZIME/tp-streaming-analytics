package org.example.tpstreaminganalytics.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MongoDBConfig {

    // Connection string with authentication for Docker MongoDB
    // Format: mongodb://username:password@host:port/?authSource=admin
    private static final String CONNECTION_STRING = "mongodb://admin:admin123@localhost:27017/?authSource=admin";

    private static final String DATABASE_NAME = "streaming_analytics";

    private MongoClient mongoClient;

    @Produces
    @ApplicationScoped
    public MongoClient createMongoClient() {
        System.out.println("🔌 Connecting to MongoDB: " + CONNECTION_STRING);
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            System.out.println("✅ MongoDB connection established successfully!");
            return mongoClient;
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to MongoDB: " + e.getMessage());
            // Fallback: try without authentication (for local testing)
            System.out.println("🔄 Trying fallback connection without authentication...");
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            return mongoClient;
        }
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase createDatabase(MongoClient mongoClient) {
        System.out.println("📁 Accessing database: " + DATABASE_NAME);
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    public void closeMongoClient(@Disposes MongoClient mongoClient) {
        if (mongoClient != null) {
            System.out.println("🔒 Closing MongoDB connection...");
            mongoClient.close();
        }
    }

    @PreDestroy
    public void cleanup() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}