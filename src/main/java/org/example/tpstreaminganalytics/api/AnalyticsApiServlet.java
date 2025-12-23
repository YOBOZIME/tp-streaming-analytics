package org.example.tpstreaminganalytics.api;

// AJOUTEZ ces imports
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tpstreaminganalytics.repository.VideoStatsRepository; // IMPORTANT !
import org.example.tpstreaminganalytics.repository.EventRepository; // IMPORTANT !
import org.example.tpstreaminganalytics.repository.UserProfileRepository; // IMPORTANT !

import org.example.tpstreaminganalytics.entity.ViewEvent;
import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.service.AnalyticsService;
import org.example.tpstreaminganalytics.service.EventProcessorService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/rest/*")
public class AnalyticsApiServlet extends HttpServlet {

    @Inject
    EventProcessorService eventProcessor;

    @Inject
    AnalyticsService analyticsService;

    // AJOUTEZ cette injection
    @Inject
    VideoStatsRepository videoStatsRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/health")) {
                // GET /api/health
                handleHealth(req, resp, out);
            }
            else if (pathInfo.equals("/videos/top")) {
                // GET /api/videos/top
                handleTopVideos(req, resp, out);
            }
            else if (pathInfo.startsWith("/videos/") && pathInfo.endsWith("/stats")) {
                // GET /api/videos/{videoId}/stats
                handleVideoStats(req, resp, out, pathInfo);
            }
            else if (pathInfo.startsWith("/users/") && pathInfo.contains("/recommendations")) {
                // GET /api/users/{userId}/recommendations
                handleUserRecommendations(req, resp, out, pathInfo);
            }
            else if (pathInfo.equals("/stats/global")) {
                // GET /api/stats/global
                handleGlobalStats(req, resp, out);
            }
            else if (pathInfo.equals("/stats/categories")) {
                // GET /api/stats/categories
                handleCategoryStats(req, resp, out);
            }
            else if (pathInfo.equals("/trending")) {
                // GET /api/trending
                handleTrending(req, resp, out);
            }
            else if (pathInfo.equals("/analytics/report")) {
                // GET /api/analytics/report
                handleAnalyticsReport(req, resp, out);
            }
            else {
                resp.setStatus(404);
                out.println(objectMapper.writeValueAsString(
                        Map.of("error", "Endpoint not found", "path", pathInfo)
                ));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            out.println(objectMapper.writeValueAsString(
                    Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now())
            ));
            e.printStackTrace();
        }

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/events")) {
                // POST /api/events
                handlePostEvent(req, resp, out);
            }
            else if (pathInfo.equals("/events/batch")) {
                // POST /api/events/batch
                handlePostBatchEvents(req, resp, out);
            }
            else {
                resp.setStatus(404);
                out.println(objectMapper.writeValueAsString(
                        Map.of("error", "Endpoint not found", "path", pathInfo)
                ));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            out.println(objectMapper.writeValueAsString(
                    Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now())
            ));
            e.printStackTrace();
        }

        out.close();
    }

    // === Handlers pour GET ===

    private void handleHealth(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("service", "streaming-analytics-api");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now());
        health.put("endpoints", Arrays.asList(
                "GET  /api/health",
                "GET  /api/videos/top",
                "GET  /api/videos/{id}/stats",
                "GET  /api/users/{id}/recommendations",
                "GET  /api/stats/global",
                "GET  /api/stats/categories",
                "GET  /api/trending",
                "POST /api/events",
                "POST /api/events/batch"
        ));

        out.println(objectMapper.writeValueAsString(health));
    }

    private void handleTopVideos(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        String limitParam = req.getParameter("limit");
        int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;

        List<VideoStats> topVideos = eventProcessor.getTopVideos(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("count", topVideos.size());
        response.put("limit", limit);
        response.put("videos", topVideos);
        response.put("generatedAt", LocalDateTime.now());

        out.println(objectMapper.writeValueAsString(response));
    }

    private void handleVideoStats(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String pathInfo)
            throws IOException {
        // Extraire videoId du chemin /videos/{videoId}/stats
        String[] parts = pathInfo.split("/");
        if (parts.length < 4) {
            resp.setStatus(400);
            out.println(objectMapper.writeValueAsString(
                    Map.of("error", "Invalid path format", "expected", "/api/videos/{videoId}/stats")
            ));
            return;
        }

        String videoId = parts[2];
        VideoStats stats = videoStatsRepository.findById(videoId);

        if (stats == null) {
            resp.setStatus(404);
            out.println(objectMapper.writeValueAsString(
                    Map.of("error", "Video not found", "videoId", videoId)
            ));
            return;
        }

        out.println(objectMapper.writeValueAsString(stats));
    }

    private void handleUserRecommendations(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String pathInfo)
            throws IOException {
        // Extraire userId du chemin /users/{userId}/recommendations
        String[] parts = pathInfo.split("/");
        if (parts.length < 4) {
            resp.setStatus(400);
            out.println(objectMapper.writeValueAsString(
                    Map.of("error", "Invalid path format", "expected", "/api/users/{userId}/recommendations")
            ));
            return;
        }

        String userId = parts[2];
        List<String> recommendations = eventProcessor.getRecommendations(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("recommendations", recommendations);
        response.put("count", recommendations.size());
        response.put("generatedAt", LocalDateTime.now());

        out.println(objectMapper.writeValueAsString(response));
    }

    private void handleGlobalStats(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        Map<String, Object> stats = analyticsService.getGlobalStats();
        out.println(objectMapper.writeValueAsString(stats));
    }

    private void handleCategoryStats(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        Map<String, Object> stats = eventProcessor.getCategoryStats();
        out.println(objectMapper.writeValueAsString(stats));
    }

    private void handleTrending(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        String limitParam = req.getParameter("limit");
        int limit = limitParam != null ? Integer.parseInt(limitParam) : 5;

        List<Map<String, Object>> trending = analyticsService.getTrendingVideos(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("count", trending.size());
        response.put("trending", trending);
        response.put("generatedAt", LocalDateTime.now());

        out.println(objectMapper.writeValueAsString(response));
    }

    private void handleAnalyticsReport(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        Map<String, Object> report = analyticsService.generateAnalyticsReport();
        out.println(objectMapper.writeValueAsString(report));
    }

    // === Handlers pour POST ===

    private void handlePostEvent(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        // Lire le corps de la requête
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        // Parser l'événement
        ViewEvent event = objectMapper.readValue(jsonBuilder.toString(), ViewEvent.class);

        // Valider l'événement
        if (event.getEventId() == null || event.getEventId().isEmpty()) {
            event.setEventId("evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        // Traiter l'événement
        eventProcessor.processEvent(event);

        // Réponse
        resp.setStatus(201); // Created
        Map<String, Object> response = new HashMap<>();
        response.put("status", "created");
        response.put("eventId", event.getEventId());
        response.put("message", "Event processed successfully");
        response.put("timestamp", LocalDateTime.now());

        out.println(objectMapper.writeValueAsString(response));
    }

    private void handlePostBatchEvents(HttpServletRequest req, HttpServletResponse resp, PrintWriter out)
            throws IOException {
        // Lire le corps de la requête
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        // Parser le tableau d'événements
        ViewEvent[] eventsArray = objectMapper.readValue(jsonBuilder.toString(), ViewEvent[].class);
        List<ViewEvent> events = Arrays.asList(eventsArray);

        // Préparer les événements
        for (ViewEvent event : events) {
            if (event.getEventId() == null || event.getEventId().isEmpty()) {
                event.setEventId("batch_evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6));
            }
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }
        }

        // Traiter en batch
        eventProcessor.processBatch(events);

        // Réponse
        resp.setStatus(201); // Created
        Map<String, Object> response = new HashMap<>();
        response.put("status", "created");
        response.put("count", events.size());
        response.put("message", "Batch of " + events.size() + " events processed successfully");
        response.put("timestamp", LocalDateTime.now());

        out.println(objectMapper.writeValueAsString(response));
    }
}