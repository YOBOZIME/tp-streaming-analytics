package org.example.tpstreaminganalytics.entity;

import java.time.LocalDateTime;
import java.util.Objects;

// SUPPRIMEZ toutes les annotations JPA !
public class VideoStats {

    // Pas d'annotations @Id ou @Column !
    private String videoId;
    private String title;
    private String category; // music, sports, education, entertainment, news
    private String description;
    private long totalViews = 0;
    private long totalDuration = 0; // Somme des durées en secondes
    private double avgDuration = 0.0;
    private int viewCount = 0; // Nombre de sessions de visionnage
    private long likes = 0;
    private long shares = 0;
    private LocalDateTime lastUpdated;

    // Constructeurs
    public VideoStats() {
        this.lastUpdated = LocalDateTime.now();
    }

    public VideoStats(String videoId, String title, String category) {
        this();
        this.videoId = videoId;
        this.title = title;
        this.category = category;
    }

    // Méthode pour mettre à jour les statistiques avec un nouvel événement
    public void updateWithEvent(ViewEvent event) {
        if ("WATCH".equals(event.getAction())) {
            this.viewCount++;
            this.totalViews++;
            this.totalDuration += event.getDuration();
            // Calculer la durée moyenne
            if (viewCount > 0) {
                this.avgDuration = (double) totalDuration / viewCount;
            }
        } else if ("LIKE".equals(event.getAction())) {
            this.likes++;
        } else if ("SHARE".equals(event.getAction())) {
            this.shares++;
        }
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters et Setters seulement
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getTotalViews() { return totalViews; }
    public void setTotalViews(long totalViews) { this.totalViews = totalViews; }

    public long getTotalDuration() { return totalDuration; }
    public void setTotalDuration(long totalDuration) { this.totalDuration = totalDuration; }

    public double getAvgDuration() { return avgDuration; }
    public void setAvgDuration(double avgDuration) { this.avgDuration = avgDuration; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }

    public long getShares() { return shares; }
    public void setShares(long shares) { this.shares = shares; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoStats that = (VideoStats) o;
        return Objects.equals(videoId, that.videoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId);
    }

    @Override
    public String toString() {
        return "VideoStats{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", totalViews=" + totalViews +
                ", avgDuration=" + avgDuration +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}