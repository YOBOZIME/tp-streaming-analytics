package org.example.tpstreaminganalytics.api;

// ADD THESE IMPORTS:
import org.example.tpstreaminganalytics.service.EventProcessorService;
import org.example.tpstreaminganalytics.entity.VideoStats;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/api/sse/simple")
public class SimpleSSEServlet extends HttpServlet {

    @Inject
    EventProcessorService eventProcessor;  // This should now work

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = resp.getWriter();

        System.out.println("📡 SSE Client connected: " + req.getRemoteAddr());

        try {
            // Send initial connection event
            out.write("event: connected\n");
            out.write("data: {\"status\": \"connected\", \"message\": \"SSE stream started\", \"timestamp\": \"" +
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"}\n\n");
            out.flush();

            // Send periodic updates every 3 seconds
            int count = 0;
            while (count < 20) { // Send 20 updates then stop (for testing)
                Thread.sleep(3000);
                count++;

                // Get REAL data from EventProcessorService
                List<VideoStats> topVideos = eventProcessor.getTopVideos(5);
                int videoCount = topVideos.size();

                // You can get more real data here
                // For example: eventProcessor.getCategoryStats(), etc.

                String update = String.format(
                        "{\"eventCount\": %d, \"videoCount\": %d, \"timestamp\": \"%s\", \"update\": %d}",
                        2300 + count * 5, // Start with your 2300 events + simulated growth
                        videoCount,
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        count
                );

                out.write("event: analytics-update\n");
                out.write("data: " + update + "\n\n");
                out.flush();

                System.out.println("📡 SSE Update #" + count + " sent with " + videoCount + " videos");
            }

            // Send completion
            out.write("event: complete\n");
            out.write("data: {\"status\": \"complete\", \"message\": \"Stream finished\"}\n\n");
            out.flush();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // If EventProcessorService injection fails, send error
            out.write("event: error\n");
            out.write("data: {\"error\": \"" + e.getMessage() + "\"}\n\n");
            out.flush();
            e.printStackTrace();
        } finally {
            System.out.println("📡 SSE Client disconnected");
        }
    }
}