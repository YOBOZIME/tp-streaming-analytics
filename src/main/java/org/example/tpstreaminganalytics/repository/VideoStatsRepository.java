package org.example.tpstreaminganalytics.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.bson.Document;
import org.example.tpstreaminganalytics.entity.VideoStats;
import org.example.tpstreaminganalytics.entity.ViewEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class VideoStatsRepository {

    @Inject
    MongoDatabase mongoDatabase;

    @Inject
    EventRepository eventRepository;

    private MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection("video_stats");
    }

    public void saveOrUpdate(VideoStats stats) {
        Document doc = new Document()
                .append("videoId", stats.getVideoId())
                .append("title", stats.getTitle())
                .append("category", stats.getCategory())
                .append("totalViews", stats.getTotalViews())
                .append("avgDuration", stats.getAvgDuration())
                .append("lastUpdated", Date.from(stats.getLastUpdated().toInstant(ZoneOffset.UTC)));

        // Upsert: insert if doesn't exist, update if exists
        getCollection().replaceOne(
                Filters.eq("videoId", stats.getVideoId()),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true));
    }

    public VideoStats findById(String videoId) {
        Document doc = getCollection().find(Filters.eq("videoId", videoId)).first();
        return documentToStats(doc);
    }

    public List<VideoStats> getTopVideos(int limit) {
        List<VideoStats> stats = new ArrayList<>();

        // Trier par totalViews descendant
        for (Document doc : getCollection().find()
                .sort(new Document("totalViews", -1))
                .limit(limit)) {
            stats.add(documentToStats(doc));
        }

        return stats;
    }

    public void updateStats(String videoId, ViewEvent event) {
        VideoStats stats = findById(videoId);

        if (stats == null) {
            // Créer une nouvelle statistique
            stats = new VideoStats();
            stats.setVideoId(videoId);
            stats.setTitle("Unknown Video");
            stats.setCategory("unknown");
            stats.setTotalViews(0);
            stats.setAvgDuration(0);
        }

        // Mettre à jour les stats
        if ("WATCH".equals(event.getAction())) {
            double currentAvg = stats.getAvgDuration();
            long currentViews = stats.getTotalViews();

            // Nouvelle moyenne pondérée
            double newAvg = (currentAvg * currentViews + event.getDuration()) / (currentViews + 1);

            stats.setTotalViews(currentViews + 1);
            stats.setAvgDuration(newAvg);
        }

        stats.setLastUpdated(LocalDateTime.now());
        saveOrUpdate(stats);
    }

    public List<VideoStats> findByCategory(String category) {
        List<VideoStats> stats = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("category", category))
                .sort(new Document("totalViews", -1))) {
            stats.add(documentToStats(doc));
        }
        return stats;
    }

    private VideoStats documentToStats(Document doc) {
        if (doc == null)
            return null;

        VideoStats stats = new VideoStats();
        stats.setVideoId(doc.getString("videoId"));
        stats.setTitle(doc.getString("title"));
        stats.setCategory(doc.getString("category"));

        // Handle both Integer and Long from MongoDB
        Number totalViews = (Number) doc.get("totalViews");
        if (totalViews != null) {
            stats.setTotalViews(totalViews.longValue());
        }

        // Handle both Integer and Double from MongoDB
        Number avgDuration = (Number) doc.get("avgDuration");
        if (avgDuration != null) {
            stats.setAvgDuration(avgDuration.doubleValue());
        }

        Date lastUpdated = doc.getDate("lastUpdated");
        if (lastUpdated != null) {
            stats.setLastUpdated(lastUpdated.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        }

        return stats;
    }
}