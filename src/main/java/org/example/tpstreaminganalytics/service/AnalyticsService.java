package org.example.tpstreaminganalytics.service;

import org.example.tpstreaminganalytics.entity.ViewEvent;
import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.repository.EventRepository;
import org.example.tpstreaminganalytics.repository.VideoStatsRepository; // AJOUTEZ cet import

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@ApplicationScoped
public class AnalyticsService {

    @Inject
    EventRepository eventRepository;

    @Inject
    EventProcessorService eventProcessor;

    // AJOUTEZ cette injection
    @Inject
    VideoStatsRepository videoStatsRepository;

    /**
     * Calcule les statistiques globales
     */
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalEvents = eventRepository.countAll();
        stats.put("totalEvents", totalEvents);
        stats.put("timestamp", LocalDateTime.now());

        // Statistiques par action
        List<ViewEvent> recentEvents = eventRepository.findByTimeRange(
                LocalDateTime.now().minusHours(24),
                LocalDateTime.now()
        );

        long watchCount = recentEvents.stream()
                .filter(e -> "WATCH".equals(e.getAction()))
                .count();
        long likeCount = recentEvents.stream()
                .filter(e -> "LIKE".equals(e.getAction()))
                .count();

        stats.put("last24hWatches", watchCount);
        stats.put("last24hLikes", likeCount);
        stats.put("eventsPerHour", watchCount / 24.0);

        return stats;
    }

    /**
     * Calcule les statistiques détaillées pour une période
     */
    public Map<String, Object> getTimeRangeStats(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();

        List<ViewEvent> events = eventRepository.findByTimeRange(start, end);

        long watchEvents = events.stream()
                .filter(e -> "WATCH".equals(e.getAction()))
                .count();
        long totalDuration = events.stream()
                .filter(e -> "WATCH".equals(e.getAction()))
                .mapToInt(ViewEvent::getDuration)
                .sum();

        // Regroupement par heure
        Map<Integer, Long> eventsByHour = new HashMap<>();
        for (ViewEvent event : events) {
            if ("WATCH".equals(event.getAction())) {
                int hour = event.getTimestamp().getHour();
                eventsByHour.put(hour, eventsByHour.getOrDefault(hour, 0L) + 1);
            }
        }

        stats.put("totalEvents", events.size());
        stats.put("watchEvents", watchEvents);
        stats.put("avgWatchDuration", watchEvents > 0 ? (double) totalDuration / watchEvents : 0);
        stats.put("eventsByHour", eventsByHour);
        stats.put("timeRange", start + " to " + end);

        return stats;
    }

    /**
     * Détecte les vidéos tendance (croissance rapide)
     */
    public List<Map<String, Object>> getTrendingVideos(int limit) {
        List<Map<String, Object>> trending = new ArrayList<>();

        // Comparer les 2 dernières heures vs les 2 heures précédentes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recentEnd = now;
        LocalDateTime recentStart = now.minus(2, ChronoUnit.HOURS);
        LocalDateTime previousEnd = recentStart;
        LocalDateTime previousStart = now.minus(4, ChronoUnit.HOURS);

        // Pour le TP, on retourne des données simulées
        for (int i = 1; i <= limit; i++) {
            Map<String, Object> video = new HashMap<>();
            video.put("videoId", "trending_" + i);
            video.put("title", "Trending Video " + i);
            video.put("growth", 100 + (i * 25) + "%"); // Croissance simulée
            video.put("currentViews", 500 + (i * 100));
            video.put("previousViews", 200 + (i * 50));
            trending.add(video);
        }

        return trending;
    }

    /**
     * Calcule les pics d'activité
     */
    public List<Map<String, Object>> getActivityPeaks() {
        List<Map<String, Object>> peaks = new ArrayList<>();

        // Simuler des pics d'activité
        String[] times = {"09:00", "12:00", "18:00", "21:00"};
        int[] activities = {850, 620, 1200, 950};

        for (int i = 0; i < times.length; i++) {
            Map<String, Object> peak = new HashMap<>();
            peak.put("time", times[i]);
            peak.put("activityLevel", activities[i]);
            peak.put("description", "Peak activity period");
            peaks.add(peak);
        }

        return peaks;
    }

    /**
     * Génère un rapport d'analyse
     */
    public Map<String, Object> generateAnalyticsReport() {
        Map<String, Object> report = new HashMap<>();

        // Statistiques globales
        report.put("globalStats", getGlobalStats());

        // Top vidéos
        List<VideoStats> topVideos = eventProcessor.getTopVideos(10);
        report.put("topVideos", topVideos);

        // Statistiques par catégorie
        Map<String, Object> categoryStats = eventProcessor.getCategoryStats();
        report.put("categoryStats", categoryStats);

        // Vidéos tendance
        List<Map<String, Object>> trending = getTrendingVideos(5);
        report.put("trendingVideos", trending);

        // Pics d'activité
        List<Map<String, Object>> peaks = getActivityPeaks();
        report.put("activityPeaks", peaks);

        // Recommandations populaires
        report.put("generatedAt", LocalDateTime.now());
        report.put("reportId", "RPT-" + System.currentTimeMillis());

        return report;
    }
}