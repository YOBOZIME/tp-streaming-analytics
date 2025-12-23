package org.example.tpstreaminganalytics.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.bson.Document;
import org.example.tpstreaminganalytics.entity.Video;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repository for Video entity
 * Manages video metadata in MongoDB
 */
@ApplicationScoped
public class VideoRepository {

    @Inject
    MongoDatabase mongoDatabase;

    private MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection("videos");
    }

    public void save(Video video) {
        Document doc = videoToDocument(video);

        getCollection().replaceOne(
                Filters.eq("videoId", video.getVideoId()),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true));
    }

    public Video findById(String videoId) {
        Document doc = getCollection().find(Filters.eq("videoId", videoId)).first();
        return documentToVideo(doc);
    }

    public List<Video> findByCategory(String category) {
        List<Video> videos = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("category", category))) {
            videos.add(documentToVideo(doc));
        }
        return videos;
    }

    public List<Video> findAll() {
        List<Video> videos = new ArrayList<>();
        for (Document doc : getCollection().find()) {
            videos.add(documentToVideo(doc));
        }
        return videos;
    }

    public List<Video> findAll(int limit) {
        List<Video> videos = new ArrayList<>();
        for (Document doc : getCollection().find().limit(limit)) {
            videos.add(documentToVideo(doc));
        }
        return videos;
    }

    public long countAll() {
        return getCollection().countDocuments();
    }

    public void delete(String videoId) {
        getCollection().deleteOne(Filters.eq("videoId", videoId));
    }

    // Convert Video to MongoDB Document
    private Document videoToDocument(Video video) {
        Document doc = new Document()
                .append("videoId", video.getVideoId())
                .append("title", video.getTitle())
                .append("description", video.getDescription())
                .append("category", video.getCategory())
                .append("channel", video.getChannel())
                .append("uploaderId", video.getUploaderId())
                .append("duration", video.getDuration())
                .append("tags", video.getTags())
                .append("thumbnailUrl", video.getThumbnailUrl());

        if (video.getUploadDate() != null) {
            doc.append("uploadDate", Date.from(video.getUploadDate().toInstant(ZoneOffset.UTC)));
        }

        return doc;
    }

    // Convert MongoDB Document to Video
    private Video documentToVideo(Document doc) {
        if (doc == null)
            return null;

        Video video = new Video();
        video.setVideoId(doc.getString("videoId"));
        video.setTitle(doc.getString("title"));
        video.setDescription(doc.getString("description"));
        video.setCategory(doc.getString("category"));
        video.setChannel(doc.getString("channel"));
        video.setUploaderId(doc.getString("uploaderId"));
        video.setThumbnailUrl(doc.getString("thumbnailUrl"));

        // Handle duration (could be Integer or Long)
        Number duration = (Number) doc.get("duration");
        if (duration != null) {
            video.setDuration(duration.intValue());
        }

        // Handle upload date
        Date uploadDate = doc.getDate("uploadDate");
        if (uploadDate != null) {
            video.setUploadDate(uploadDate.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        }

        // Handle tags
        List<String> tags = (List<String>) doc.get("tags");
        if (tags != null) {
            video.setTags(tags);
        }

        return video;
    }
}
