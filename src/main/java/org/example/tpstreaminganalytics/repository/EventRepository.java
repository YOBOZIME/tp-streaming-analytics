package org.example.tpstreaminganalytics.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.tpstreaminganalytics.entity.ViewEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class EventRepository {

    @Inject
    MongoDatabase mongoDatabase;

    private MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection("view_events");
    }

    public void save(ViewEvent event) {
        Document doc = new Document()
                .append("eventId", event.getEventId())
                .append("userId", event.getUserId())
                .append("videoId", event.getVideoId())
                .append("timestamp", Date.from(event.getTimestamp().toInstant(ZoneOffset.UTC)))
                .append("action", event.getAction())
                .append("duration", event.getDuration())
                .append("quality", event.getQuality())
                .append("deviceType", event.getDeviceType());

        getCollection().insertOne(doc);
    }

    public void saveBatch(List<ViewEvent> events) {
        List<Document> documents = new ArrayList<>();
        for (ViewEvent event : events) {
            Document doc = new Document()
                    .append("eventId", event.getEventId())
                    .append("userId", event.getUserId())
                    .append("videoId", event.getVideoId())
                    .append("timestamp", Date.from(event.getTimestamp().toInstant(ZoneOffset.UTC)))
                    .append("action", event.getAction())
                    .append("duration", event.getDuration())
                    .append("quality", event.getQuality())
                    .append("deviceType", event.getDeviceType());
            documents.add(doc);
        }

        if (!documents.isEmpty()) {
            getCollection().insertMany(documents);
        }
    }

    public ViewEvent findById(String eventId) {
        Document doc = getCollection().find(Filters.eq("eventId", eventId)).first();
        return documentToEvent(doc);
    }

    public List<ViewEvent> findByUserId(String userId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("userId", userId))) {
            events.add(documentToEvent(doc));
        }
        return events;
    }

    public List<ViewEvent> findByVideoId(String videoId) {
        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("videoId", videoId))) {
            events.add(documentToEvent(doc));
        }
        return events;
    }

    public List<ViewEvent> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        Bson filter = Filters.and(
                Filters.gte("timestamp", Date.from(start.toInstant(ZoneOffset.UTC))),
                Filters.lte("timestamp", Date.from(end.toInstant(ZoneOffset.UTC)))
        );

        List<ViewEvent> events = new ArrayList<>();
        for (Document doc : getCollection().find(filter)) {
            events.add(documentToEvent(doc));
        }
        return events;
    }

    public long countAll() {
        return getCollection().countDocuments();
    }

    public void deleteAll() {
        getCollection().deleteMany(new Document());
    }

    private ViewEvent documentToEvent(Document doc) {
        if (doc == null) return null;

        ViewEvent event = new ViewEvent();
        event.setEventId(doc.getString("eventId"));
        event.setUserId(doc.getString("userId"));
        event.setVideoId(doc.getString("videoId"));

        Date timestamp = doc.getDate("timestamp");
        if (timestamp != null) {
            event.setTimestamp(timestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        }

        event.setAction(doc.getString("action"));
        event.setDuration(doc.getInteger("duration", 0));
        event.setQuality(doc.getString("quality"));
        event.setDeviceType(doc.getString("deviceType"));

        return event;
    }
}