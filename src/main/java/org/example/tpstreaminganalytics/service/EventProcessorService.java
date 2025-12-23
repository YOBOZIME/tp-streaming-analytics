package org.example.tpstreaminganalytics.service;

import org.example.tpstreaminganalytics.entity.ViewEvent;
import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.entity.UserProfile;
import org.example.tpstreaminganalytics.repository.EventRepository;
import org.example.tpstreaminganalytics.repository.VideoStatsRepository;
import org.example.tpstreaminganalytics.repository.UserProfileRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventProcessorService {

    @Inject
    EventRepository eventRepository;

    @Inject
    VideoStatsRepository videoStatsRepository;

    @Inject
    UserProfileRepository userProfileRepository;

    /**
     * Traite un événement de visualisation
     */
    public void processEvent(ViewEvent event) {
        System.out.println("Processing event: " + event.getEventId());

        // 1. Sauvegarder l'événement
        eventRepository.save(event);

        // 2. Mettre à jour les statistiques vidéo
        videoStatsRepository.updateStats(event.getVideoId(), event);

        // 3. Mettre à jour le profil utilisateur
        updateUserProfile(event);

        // 4. Détecter les tendances (optionnel)
        if ("WATCH".equals(event.getAction())) {
            detectTrendingVideos();
        }
    }

    /**
     * Traite un lot d'événements (optimisé)
     */
    public void processBatch(List<ViewEvent> events) {
        System.out.println("Processing batch of " + events.size() + " events");

        // 1. Sauvegarder en batch
        eventRepository.saveBatch(events);

        // 2. Mettre à jour les statistiques
        Map<String, List<ViewEvent>> eventsByVideo = events.stream()
                .collect(Collectors.groupingBy(ViewEvent::getVideoId));

        for (Map.Entry<String, List<ViewEvent>> entry : eventsByVideo.entrySet()) {
            String videoId = entry.getKey();
            List<ViewEvent> videoEvents = entry.getValue();

            // Mettre à jour les stats pour cette vidéo
            VideoStats stats = videoStatsRepository.findById(videoId);
            if (stats == null) {
                stats = new VideoStats(videoId, "Video " + videoId, "unknown");
            }

            for (ViewEvent event : videoEvents) {
                stats.updateWithEvent(event);
            }

            videoStatsRepository.saveOrUpdate(stats);
        }

        // 3. Mettre à jour les profils utilisateurs
        Map<String, List<ViewEvent>> eventsByUser = events.stream()
                .collect(Collectors.groupingBy(ViewEvent::getUserId));

        for (Map.Entry<String, List<ViewEvent>> entry : eventsByUser.entrySet()) {
            String userId = entry.getKey();
            List<ViewEvent> userEvents = entry.getValue();

            userEvents.forEach(this::updateUserProfile);
        }
    }

    /**
     * Met à jour le profil utilisateur - CORRIGÉ
     */
    private void updateUserProfile(ViewEvent event) {
        // Extrayez les valeurs nécessaires AVANT la lambda
        final String userId = event.getUserId();
        final String videoId = event.getVideoId();
        final String action = event.getAction();
        final int duration = event.getDuration();
        final String category = getCategoryForVideo(videoId);

        userProfileRepository.findById(userId).ifPresentOrElse(
                profile -> {
                    if ("WATCH".equals(action)) {
                        profile.addToWatchHistory(videoId, duration);
                    }

                    if (category != null) {
                        profile.updateCategoryPreference(category, 1);
                    }

                    userProfileRepository.save(profile);
                },
                () -> {
                    UserProfile newProfile = new UserProfile(userId, "user_" + userId);
                    userProfileRepository.save(newProfile);
                }
        );
    }

    /**
     * Retourne les vidéos les plus populaires
     */
    public List<VideoStats> getTopVideos(int limit) {
        return videoStatsRepository.getTopVideos(limit);
    }

    /**
     * Calcule les statistiques par catégorie
     */
    public Map<String, Object> getCategoryStats() {
        List<VideoStats> allStats = videoStatsRepository.getTopVideos(1000);

        Map<String, CategoryStats> categoryMap = new HashMap<>();

        for (VideoStats stats : allStats) {
            String category = stats.getCategory();
            if (category == null) category = "unknown";

            CategoryStats catStats = categoryMap.getOrDefault(category, new CategoryStats(category));
            catStats.addVideo(stats);
            categoryMap.put(category, catStats);
        }

        Map<String, Object> result = new HashMap<>();
        for (CategoryStats catStats : categoryMap.values()) {
            result.put(catStats.getCategory(), catStats.toMap());
        }

        return result;
    }

    /**
     * Génère des recommandations pour un utilisateur
     */
    public List<String> getRecommendations(String userId) {
        List<String> recommendations = new ArrayList<>();

        userProfileRepository.findById(userId).ifPresent(profile -> {

            String preferredCategory = profile.getPreferredCategory();
            if (preferredCategory != null) {
                List<VideoStats> categoryVideos =
                        videoStatsRepository.findByCategory(preferredCategory);

                recommendations.addAll(
                        categoryVideos.stream()
                                .map(VideoStats::getVideoId)
                                .limit(3)
                                .collect(Collectors.toList())
                );
            }

            List<VideoStats> topVideos = videoStatsRepository.getTopVideos(5);
            recommendations.addAll(
                    topVideos.stream()
                            .map(VideoStats::getVideoId)
                            .limit(2)
                            .collect(Collectors.toList())
            );

            // ✅ Remove duplicates WITHOUT reassignment
            List<String> distinct =
                    recommendations.stream().distinct().collect(Collectors.toList());

            recommendations.clear();
            recommendations.addAll(distinct);

            userProfileRepository.updateRecommendations(userId, recommendations);
        });

        return recommendations;
    }



    /**
     * Détecte les vidéos tendance
     */
    private void detectTrendingVideos() {
        // Logique simplifiée de détection de tendances
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);

        // En production, on comparerait avec la période précédente
        System.out.println("Trending detection running at " + now);
    }

    /**
     * Méthode utilitaire pour obtenir la catégorie d'une vidéo
     */
    private String getCategoryForVideo(String videoId) {
        // En production, on récupérerait cette info depuis la base de données
        // Pour le TP, on utilise une logique simple
        if (videoId.contains("music") || videoId.endsWith("3")) return "music";
        if (videoId.contains("sport") || videoId.endsWith("4")) return "sports";
        if (videoId.contains("edu") || videoId.endsWith("1") || videoId.endsWith("2")) return "education";
        return "entertainment";
    }

    /**
     * Classe interne pour les statistiques par catégorie
     */
    public static class CategoryStats {
        private String category;
        private long totalViews = 0;
        private long videoCount = 0;
        private double avgDuration = 0;

        public CategoryStats(String category) {
            this.category = category;
        }

        public void addVideo(VideoStats video) {
            this.totalViews += video.getTotalViews();
            this.videoCount++;
            this.avgDuration = (this.avgDuration * (videoCount - 1) + video.getAvgDuration()) / videoCount;
        }

        public String getCategory() { return category; }
        public long getTotalViews() { return totalViews; }
        public long getVideoCount() { return videoCount; }
        public double getAvgDuration() { return avgDuration; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("totalViews", totalViews);
            map.put("videoCount", videoCount);
            map.put("avgDuration", avgDuration);
            map.put("avgViewsPerVideo", videoCount > 0 ? (double) totalViews / videoCount : 0);
            return map;
        }
    }
}