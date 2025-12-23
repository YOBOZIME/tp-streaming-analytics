package org.example.tpstreaminganalytics.api;

import org.example.tpstreaminganalytics.entity.ViewEvent;
import org.example.tpstreaminganalytics.service.EventProcessorService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Data Generator Servlet
 * Generates test events for the streaming analytics platform
 * 
 * Usage: GET /generate-test-data?count=100
 */
@WebServlet("/generate-test-data")
public class DataGeneratorServlet extends HttpServlet {

    @Inject
    EventProcessorService eventProcessor;

    private static final String[] ACTIONS = { "WATCH", "PAUSE", "STOP", "LIKE", "SHARE" };
    private static final String[] QUALITIES = { "1080p", "720p", "480p", "360p" };
    private static final String[] DEVICES = { "mobile", "desktop", "tablet", "tv" };
    private static final String[] VIDEO_IDS = {
            "video_001", "video_002", "video_003", "video_004", "video_005",
            "video_006", "video_007", "video_008", "video_009", "video_010"
    };
    private static final String[] USER_IDS = {
            "user_001", "user_002", "user_003", "user_004", "user_005",
            "user_006", "user_007", "user_008", "user_009", "user_010"
    };

    private final Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // Get count parameter (default: 100)
        String countParam = req.getParameter("count");
        int count = 100;
        try {
            if (countParam != null && !countParam.isEmpty()) {
                count = Integer.parseInt(countParam);
                count = Math.min(count, 10000); // Max 10K events at once
            }
        } catch (NumberFormatException e) {
            // Use default
        }

        try {
            long startTime = System.currentTimeMillis();

            // Generate events
            List<ViewEvent> events = generateEvents(count);

            // Process in batch
            if (eventProcessor != null) {
                eventProcessor.processBatch(events);
            } else {
                out.println("{\"error\": \"EventProcessor not available - CDI injection failed\"}");
                return;
            }

            long processingTime = System.currentTimeMillis() - startTime;
            double eventsPerSecond = count * 1000.0 / Math.max(1, processingTime);

            // Build response
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"status\": \"success\",\n");
            json.append("  \"message\": \"Generated and processed ").append(count).append(" test events\",\n");
            json.append("  \"count\": ").append(count).append(",\n");
            json.append("  \"processingTimeMs\": ").append(processingTime).append(",\n");
            json.append("  \"eventsPerSecond\": ").append(String.format("%.2f", eventsPerSecond)).append(",\n");
            json.append("  \"timestamp\": \"").append(LocalDateTime.now()).append("\"\n");
            json.append("}");

            resp.setStatus(200);
            out.println(json.toString());

        } catch (Exception e) {
            resp.setStatus(500);
            out.println("{\"error\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            e.printStackTrace();
        }

        out.close();
    }

    /**
     * Generate random test events
     */
    private List<ViewEvent> generateEvents(int count) {
        List<ViewEvent> events = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ViewEvent event = new ViewEvent();

            event.setEventId(
                    "gen_evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6));
            event.setUserId(USER_IDS[random.nextInt(USER_IDS.length)]);
            event.setVideoId(VIDEO_IDS[random.nextInt(VIDEO_IDS.length)]);
            event.setTimestamp(LocalDateTime.now().minusMinutes(random.nextInt(60)));
            event.setAction(ACTIONS[random.nextInt(ACTIONS.length)]);
            event.setDuration(random.nextInt(3600)); // 0-3600 seconds
            event.setQuality(QUALITIES[random.nextInt(QUALITIES.length)]);
            event.setDeviceType(DEVICES[random.nextInt(DEVICES.length)]);

            events.add(event);
        }

        return events;
    }
}