package org.example.tpstreaminganalytics.entity;

import java.time.LocalDateTime;
import java.util.*;

// Supprimez toutes les annotations JPA !
public class UserProfile {

    private String userId;
    private String username;
    private String email;
    private LocalDateTime registrationDate;
    private String preferredCategory;
    private String preferredQuality;

    // Stocke directement des structures complexes pour MongoDB
    private List<WatchHistoryItem> watchHistory = new ArrayList<>();
    private Map<String, Integer> categoryPreferences = new HashMap<>();
    private List<String> recommendedVideoIds = new ArrayList<>();

    // Champs supplémentaires pour faciliter les requêtes
    private String lastVideoWatched;
    private LocalDateTime lastWatchTime;
    private long totalWatchTime = 0;

    // Classe interne - PAS @Embeddable !
    public static class WatchHistoryItem {
        private String videoId;
        private LocalDateTime watchedAt;
        private int watchDuration;

        // Constructeurs
        public WatchHistoryItem() {}
        public WatchHistoryItem(String videoId, LocalDateTime watchedAt, int watchDuration) {
            this.videoId = videoId;
            this.watchedAt = watchedAt;
            this.watchDuration = watchDuration;
        }

        // Getters et setters seulement (pas d'annotations)
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }

        public LocalDateTime getWatchedAt() { return watchedAt; }
        public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }

        public int getWatchDuration() { return watchDuration; }
        public void setWatchDuration(int watchDuration) { this.watchDuration = watchDuration; }

        @Override
        public String toString() {
            return "WatchHistoryItem{" +
                    "videoId='" + videoId + '\'' +
                    ", watchedAt=" + watchedAt +
                    ", watchDuration=" + watchDuration +
                    '}';
        }
    }

    // Constructeurs
    public UserProfile() {
        this.registrationDate = LocalDateTime.now();
    }

    public UserProfile(String userId, String username) {
        this();
        this.userId = userId;
        this.username = username;
    }

    // Méthodes utilitaires
    public void addToWatchHistory(String videoId, int duration) {
        WatchHistoryItem item = new WatchHistoryItem(videoId, LocalDateTime.now(), duration);
        this.watchHistory.add(item);
        this.lastVideoWatched = videoId;
        this.lastWatchTime = LocalDateTime.now();
        this.totalWatchTime += duration;

        // Garder seulement les 50 dernières entrées
        if (watchHistory.size() > 50) {
            watchHistory.remove(0);
        }
    }

    public void updateCategoryPreference(String category, int points) {
        categoryPreferences.merge(category, points, Integer::sum);

        // Trouver la catégorie préférée
        preferredCategory = categoryPreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Getters et setters (tous)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) {
        this.preferredCategory = preferredCategory;
    }

    public String getPreferredQuality() { return preferredQuality; }
    public void setPreferredQuality(String preferredQuality) {
        this.preferredQuality = preferredQuality;
    }

    public List<WatchHistoryItem> getWatchHistory() { return watchHistory; }
    public void setWatchHistory(List<WatchHistoryItem> watchHistory) {
        this.watchHistory = watchHistory;
    }

    public Map<String, Integer> getCategoryPreferences() { return categoryPreferences; }
    public void setCategoryPreferences(Map<String, Integer> categoryPreferences) {
        this.categoryPreferences = categoryPreferences;
    }

    public List<String> getRecommendedVideoIds() { return recommendedVideoIds; }
    public void setRecommendedVideoIds(List<String> recommendedVideoIds) {
        this.recommendedVideoIds = recommendedVideoIds;
    }

    public String getLastVideoWatched() { return lastVideoWatched; }
    public void setLastVideoWatched(String lastVideoWatched) {
        this.lastVideoWatched = lastVideoWatched;
    }

    public LocalDateTime getLastWatchTime() { return lastWatchTime; }
    public void setLastWatchTime(LocalDateTime lastWatchTime) {
        this.lastWatchTime = lastWatchTime;
    }

    public long getTotalWatchTime() { return totalWatchTime; }
    public void setTotalWatchTime(long totalWatchTime) {
        this.totalWatchTime = totalWatchTime;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", watchHistory=" + watchHistory.size() + " items" +
                ", totalWatchTime=" + totalWatchTime +
                '}';
    }
}