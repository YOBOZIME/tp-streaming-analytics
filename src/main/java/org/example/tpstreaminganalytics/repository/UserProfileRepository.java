package org.example.tpstreaminganalytics.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.bson.Document;
import org.example.tpstreaminganalytics.entity.UserProfile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ApplicationScoped
public class UserProfileRepository {

    @Inject
    MongoDatabase mongoDatabase;

    private MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection("user_profiles");
    }

    public void save(UserProfile profile) {
        Document doc = userProfileToDocument(profile);

        getCollection().replaceOne(
                Filters.eq("userId", profile.getUserId()),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true));
    }

    public Optional<UserProfile> findById(String userId) {
        Document doc = getCollection().find(Filters.eq("userId", userId)).first();
        return Optional.ofNullable(documentToUserProfile(doc));
    }

    public List<UserProfile> findAll() {
        List<UserProfile> profiles = new ArrayList<>();
        for (Document doc : getCollection().find()) {
            profiles.add(documentToUserProfile(doc));
        }
        return profiles;
    }

    public void updateRecommendations(String userId, List<String> videoIds) {
        Optional<UserProfile> profileOpt = findById(userId);
        profileOpt.ifPresent(profile -> {
            profile.setRecommendedVideoIds(videoIds);
            save(profile);
        });
    }

    public void delete(String userId) {
        getCollection().deleteOne(Filters.eq("userId", userId));
    }

    // Helper method: Convert UserProfile to MongoDB Document
    private Document userProfileToDocument(UserProfile profile) {
        Document doc = new Document()
                .append("userId", profile.getUserId())
                .append("username", profile.getUsername())
                .append("email", profile.getEmail())
                .append("preferredCategory", profile.getPreferredCategory())
                .append("preferredQuality", profile.getPreferredQuality());

        // Handle dates
        if (profile.getRegistrationDate() != null) {
            doc.append("registrationDate",
                    Date.from(profile.getRegistrationDate().toInstant(ZoneOffset.UTC)));
        }

        if (profile.getLastWatchTime() != null) {
            doc.append("lastWatchTime",
                    Date.from(profile.getLastWatchTime().toInstant(ZoneOffset.UTC)));
        }

        // Handle watch history
        List<Document> watchHistoryDocs = new ArrayList<>();
        for (UserProfile.WatchHistoryItem item : profile.getWatchHistory()) {
            Document historyDoc = new Document()
                    .append("videoId", item.getVideoId())
                    .append("watchedAt",
                            item.getWatchedAt() != null ? Date.from(item.getWatchedAt().toInstant(ZoneOffset.UTC))
                                    : null)
                    .append("watchDuration", item.getWatchDuration());
            watchHistoryDocs.add(historyDoc);
        }
        doc.append("watchHistory", watchHistoryDocs);

        // Handle category preferences (Map<String, Integer>)
        Document preferencesDoc = new Document();
        for (Map.Entry<String, Integer> entry : profile.getCategoryPreferences().entrySet()) {
            preferencesDoc.append(entry.getKey(), entry.getValue());
        }
        doc.append("categoryPreferences", preferencesDoc);

        // Handle recommended video IDs
        doc.append("recommendedVideoIds", profile.getRecommendedVideoIds())
                .append("totalWatchTime", profile.getTotalWatchTime())
                .append("lastVideoWatched", profile.getLastVideoWatched());

        return doc;
    }

    // Helper method: Convert Document to UserProfile (CORRIGÉ)
    private UserProfile documentToUserProfile(Document doc) {
        if (doc == null)
            return null;

        UserProfile profile = new UserProfile();
        profile.setUserId(doc.getString("userId"));
        profile.setUsername(doc.getString("username"));
        profile.setEmail(doc.getString("email"));
        profile.setPreferredCategory(doc.getString("preferredCategory"));
        profile.setPreferredQuality(doc.getString("preferredQuality"));
        profile.setLastVideoWatched(doc.getString("lastVideoWatched"));

        // Handle both Integer and Long from MongoDB
        Number totalWatchTime = (Number) doc.get("totalWatchTime");
        if (totalWatchTime != null) {
            profile.setTotalWatchTime(totalWatchTime.longValue());
        }

        // Handle dates
        Date registrationDate = doc.getDate("registrationDate");
        if (registrationDate != null) {
            profile.setRegistrationDate(
                    registrationDate.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        }

        Date lastWatchTime = doc.getDate("lastWatchTime");
        if (lastWatchTime != null) {
            profile.setLastWatchTime(
                    lastWatchTime.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
        }

        // Handle watch history - CORRECTION ICI
        List<Document> watchHistoryDocs = (List<Document>) doc.get("watchHistory");
        if (watchHistoryDocs != null) {
            List<UserProfile.WatchHistoryItem> watchHistory = new ArrayList<>();
            for (Document historyDoc : watchHistoryDocs) {
                UserProfile.WatchHistoryItem item = new UserProfile.WatchHistoryItem();
                item.setVideoId(historyDoc.getString("videoId"));
                Date watchedAt = historyDoc.getDate("watchedAt");
                if (watchedAt != null) {
                    item.setWatchedAt(watchedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime());
                }
                Integer duration = historyDoc.getInteger("watchDuration");
                if (duration != null) {
                    item.setWatchDuration(duration);
                }
                watchHistory.add(item);
            }
            profile.setWatchHistory(watchHistory);
        }

        // Handle category preferences - CORRECTION CRITIQUE ICI
        Document preferencesDoc = (Document) doc.get("categoryPreferences");
        if (preferencesDoc != null) {
            Map<String, Integer> preferences = new HashMap<>();
            for (Map.Entry<String, Object> entry : preferencesDoc.entrySet()) {
                if (entry.getValue() instanceof Integer) {
                    preferences.put(entry.getKey(), (Integer) entry.getValue());
                } else if (entry.getValue() instanceof Number) {
                    preferences.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                }
            }
            profile.setCategoryPreferences(preferences);
        }

        // Handle recommended video IDs

        return profile;
    }

    // Méthodes utilitaires supplémentaires
    public List<UserProfile> findByPreferredCategory(String category) {
        List<UserProfile> profiles = new ArrayList<>();
        for (Document doc : getCollection().find(Filters.eq("preferredCategory", category))) {
            profiles.add(documentToUserProfile(doc));
        }
        return profiles;
    }

    public void updateWatchHistory(String userId, String videoId, int duration) {
        Optional<UserProfile> profileOpt = findById(userId);
        profileOpt.ifPresent(profile -> {
            profile.addToWatchHistory(videoId, duration);
            save(profile);
        });
    }

    public void updateCategoryPreference(String userId, String category, int points) {
        Optional<UserProfile> profileOpt = findById(userId);
        profileOpt.ifPresent(profile -> {
            profile.updateCategoryPreference(category, points);
            save(profile);
        });
    }
}