package org.example.tpstreaminganalytics.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MongoDBConfig {

    // ✅ CORRECT - Your working connection
    private static final String CONNECTION_STRING = "mongodb://admin:admin123@localhost:27017/?authSource=admin";

    private static final String DATABASE_NAME = "streaming_analytics";
    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        System.out.println("🎯 MongoDBConfig initializing...");
    }

    @Produces
    @ApplicationScoped
    public MongoClient createMongoClient() {
        System.out.println("🔌 Connecting to MongoDB...");

        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);

            // Test connection with ping
            mongoClient.getDatabase("admin").runCommand(
                    new org.bson.Document("ping", 1));

            System.out.println("✅ MongoDB connection SUCCESS!");
            System.out.println("📊 Available databases:");

            for (String dbName : mongoClient.listDatabaseNames()) {
                System.out.println("   - " + dbName +
                        (DATABASE_NAME.equals(dbName) ? " ✅ (YOUR DB)" : ""));
            }

            return mongoClient;

        } catch (Exception e) {
            System.err.println("❌ MongoDB connection FAILED: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to MongoDB", e);
        }
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase createDatabase(MongoClient mongoClient) {
        System.out.println("📁 Accessing database: " + DATABASE_NAME);
        MongoDatabase db = mongoClient.getDatabase(DATABASE_NAME);

        // Verify collections exist
        System.out.println("📋 Collections in " + DATABASE_NAME + ":");
        for (String collection : db.listCollectionNames()) {
            long count = db.getCollection(collection).countDocuments();
            System.out.println("   - " + collection + ": " + count + " documents");
        }

        return db;
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