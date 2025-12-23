package org.example.tpstreaminganalytics.servlet;

import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.service.AnalyticsService;
import org.example.tpstreaminganalytics.service.EventProcessorService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Inject
    EventProcessorService eventProcessor;

    @Inject
    AnalyticsService analyticsService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1. Récupérer les données
            List<VideoStats> topVideos = eventProcessor.getTopVideos(10);
            Map<String, Object> categoryStats = eventProcessor.getCategoryStats();
            Map<String, Object> globalStats = analyticsService.getGlobalStats();
            List<Map<String, Object>> trendingVideos = analyticsService.getTrendingVideos(5);
            List<Map<String, Object>> activityPeaks = analyticsService.getActivityPeaks();

            // 2. Passer les données à la vue
            req.setAttribute("topVideos", topVideos);
            req.setAttribute("categoryStats", categoryStats);
            req.setAttribute("globalStats", globalStats);
            req.setAttribute("trendingVideos", trendingVideos);
            req.setAttribute("activityPeaks", activityPeaks);
            req.setAttribute("timestamp", new java.util.Date());

            // 3. Forward vers JSP
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            // En cas d'erreur, afficher une page d'erreur simple
            resp.setContentType("text/html");
            resp.getWriter().println("<h1>Error loading dashboard</h1>");
            resp.getWriter().println("<p>" + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}