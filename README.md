# 📊 Streaming Analytics Platform

Plateforme d'analyse Big Data pour un service de streaming vidéo simulé, développée avec Jakarta EE sur Tomcat 9.

## 🚀 Fonctionnalités

- **Ingestion de données** - API REST pour événements en temps réel et batch
- **Statistiques vidéos** - Top vidéos, durée moyenne, vues par catégorie
- **Recommandations** - Système de recommandation personnalisé
- **Dashboard temps réel** - Interface avec SSE (Server-Sent Events)
- **Analytics** - Détection de tendances et pics d'activité

## 📋 Prérequis

- Java 17+
- Apache Tomcat 9.x
- Docker & Docker Compose
- Maven 3.x

## 🛠️ Installation

### 1. Démarrer MongoDB

```bash
docker-compose up -d
```

Vérifie que MongoDB est accessible sur `localhost:27017`

### 2. Compiler le projet

```bash
# Windows
.\mvnw.cmd clean package

# Linux/Mac
./mvnw clean package
```

### 3. Déployer sur Tomcat

Copier `target/tpstreaming.war` dans le dossier `webapps/` de Tomcat.

Ou déployer via IntelliJ/Eclipse vers Tomcat.

## 🌐 URLs

| Ressource | URL |
|-----------|-----|
| Dashboard | http://localhost:8080/tpstreaming/dashboard |
| API Health | http://localhost:8080/tpstreaming/api/v1/analytics/health |
| Mongo Express | http://localhost:8081 |

## 📡 API REST Endpoints

Base URL: `/api/v1/analytics`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/health` | Health check |
| POST | `/events` | Ingérer un événement |
| POST | `/events/batch` | Ingérer un lot d'événements |
| GET | `/videos/top?limit=10` | Top vidéos par vues |
| GET | `/videos/{id}/stats` | Stats d'une vidéo |
| GET | `/users/{id}/recommendations` | Recommandations |
| GET | `/stats/global` | Statistiques globales |
| GET | `/stats/categories` | Stats par catégorie |
| GET | `/trending` | Vidéos tendance |
| GET | `/report` | Rapport complet |
| GET | `/realtime/stream` | Flux SSE temps réel |

## 📝 Exemples API

### Ingérer un événement

```bash
curl -X POST http://localhost:8080/tpstreaming/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user_001",
    "videoId": "video_001",
    "action": "WATCH",
    "duration": 120,
    "quality": "1080p",
    "deviceType": "mobile"
  }'
```

### Générer des données test

```bash
# 100 événements
curl http://localhost:8080/tpstreaming/generate-test-data?count=100

# 1000 événements
curl http://localhost:8080/tpstreaming/generate-test-data?count=1000
```

### Top vidéos

```bash
curl http://localhost:8080/tpstreaming/api/v1/analytics/videos/top?limit=5
```

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  [Data Generator] → [REST API] → [Event Processor]             │
│                                          ↓                      │
│                                    [MongoDB]                    │
│                                          ↓                      │
│                                    [Dashboard JSP]              │
│                                          ↓                      │
│                                    [SSE Stream]                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 📁 Structure du Projet

```
tp-streaming-analytics/
├── src/main/java/org/example/tpstreaminganalytics/
│   ├── api/                    # REST Resources & Servlets
│   │   ├── AnalyticsResource.java
│   │   ├── SSEResource.java
│   │   └── DataGeneratorServlet.java
│   ├── config/                 # Configuration
│   │   ├── MongoDBConfig.java
│   │   └── JacksonConfig.java
│   ├── entity/                 # Entités
│   │   ├── ViewEvent.java
│   │   ├── VideoStats.java
│   │   ├── UserProfile.java
│   │   └── Video.java
│   ├── repository/             # Repositories (CDI)
│   │   ├── EventRepository.java
│   │   ├── VideoStatsRepository.java
│   │   ├── UserProfileRepository.java
│   │   └── VideoRepository.java
│   ├── service/                # Services
│   │   ├── EventProcessorService.java
│   │   └── AnalyticsService.java
│   └── servlet/                # MVC Servlets
│       └── DashboardServlet.java
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── views/dashboard.jsp
│   │   ├── beans.xml
│   │   └── web.xml
│   └── index.jsp
├── docker-compose.yml
├── mongo-init.js
└── pom.xml
```

## 🔧 Technologies

- **Backend**: Java 17, JAX-RS (Jersey 2.41), CDI (Weld 3.x)
- **Database**: MongoDB 7.x
- **Frontend**: JSP, Bootstrap 5, Chart.js
- **Server**: Apache Tomcat 9.x
- **Build**: Maven

## 👤 Auteur

TP Pratique - Plateforme d'Analyse Big Data avec JEE/Jakarta EE
