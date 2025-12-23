package org.example.tpstreaminganalytics.api;

import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.service.EventProcessorService;
import org.example.tpstreaminganalytics.service.AnalyticsService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * SSE Resource for Real-time Analytics Streaming
 * Endpoint: GET /api/v1/analytics/realtime/stream
 */
@Path("/api/v1/analytics/realtime")
public class SSEResource {

    @Inject
    EventProcessorService eventProcessor;

    @Inject
    AnalyticsService analyticsService;

    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;

    public SSEResource() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.scheduler = Executors.newScheduledThreadPool(2);
    }

    /**
     * SSE Endpoint - Streams real-time analytics every 2 seconds
     * GET /api/v1/analytics/realtime/stream
     */
    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamRealtime(@Context SseEventSink sseEventSink, @Context Sse sse) {
        System.out.println("📡 New SSE client connected for real-time streaming");

        // Schedule periodic updates every 2 seconds
        scheduler.scheduleAtFixedRate(() -> {
            if (sseEventSink.isClosed()) {
                System.out.println("🔌 SSE client disconnected");
                return;
            }

            try {
                // Build real-time stats
                Map<String, Object> realtimeData = buildRealtimeData();
                String jsonData = objectMapper.writeValueAsString(realtimeData);

                // Create and send SSE event
                OutboundSseEvent event = sse.newEventBuilder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .name("analytics-update")
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .data(String.class, jsonData)
                        .build();

                sseEventSink.send(event);

            } catch (Exception e) {
                System.err.println("❌ Error sending SSE event: " + e.getMessage());
                if (!sseEventSink.isClosed()) {
                    try {
                        OutboundSseEvent errorEvent = sse.newEventBuilder()
                                .name("error")
                                .data(String.class, "{\"error\": \"" + e.getMessage() + "\"}")
                                .build();
                        sseEventSink.send(errorEvent);
                    } catch (Exception ex) {
                        // Ignore
                    }
                }
            }
        }, 0, 2, TimeUnit.SECONDS);

        // Send initial connection confirmation
        try {
            OutboundSseEvent welcomeEvent = sse.newEventBuilder()
                    .name("connected")
                    .data(String.class, "{\"status\": \"connected\", \"message\": \"Real-time streaming started\"}")
                    .build();
            sseEventSink.send(welcomeEvent);
        } catch (Exception e) {
            System.err.println("❌ Error sending welcome event: " + e.getMessage());
        }
    }

    /**
     * Build real-time analytics data
     */
    private Map<String, Object> buildRealtimeData() {
        Map<String, Object> data = new HashMap<>();

        try {
            // Get top videos
            List<VideoStats> topVideos = eventProcessor.getTopVideos(5);

            // Get global stats
            Map<String, Object> globalStats = analyticsService.getGlobalStats();

            // Get category stats
            Map<String, Object> categoryStats = eventProcessor.getCategoryStats();

            data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            data.put("topVideos", topVideos);
            data.put("globalStats", globalStats);
            data.put("categoryStats", categoryStats);
            data.put("eventType", "analytics-update");

        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        return data;
    }

    /**
     * Simple test endpoint for SSE
     * GET /api/v1/analytics/realtime/test
     */
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "✅ SSE Resource is working! Access /stream for real-time data.";
    }
}
