package org.example.tpstreaminganalytics.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tpstreaminganalytics.service.EventProcessorService;
import org.example.tpstreaminganalytics.entity.ViewEvent;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/api/generate-events")
public class EventGeneratorApiServlet extends HttpServlet {

    @Inject
    EventProcessorService eventProcessor;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    // Copy these constants from your existing DataGeneratorServlet
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = resp.getWriter();

        // Get count parameter
        String countParam = req.getParameter("count");
        int count = 100;
        try {
            if (countParam != null && !countParam.isEmpty()) {
                count = Integer.parseInt(countParam);
                count = Math.min(count, 5000); // Reasonable limit
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
            }

            long processingTime = System.currentTimeMillis() - startTime;
            double eventsPerSecond = count * 1000.0 / Math.max(1, processingTime);

            // Create response map
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "success");
            response.put("message", "Generated and processed " + count + " test events");
            response.put("count", count);
            response.put("processingTimeMs", processingTime);
            response.put("eventsPerSecond", eventsPerSecond);
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("generatedEvents", events.size());

            // Convert to proper JSON
            String jsonResponse = objectMapper.writeValueAsString(response);
            out.print(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(500);
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            String jsonError = objectMapper.writeValueAsString(errorResponse);
            out.print(jsonError);
        }

        out.close();
    }

    private List<ViewEvent> generateEvents(int count) {
        List<ViewEvent> events = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ViewEvent event = new ViewEvent();
            event.setEventId("api_evt_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6));
            event.setUserId(USER_IDS[random.nextInt(USER_IDS.length)]);
            event.setVideoId(VIDEO_IDS[random.nextInt(VIDEO_IDS.length)]);
            event.setTimestamp(LocalDateTime.now().minusMinutes(random.nextInt(60)));
            event.setAction(ACTIONS[random.nextInt(ACTIONS.length)]);
            event.setDuration(random.nextInt(3600));
            event.setQuality(QUALITIES[random.nextInt(QUALITIES.length)]);
            event.setDeviceType(DEVICES[random.nextInt(DEVICES.length)]);
            events.add(event);
        }

        return events;
    }
}