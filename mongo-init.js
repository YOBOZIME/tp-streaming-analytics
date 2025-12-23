// Script d'initialisation MongoDB pour le TP Streaming Analytics
db = db.getSiblingDB('streaming_analytics');

print('🚀 Initialisation de la base de données Streaming Analytics...');

// Créer des collections (si elles n'existent pas)
const collections = ['view_events', 'video_stats', 'user_profiles', 'videos'];

collections.forEach(collectionName => {
    if (!db.getCollectionNames().includes(collectionName)) {
        db.createCollection(collectionName);
        print(`✅ Collection créée: ${collectionName}`);
    } else {
        print(`📁 Collection existante: ${collectionName}`);
    }
});

// Créer des indexes pour optimiser les performances
print('\n🔍 Création des indexes...');

db.view_events.createIndex({ eventId: 1 }, { unique: true });
db.view_events.createIndex({ userId: 1 });
db.view_events.createIndex({ videoId: 1 });
db.view_events.createIndex({ timestamp: -1 });
print('✅ Indexes créés pour view_events');

db.video_stats.createIndex({ videoId: 1 }, { unique: true });
db.video_stats.createIndex({ category: 1 });
db.video_stats.createIndex({ totalViews: -1 });
print('✅ Indexes créés pour video_stats');

db.user_profiles.createIndex({ userId: 1 }, { unique: true });
db.user_profiles.createIndex({ username: 1 });
db.user_profiles.createIndex({ preferredCategory: 1 });
print('✅ Indexes créés pour user_profiles');

db.videos.createIndex({ videoId: 1 }, { unique: true });
db.videos.createIndex({ category: 1 });
db.videos.createIndex({ uploadDate: -1 });
print('✅ Indexes créés pour videos');

// Insérer des vidéos de test
print('\n📹 Insertion de vidéos de test...');
const videosCount = db.videos.countDocuments();
if (videosCount === 0) {
    db.videos.insertMany([
        {
            videoId: 'video_001',
            title: 'Introduction to Java Programming',
            description: 'Learn the basics of Java programming language',
            category: 'education',
            duration: 1800,
            uploadDate: new Date('2024-01-15'),
            tags: ['java', 'programming', 'tutorial'],
            channel: 'Tech Academy',
            uploaderId: 'uploader_001'
        },
        {
            videoId: 'video_002',
            title: 'Top 10 JavaScript Tips 2024',
            description: 'Advanced JavaScript techniques and tips for modern web development',
            category: 'education',
            duration: 1200,
            uploadDate: new Date('2024-01-20'),
            tags: ['javascript', 'web', 'development', 'tips'],
            channel: 'Web Masters',
            uploaderId: 'uploader_002'
        },
        {
            videoId: 'video_003',
            title: 'Workout Music Mix 2024',
            description: 'Best workout music to keep you motivated during exercise',
            category: 'music',
            duration: 3600,
            uploadDate: new Date('2024-01-25'),
            tags: ['music', 'workout', 'fitness', '2024'],
            channel: 'Fitness Beats',
            uploaderId: 'uploader_003'
        },
        {
            videoId: 'video_004',
            title: 'Football Highlights - Champions League',
            description: 'Best moments from the latest Champions League matches',
            category: 'sports',
            duration: 2400,
            uploadDate: new Date('2024-02-01'),
            tags: ['football', 'sports', 'highlights', 'champions league'],
            channel: 'Sports World',
            uploaderId: 'uploader_004'
        },
        {
            videoId: 'video_005',
            title: 'Cooking Italian Pasta',
            description: 'Learn how to make authentic Italian pasta from scratch',
            category: 'entertainment',
            duration: 1500,
            uploadDate: new Date('2024-02-05'),
            tags: ['cooking', 'food', 'italian', 'pasta', 'recipe'],
            channel: 'Foodie Channel',
            uploaderId: 'uploader_005'
        }
    ]);
    print('✅ 5 vidéos de test insérées');
} else {
    print(`📊 ${videosCount} vidéos déjà existantes`);
}

