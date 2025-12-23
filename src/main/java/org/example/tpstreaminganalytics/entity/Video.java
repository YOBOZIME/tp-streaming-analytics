package org.example.tpstreaminganalytics.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Pas d'annotations JPA !
public class Video {

    private String videoId;
    private String title;
    private String description;
    private String category; // music, sports, education, entertainment, news
    private String channel;
    private String uploaderId;
    private int duration; // en secondes
    private LocalDateTime uploadDate;
    private List<String> tags = new ArrayList<>(); // Utilisez List<String>, pas String[]
    private String thumbnailUrl;

    // Constructeurs
    public Video() {
        this.uploadDate = LocalDateTime.now();
    }

    public Video(String videoId, String title, String description,
                 String category, int duration) {
        this();
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.duration = duration;
    }

    // Getters et Setters seulement
    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", duration=" + duration +
                '}';
    }
}