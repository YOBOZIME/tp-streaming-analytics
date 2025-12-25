package org.example.tpstreaminganalytics.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

@WebServlet("/api/check-mongodb")
public class CheckMongoDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><body>");
        out.println("<h1>MongoDB Connection Test</h1>");

        // Test different connection strings
        String[] connections = {
                "mongodb://admin:admin123@localhost:27017/?authSource=admin",
                "mongodb://localhost:27017",
                "mongodb://localhost:27017/streaming_analytics",
                "mongodb://root:example@localhost:27017/?authSource=admin"  // Common default
        };

        for (String connectionString : connections) {
            out.println("<h3>Testing: " + connectionString + "</h3>");
            testConnection(connectionString, out);
            out.println("<hr>");
        }

        out.println("</body></html>");
    }

    private void testConnection(String connectionString, PrintWriter out) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            out.println("<p>✅ Connection successful!</p>");

            // List databases
            out.println("<p><strong>Databases:</strong></p><ul>");
            for (String dbName : mongoClient.listDatabaseNames()) {
                out.println("<li>" + dbName);

                // Check if this is streaming_analytics
                if ("streaming_analytics".equals(dbName)) {
                    MongoDatabase db = mongoClient.getDatabase(dbName);
                    out.println(" <strong>✓ YOUR DATABASE!</strong>");
                    out.println("<ul>");

                    // List collections
                    for (String collection : db.listCollectionNames()) {
                        MongoCollection<Document> col = db.getCollection(collection);
                        long count = col.countDocuments();
                        out.println("<li>" + collection + " (" + count + " documents)</li>");
                    }
                    out.println("</ul>");
                }
                out.println("</li>");
            }
            out.println("</ul>");

        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Connection failed: " + e.getMessage() + "</p>");
        }
    }
}