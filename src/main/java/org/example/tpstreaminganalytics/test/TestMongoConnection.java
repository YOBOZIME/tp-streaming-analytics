package org.example.tpstreaminganalytics.test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class TestMongoConnection {
    public static void main(String[] args) {
        try {
            // Test de connexion MongoDB
            MongoClient client = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = client.getDatabase("streaming_analytics");

            // Lister les collections
            for (String collectionName : database.listCollectionNames()) {
                System.out.println("Collection: " + collectionName);
            }

            // Tester une insertion
            database.getCollection("test").insertOne(
                    new Document("test", "MongoDB fonctionne!")
            );

            System.out.println("✅ MongoDB connecté avec succès!");
            client.close();

        } catch (Exception e) {
            System.err.println("❌ Erreur MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}