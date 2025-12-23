package org.example.tpstreaminganalytics.entity;

import java.time.LocalDateTime;
import java.util.Objects;

// Pas d'annotations JPA !
public class ViewEvent {

    private String eventId;
    private String userId;
    private String videoId;
    private LocalDateTime timestamp;
    private String action; // WATCH, PAUSE, STOP, LIKE, SHARE
    private int duration;
    private String quality; // 1080p, 720p, 480p
    private String deviceType; // mobile, desktop, tablet, tv

    // Constructeurs
    public ViewEvent() {}

    public ViewEvent(String eventId, String userId, String videoId,
                     LocalDateTime timestamp, String action, int duration,
                     String quality, String deviceType) {
        this.eventId = eventId;
        this.userId = userId;
        this.videoId = videoId;
        this.timestamp = timestamp;
        this.action = action;
        this.duration = duration;
        this.quality = quality;
        this.deviceType = deviceType;
    }

    // Getters et Setters seulement
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewEvent viewEvent = (ViewEvent) o;
        return Objects.equals(eventId, viewEvent.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "ViewEvent{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", duration=" + duration +
                ", quality='" + quality + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}