// Insérer des statistiques de test
print('\n📊 Insertion de statistiques de test...');
const statsCount = db.video_stats.countDocuments();
if (statsCount === 0) {
    db.video_stats.insertMany([
        {
            videoId: 'video_001',
            title: 'Introduction to Java Programming',
            category: 'education',
            totalViews: 1500,
            avgDuration: 1250.5,
            lastUpdated: new Date()
        },
        {
            videoId: 'video_002',
            title: 'Top 10 JavaScript Tips 2024',
            category: 'education',
            totalViews: 2800,
            avgDuration: 980.2,
            lastUpdated: new Date()
        },
        {
            videoId: 'video_003',
            title: 'Workout Music Mix 2024',
            category: 'music',
            totalViews: 4500,
            avgDuration: 3200.8,
            lastUpdated: new Date()
        },
        {
            videoId: 'video_004',
            title: 'Football Highlights - Champions League',
            category: 'sports',
            totalViews: 6200,
            avgDuration: 2100.3,
            lastUpdated: new Date()
        },
        {
            videoId: 'video_005',
            title: 'Cooking Italian Pasta',
            category: 'entertainment',
            totalViews: 3400,
            avgDuration: 1400.7,
            lastUpdated: new Date()
        }
    ]);
    print('✅ 5 statistiques de test insérées');
} else {
    print(`📈 ${statsCount} statistiques déjà existantes`);
}

// Insérer des profils utilisateurs de test
print('\n👤 Insertion de profils utilisateurs de test...');
const usersCount = db.user_profiles.countDocuments();
if (usersCount === 0) {
    db.user_profiles.insertMany([
        {
            userId: 'user_001',
            username: 'john_doe',
            email: 'john@example.com',
            registrationDate: new Date('2024-01-01'),
            preferredCategory: 'education',
            preferredQuality: '1080p',
            totalWatchTime: 15600,
            lastVideoWatched: 'video_002',
            lastWatchTime: new Date(),
            watchHistory: [
                {
                    videoId: 'video_001',
                    watchedAt: new Date('2024-01-16'),
                    watchDuration: 1200
                },
                {
                    videoId: 'video_002',
                    watchedAt: new Date('2024-01-20'),
                    watchDuration: 950
                }
            ],
            categoryPreferences: {
                'education': 25,
                'technology': 15,
                'music': 5
            },
            recommendedVideoIds: ['video_003', 'video_005']
        },
        {
            userId: 'user_002',
            username: 'jane_smith',
            email: 'jane@example.com',
            registrationDate: new Date('2024-01-05'),
            preferredCategory: 'music',
            preferredQuality: '720p',
            totalWatchTime: 8900,
            lastVideoWatched: 'video_003',
            lastWatchTime: new Date(),
            watchHistory: [
                {
                    videoId: 'video_003',
                    watchedAt: new Date('2024-01-26'),
                    watchDuration: 2800
                }
            ],
            categoryPreferences: {
                'music': 30,
                'entertainment': 10
            },
            recommendedVideoIds: ['video_001', 'video_004']
        },
        {
            userId: 'user_003',
            username: 'bob_wilson',
            email: 'bob@example.com',
            registrationDate: new Date('2024-01-10'),
            preferredCategory: 'sports',
            preferredQuality: '1080p',
            totalWatchTime: 12400,
            lastVideoWatched: 'video_004',
            lastWatchTime: new Date(),
            watchHistory: [
                {
                    videoId: 'video_004',
                    watchedAt: new Date('2024-02-02'),
                    watchDuration: 1800
                },
                {
                    videoId: 'video_005',
                    watchedAt: new Date('2024-02-06'),
                    watchDuration: 1200
                }
            ],
            categoryPreferences: {
                'sports': 40,
                'entertainment': 15
            },
            recommendedVideoIds: ['video_002', 'video_003']
        }
    ]);
    print('✅ 3 profils utilisateurs de test insérés');
} else {
    print(`👥 ${usersCount} utilisateurs déjà existants`);
}

print('\n🎉 Initialisation MongoDB terminée avec succès!');
print('=============================================');
print(`📁 Base de données: streaming_analytics`);
print(`📊 Collections: ${db.getCollectionNames().join(', ')}`);
print(`👤 Utilisateurs: ${db.user_profiles.countDocuments()}`);
print(`📹 Vidéos: ${db.videos.countDocuments()}`);
print(`📈 Statistiques: ${db.video_stats.countDocuments()}`);
print(`🎬 Événements: ${db.view_events.countDocuments()}`);
print('=============================================\n');