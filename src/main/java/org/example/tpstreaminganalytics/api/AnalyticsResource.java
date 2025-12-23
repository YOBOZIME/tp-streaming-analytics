package org.example.tpstreaminganalytics.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.entity.ViewEvent;
import org.example.tpstreaminganalytics.repository.VideoStatsRepository;
import org.example.tpstreaminganalytics.service.AnalyticsService;
import org.example.tpstreaminganalytics.service.EventProcessorService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST API Resource for Analytics
 * Base path: /api/v1/analytics
 */
@Path("/v1/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsResource {

    @Inject
    VideoStatsRepository videoStatsRepository;

    @Inject
    EventProcessorService eventProcessor;

    @Inject
    AnalyticsService analyticsService;

    private final ObjectMapper objectMapper;

    public AnalyticsResource() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // ==================== Health Check ====================

    @GET
    @Path("/health")
    public Response health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("service", "streaming-analytics");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("endpoints", Arrays.asList(
                "GET  /api/v1/analytics/health",
                "POST /api/v1/analytics/events",
                "POST /api/v1/analytics/events/batch",
                "GET  /api/v1/analytics/videos/top",
                "GET  /api/v1/analytics/videos/{videoId}/stats",
                "GET  /api/v1/analytics/users/{userId}/recommendations",
                "GET  /api/v1/analytics/stats/global",
                "GET  /api/v1/analytics/stats/categories",
                "GET  /api/v1/analytics/realtime/stream (SSE)"));
        return Response.ok(health).build();
    }

    // ==================== Event Ingestion ====================

    /**
     * POST /api/v1/analytics/events
     * Ingests a single view event
     */
    @POST
    @Path("/events")
    public Response ingestEvent(ViewEvent event) {
        try {
            // Validate and set defaults
            if (event.getEventId() == null || event.getEventId().isEmpty()) {
                event.setEventId(
                        "evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }

            // Process the event
            eventProcessor.processEvent(event);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "created");
            response.put("eventId", event.getEventId());
            response.put("message", "Event processed successfully");
            response.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /api/v1/analytics/events/batch
     * Ingests a batch of events (bulk insert)
     */
    @POST
    @Path("/events/batch")
    public Response ingestBatch(List<ViewEvent> events) {
        try {
            // Prepare events
            for (ViewEvent event : events) {
                if (event.getEventId() == null || event.getEventId().isEmpty()) {
                    event.setEventId("batch_evt_" + System.currentTimeMillis() + "_"
                            + UUID.randomUUID().toString().substring(0, 6));
                }
                if (event.getTimestamp() == null) {
                    event.setTimestamp(LocalDateTime.now());
                }
            }

            // Process batch
            long startTime = System.currentTimeMillis();
            eventProcessor.processBatch(events);
            long processingTime = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "created");
            response.put("count", events.size());
            response.put("processingTimeMs", processingTime);
            response.put("eventsPerSecond", events.size() * 1000.0 / Math.max(1, processingTime));
            response.put("message", "Batch of " + events.size() + " events processed successfully");
            response.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ==================== Video Stats ====================

    /**
     * GET /api/v1/analytics/videos/top
     * Returns top videos by views
     */
    @GET
    @Path("/videos/top")
    public Response getTopVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
        try {
            List<VideoStats> topVideos = eventProcessor.getTopVideos(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("count", topVideos.size());
            response.put("limit", limit);
            response.put("videos", topVideos);
            response.put("generatedAt", LocalDateTime.now().toString());

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/v1/analytics/videos/{videoId}/stats
     * Returns statistics for a specific video
     */
    @GET
    @Path("/videos/{videoId}/stats")
    public Response getVideoStats(@PathParam("videoId") String videoId) {
        try {
            VideoStats stats = videoStatsRepository.findById(videoId);

            if (stats == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Video not found", "videoId", videoId))
                        .build();
            }

            return Response.ok(stats).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ==================== User Recommendations ====================

    /**
     * GET /api/v1/analytics/users/{userId}/recommendations
     * Returns personalized recommendations for a user
     */
    @GET
    @Path("/users/{userId}/recommendations")
    public Response getRecommendations(@PathParam("userId") String userId) {
        try {
            List<String> recommendations = eventProcessor.getRecommendations(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("recommendations", recommendations);
            response.put("count", recommendations.size());
            response.put("generatedAt", LocalDateTime.now().toString());

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ==================== Analytics Stats ====================

    /**
     * GET /api/v1/analytics/stats/global
     * Returns global platform statistics
     */
    @GET
    @Path("/stats/global")
    public Response getGlobalStats() {
        try {
            Map<String, Object> stats = analyticsService.getGlobalStats();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/v1/analytics/stats/categories
     * Returns statistics grouped by video category
     */
    @GET
    @Path("/stats/categories")
    public Response getCategoryStats() {
        try {
            Map<String, Object> stats = eventProcessor.getCategoryStats();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/v1/analytics/trending
     * Returns trending videos
     */
    @GET
    @Path("/trending")
    public Response getTrendingVideos(@QueryParam("limit") @DefaultValue("5") int limit) {
        try {
            List<Map<String, Object>> trending = analyticsService.getTrendingVideos(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("count", trending.size());
            response.put("trending", trending);
            response.put("generatedAt", LocalDateTime.now().toString());

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /api/v1/analytics/report
     * Generates a complete analytics report
     */
    @GET
    @Path("/report")
    public Response generateReport() {
        try {
            Map<String, Object> report = analyticsService.generateAnalyticsReport();
            return Response.ok(report).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ==================== Test Endpoint ====================

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "✅ Analytics API is working! Timestamp: " + LocalDateTime.now();
    }
}