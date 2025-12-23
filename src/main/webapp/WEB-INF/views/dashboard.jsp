<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.*" %>
        <%@ page import="org.example.tpstreaminganalytics.entity.VideoStats" %>
            <% List<VideoStats> topVideos = (List<VideoStats>) request.getAttribute("topVideos");
                    Map<String, Object> categoryStats = (Map<String, Object>) request.getAttribute("categoryStats");
                            Map<String, Object> globalStats = (Map<String, Object>) request.getAttribute("globalStats");
                                    List<Map<String, Object>> trendingVideos = (List<Map<String, Object>>)
                                            request.getAttribute("trendingVideos");
                                            List<Map<String, Object>> activityPeaks = (List<Map<String, Object>>)
                                                    request.getAttribute("activityPeaks");
                                                    if (topVideos == null) topVideos = new ArrayList<>();
                                                        if (categoryStats == null) categoryStats = new HashMap<>();
                                                            if (globalStats == null) globalStats = new HashMap<>();
                                                                if (trendingVideos == null) trendingVideos = new
                                                                ArrayList<>();
                                                                    if (activityPeaks == null) activityPeaks = new
                                                                    ArrayList<>();
                                                                        java.text.NumberFormat nf =
                                                                        java.text.NumberFormat.getInstance();
                                                                        String timeNow = new
                                                                        java.text.SimpleDateFormat("HH:mm:ss").format(new
                                                                        java.util.Date());
                                                                        %>
                                                                        <!DOCTYPE html>
                                                                        <html lang="fr">

                                                                        <head>
                                                                            <meta charset="UTF-8">
                                                                            <meta name="viewport"
                                                                                content="width=device-width, initial-scale=1.0">
                                                                            <title>Streaming Analytics Dashboard</title>
                                                                            <link
                                                                                href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
                                                                                rel="stylesheet">
                                                                            <link
                                                                                href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
                                                                                rel="stylesheet">
                                                                            <link
                                                                                href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
                                                                                rel="stylesheet">
                                                                            <script
                                                                                src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                                                                            <style>
                                                                                :root {
                                                                                    --dark-bg: #0f0f23;
                                                                                    --card-bg: rgba(255, 255, 255, 0.05);
                                                                                    --card-border: rgba(255, 255, 255, 0.1);
                                                                                }

                                                                                * {
                                                                                    margin: 0;
                                                                                    padding: 0;
                                                                                    box-sizing: border-box;
                                                                                }

                                                                                body {
                                                                                    font-family: 'Inter', sans-serif;
                                                                                    background: var(--dark-bg);
                                                                                    min-height: 100vh;
                                                                                    color: #fff;
                                                                                }

                                                                                .navbar {
                                                                                    background: rgba(15, 15, 35, 0.9) !important;
                                                                                    border-bottom: 1px solid var(--card-border);
                                                                                }

                                                                                .navbar-brand {
                                                                                    font-weight: 700;
                                                                                    color: #667eea !important;
                                                                                }

                                                                                .glass-card {
                                                                                    background: var(--card-bg);
                                                                                    border: 1px solid var(--card-border);
                                                                                    border-radius: 16px;
                                                                                    padding: 1.25rem;
                                                                                    margin-bottom: 1.25rem;
                                                                                }

                                                                                .stat-card {
                                                                                    text-align: center;
                                                                                    padding: 1.5rem 1rem;
                                                                                    border-top: 3px solid #667eea;
                                                                                }

                                                                                .stat-card.g2 {
                                                                                    border-color: #38ef7d;
                                                                                }

                                                                                .stat-card.g3 {
                                                                                    border-color: #f2994a;
                                                                                }

                                                                                .stat-card.g4 {
                                                                                    border-color: #4facfe;
                                                                                }

                                                                                .stat-number {
                                                                                    font-size: 2rem;
                                                                                    font-weight: 700;
                                                                                    color: #667eea;
                                                                                }

                                                                                .stat-card.g2 .stat-number {
                                                                                    color: #38ef7d;
                                                                                }

                                                                                .stat-card.g3 .stat-number {
                                                                                    color: #f2994a;
                                                                                }

                                                                                .stat-card.g4 .stat-number {
                                                                                    color: #4facfe;
                                                                                }

                                                                                .stat-label {
                                                                                    color: rgba(255, 255, 255, 0.6);
                                                                                    font-size: 0.8rem;
                                                                                    text-transform: uppercase;
                                                                                    margin-top: 0.25rem;
                                                                                }

                                                                                .section-header {
                                                                                    font-weight: 600;
                                                                                    margin-bottom: 1rem;
                                                                                    padding-bottom: 0.5rem;
                                                                                    border-bottom: 1px solid var(--card-border);
                                                                                    display: flex;
                                                                                    align-items: center;
                                                                                    gap: 0.5rem;
                                                                                }

                                                                                .section-header i {
                                                                                    color: #667eea;
                                                                                }

                                                                                .table {
                                                                                    color: #fff;
                                                                                }

                                                                                .table thead th {
                                                                                    border-bottom: 1px solid var(--card-border);
                                                                                    color: rgba(255, 255, 255, 0.6);
                                                                                    font-size: 0.8rem;
                                                                                }

                                                                                .table tbody td {
                                                                                    border-bottom: 1px solid var(--card-border);
                                                                                    vertical-align: middle;
                                                                                }

                                                                                .rank-badge {
                                                                                    width: 28px;
                                                                                    height: 28px;
                                                                                    display: inline-flex;
                                                                                    align-items: center;
                                                                                    justify-content: center;
                                                                                    background: #667eea;
                                                                                    color: #fff;
                                                                                    border-radius: 50%;
                                                                                    font-weight: 600;
                                                                                    font-size: 0.8rem;
                                                                                }

                                                                                .rank-badge.gold {
                                                                                    background: #f7971e;
                                                                                }

                                                                                .rank-badge.silver {
                                                                                    background: #bdc3c7;
                                                                                }

                                                                                .rank-badge.bronze {
                                                                                    background: #cd7f32;
                                                                                }

                                                                                .badge-cat {
                                                                                    background: rgba(102, 126, 234, 0.2);
                                                                                    color: #667eea;
                                                                                    padding: 0.25rem 0.5rem;
                                                                                    border-radius: 12px;
                                                                                    font-size: 0.75rem;
                                                                                }

                                                                                .badge-views {
                                                                                    background: rgba(56, 239, 125, 0.2);
                                                                                    color: #38ef7d;
                                                                                    padding: 0.25rem 0.5rem;
                                                                                    border-radius: 12px;
                                                                                    font-size: 0.8rem;
                                                                                }

                                                                                .trending-item {
                                                                                    display: flex;
                                                                                    justify-content: space-between;
                                                                                    align-items: center;
                                                                                    padding: 0.75rem;
                                                                                    margin-bottom: 0.5rem;
                                                                                    background: rgba(255, 255, 255, 0.02);
                                                                                    border-radius: 10px;
                                                                                }

                                                                                .trending-growth {
                                                                                    background: linear-gradient(135deg, #f093fb, #f5576c);
                                                                                    color: #fff;
                                                                                    padding: 0.2rem 0.5rem;
                                                                                    border-radius: 10px;
                                                                                    font-size: 0.75rem;
                                                                                    font-weight: 600;
                                                                                }

                                                                                .chart-container {
                                                                                    position: relative;
                                                                                    height: 200px;
                                                                                    width: 100%;
                                                                                }

                                                                                .action-btn {
                                                                                    padding: 0.6rem 1.2rem;
                                                                                    border-radius: 10px;
                                                                                    font-weight: 500;
                                                                                    border: none;
                                                                                    text-decoration: none;
                                                                                    display: inline-flex;
                                                                                    align-items: center;
                                                                                    gap: 0.4rem;
                                                                                    transition: all 0.2s;
                                                                                }

                                                                                .action-btn.primary {
                                                                                    background: linear-gradient(135deg, #667eea, #764ba2);
                                                                                    color: #fff;
                                                                                }

                                                                                .action-btn.warning {
                                                                                    background: linear-gradient(135deg, #f2994a, #f2c94c);
                                                                                    color: #fff;
                                                                                }

                                                                                .action-btn.success {
                                                                                    background: linear-gradient(135deg, #11998e, #38ef7d);
                                                                                    color: #fff;
                                                                                }

                                                                                .action-btn.secondary {
                                                                                    background: rgba(255, 255, 255, 0.1);
                                                                                    color: #fff;
                                                                                    border: 1px solid var(--card-border);
                                                                                }

                                                                                .action-btn:hover {
                                                                                    transform: translateY(-2px);
                                                                                    color: #fff;
                                                                                }

                                                                                .connection-status {
                                                                                    display: inline-flex;
                                                                                    align-items: center;
                                                                                    gap: 0.4rem;
                                                                                    padding: 0.3rem 0.6rem;
                                                                                    border-radius: 15px;
                                                                                    font-size: 0.75rem;
                                                                                    background: rgba(255, 255, 255, 0.1);
                                                                                    color: rgba(255, 255, 255, 0.7);
                                                                                }

                                                                                .connection-status.connected {
                                                                                    background: rgba(56, 239, 125, 0.2);
                                                                                    color: #38ef7d;
                                                                                }

                                                                                .event-stream {
                                                                                    max-height: 200px;
                                                                                    overflow-y: auto;
                                                                                }

                                                                                .event-item {
                                                                                    display: flex;
                                                                                    align-items: center;
                                                                                    gap: 0.5rem;
                                                                                    padding: 0.5rem;
                                                                                    margin-bottom: 0.4rem;
                                                                                    background: rgba(0, 0, 0, 0.2);
                                                                                    border-radius: 8px;
                                                                                    font-size: 0.8rem;
                                                                                }
                                                                            </style>
                                                                        </head>

                                                                        <body>
                                                                            <nav class="navbar navbar-dark py-2">
                                                                                <div class="container-fluid px-4">
                                                                                    <a class="navbar-brand" href="#"><i
                                                                                            class="bi bi-graph-up-arrow me-2"></i>Streaming
                                                                                        Analytics</a>
                                                                                    <div
                                                                                        class="d-flex align-items-center gap-3">
                                                                                        <span class="connection-status"
                                                                                            id="connStatus"><span
                                                                                                id="statusText">Hors
                                                                                                ligne</span></span>
                                                                                        <span
                                                                                            class="text-secondary small">
                                                                                            <%= timeNow %>
                                                                                        </span>
                                                                                    </div>
                                                                                </div>
                                                                            </nav>

                                                                            <div class="container-fluid px-4 py-3">
                                                                                <!-- Stats Row -->
                                                                                <div class="row g-3 mb-3">
                                                                                    <div class="col-6 col-md-3">
                                                                                        <div
                                                                                            class="glass-card stat-card">
                                                                                            <div class="stat-number">
                                                                                                <%= nf.format(globalStats.getOrDefault("totalEvents",
                                                                                                    0)) %>
                                                                                            </div>
                                                                                            <div class="stat-label">
                                                                                                Événements</div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="col-6 col-md-3">
                                                                                        <div
                                                                                            class="glass-card stat-card g2">
                                                                                            <div class="stat-number">
                                                                                                <%= nf.format(globalStats.getOrDefault("last24hWatches",
                                                                                                    0)) %>
                                                                                            </div>
                                                                                            <div class="stat-label">Vues
                                                                                                24h</div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="col-6 col-md-3">
                                                                                        <div
                                                                                            class="glass-card stat-card g3">
                                                                                            <div class="stat-number">
                                                                                                <%= String.format("%.1f",
                                                                                                    globalStats.getOrDefault("eventsPerHour",
                                                                                                    0.0)) %>
                                                                                            </div>
                                                                                            <div class="stat-label">
                                                                                                Events/h</div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="col-6 col-md-3">
                                                                                        <div
                                                                                            class="glass-card stat-card g4">
                                                                                            <div class="stat-number">
                                                                                                <%= topVideos.size() %>
                                                                                            </div>
                                                                                            <div class="stat-label">
                                                                                                Vidéos</div>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>

                                                                                <div class="row g-3">
                                                                                    <!-- Top Videos -->
                                                                                    <div class="col-lg-8">
                                                                                        <div class="glass-card">
                                                                                            <div class="section-header">
                                                                                                <i
                                                                                                    class="bi bi-trophy"></i>
                                                                                                Top Vidéos</div>
                                                                                            <div
                                                                                                class="table-responsive">
                                                                                                <table
                                                                                                    class="table table-sm mb-0">
                                                                                                    <thead>
                                                                                                        <tr>
                                                                                                            <th>#</th>
                                                                                                            <th>Video
                                                                                                            </th>
                                                                                                            <th>Catégorie
                                                                                                            </th>
                                                                                                            <th>Vues
                                                                                                            </th>
                                                                                                            <th>Durée
                                                                                                            </th>
                                                                                                        </tr>
                                                                                                    </thead>
                                                                                                    <tbody>
                                                                                                        <% for (int i=0;
                                                                                                            i <
                                                                                                            Math.min(topVideos.size(),
                                                                                                            10); i++) {
                                                                                                            VideoStats
                                                                                                            v=topVideos.get(i);
                                                                                                            String
                                                                                                            rc=i==0
                                                                                                            ? "gold" :
                                                                                                            (i==1
                                                                                                            ? "silver" :
                                                                                                            (i==2
                                                                                                            ? "bronze"
                                                                                                            : "" )); %>
                                                                                                            <tr>
                                                                                                                <td><span
                                                                                                                        class="rank-badge <%= rc %>">
                                                                                                                        <%= i+1
                                                                                                                            %>
                                                                                                                    </span>
                                                                                                                </td>
                                                                                                                <td><strong>
                                                                                                                        <%= v.getVideoId()
                                                                                                                            %>
                                                                                                                    </strong><br><small
                                                                                                                        class="text-secondary">
                                                                                                                        <%= v.getTitle()
                                                                                                                            !=null
                                                                                                                            ?
                                                                                                                            v.getTitle()
                                                                                                                            : "-"
                                                                                                                            %>
                                                                                                                    </small>
                                                                                                                </td>
                                                                                                                <td><span
                                                                                                                        class="badge-cat">
                                                                                                                        <%= v.getCategory()
                                                                                                                            !=null
                                                                                                                            ?
                                                                                                                            v.getCategory()
                                                                                                                            : "?"
                                                                                                                            %>
                                                                                                                    </span>
                                                                                                                </td>
                                                                                                                <td><span
                                                                                                                        class="badge-views">
                                                                                                                        <%= nf.format(v.getTotalViews())
                                                                                                                            %>
                                                                                                                    </span>
                                                                                                                </td>
                                                                                                                <td>
                                                                                                                    <%= String.format("%.0f",
                                                                                                                        v.getAvgDuration())
                                                                                                                        %>
                                                                                                                        s
                                                                                                                </td>
                                                                                                            </tr>
                                                                                                            <% } %>
                                                                                                                <% if
                                                                                                                    (topVideos.isEmpty())
                                                                                                                    { %>
                                                                                                                    <tr>
                                                                                                                        <td colspan="5"
                                                                                                                            class="text-center text-secondary py-3">
                                                                                                                            Aucune
                                                                                                                            donnée
                                                                                                                        </td>
                                                                                                                    </tr>
                                                                                                                    <% }
                                                                                                                        %>
                                                                                                    </tbody>
                                                                                                </table>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>

                                                                                    <!-- Chart + Trending -->
                                                                                    <div class="col-lg-4">
                                                                                        <div class="glass-card">
                                                                                            <div class="section-header">
                                                                                                <i
                                                                                                    class="bi bi-pie-chart"></i>
                                                                                                Par Catégorie</div>
                                                                                            <div
                                                                                                class="chart-container">
                                                                                                <canvas
                                                                                                    id="catChart"></canvas>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="glass-card">
                                                                                            <div class="section-header">
                                                                                                <i
                                                                                                    class="bi bi-fire"></i>
                                                                                                Tendances</div>
                                                                                            <% for (int i=0; i <
                                                                                                Math.min(trendingVideos.size(),
                                                                                                3); i++) {
                                                                                                Map<String,Object> t =
                                                                                                trendingVideos.get(i);
                                                                                                %>
                                                                                                <div
                                                                                                    class="trending-item">
                                                                                                    <div><span
                                                                                                            class="rank-badge">
                                                                                                            <%= i+1 %>
                                                                                                        </span> <strong
                                                                                                            class="ms-2">
                                                                                                            <%= t.get("title")
                                                                                                                %>
                                                                                                        </strong></div>
                                                                                                    <span
                                                                                                        class="trending-growth"><i
                                                                                                            class="bi bi-arrow-up"></i>
                                                                                                        <%= t.get("growth")
                                                                                                            %>
                                                                                                    </span>
                                                                                                </div>
                                                                                                <% } %>
                                                                                                    <% if
                                                                                                        (trendingVideos.isEmpty())
                                                                                                        { %>
                                                                                                        <div
                                                                                                            class="text-center text-secondary py-2 small">
                                                                                                            Pas de
                                                                                                            tendances
                                                                                                        </div>
                                                                                                        <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>

                                                                                <!-- Real-time + Actions -->
                                                                                <div class="row g-3 mt-1">
                                                                                    <div class="col-lg-6">
                                                                                        <div class="glass-card">
                                                                                            <div class="section-header">
                                                                                                <i
                                                                                                    class="bi bi-broadcast"></i>
                                                                                                Temps Réel <button
                                                                                                    class="action-btn secondary ms-auto btn-sm"
                                                                                                    id="sseBtn"
                                                                                                    onclick="toggleSSE()"><i
                                                                                                        class="bi bi-wifi"></i>
                                                                                                    Activer</button>
                                                                                            </div>
                                                                                            <div class="event-stream"
                                                                                                id="eventStream">
                                                                                                <div
                                                                                                    class="text-center text-secondary py-3 small">
                                                                                                    Cliquez Activer pour
                                                                                                    le flux SSE</div>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="col-lg-6">
                                                                                        <div class="glass-card">
                                                                                            <div class="section-header">
                                                                                                <i
                                                                                                    class="bi bi-tools"></i>
                                                                                                Actions</div>
                                                                                            <div
                                                                                                class="d-flex flex-wrap gap-2">
                                                                                                <a href="<%= request.getContextPath() %>/generate-test-data?count=100"
                                                                                                    class="action-btn primary"><i
                                                                                                        class="bi bi-plus"></i>
                                                                                                    100 Events</a>
                                                                                                <a href="<%= request.getContextPath() %>/generate-test-data?count=1000"
                                                                                                    class="action-btn warning"><i
                                                                                                        class="bi bi-lightning"></i>
                                                                                                    1000 Events</a>
                                                                                                <a href="<%= request.getContextPath() %>/api/v1/analytics/report"
                                                                                                    class="action-btn success"
                                                                                                    target="_blank"><i
                                                                                                        class="bi bi-file-text"></i>
                                                                                                    Rapport</a>
                                                                                                <button
                                                                                                    onclick="location.reload()"
                                                                                                    class="action-btn secondary"><i
                                                                                                        class="bi bi-arrow-clockwise"></i>
                                                                                                    Refresh</button>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>
                                                                            </div>

                                                                            <script>
                                                                                var catData = { labels: [], datasets: [{ data: [], backgroundColor: ['#667eea', '#38ef7d', '#f093fb', '#4facfe', '#f5576c', '#f2994a'], borderWidth: 0 }] };
<% for (Map.Entry < String, Object > e : categoryStats.entrySet()) {
                                                                                    Map < String, Object > s = (Map < String, Object >) e.getValue(); %>
                                                                                        catData.labels.push("<%= e.getKey() %>");
                                                                                    catData.datasets[0].data.push(<%= s.get("totalViews") %>);
<% } %>
                                                                                    new Chart(document.getElementById('catChart'), {
                                                                                        type: 'doughnut',
                                                                                        data: catData,
                                                                                        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { color: '#fff', font: { size: 10 } } } } }
                                                                                    });

                                                                                var es = null, active = false, ctx = '<%= request.getContextPath() %>';
                                                                                function toggleSSE() {
                                                                                    if (active) { if (es) es.close(); document.getElementById('connStatus').className = 'connection-status'; document.getElementById('statusText').textContent = 'Hors ligne'; document.getElementById('sseBtn').innerHTML = '<i class="bi bi-wifi"></i> Activer'; active = false; }
                                                                                    else {
                                                                                        es = new EventSource(ctx + '/api/v1/analytics/realtime/stream');
                                                                                        es.onopen = function () { document.getElementById('connStatus').className = 'connection-status connected'; document.getElementById('statusText').textContent = 'En direct'; document.getElementById('sseBtn').innerHTML = '<i class="bi bi-stop"></i> Stop'; document.getElementById('eventStream').innerHTML = ''; active = true; };
                                                                                        es.addEventListener('analytics-update', function (e) { var d = JSON.parse(e.data); addEvent('Update: ' + (d.topVideos ? d.topVideos.length : 0) + ' videos'); });
                                                                                        es.onerror = function () { toggleSSE(); };
                                                                                    }
                                                                                }
                                                                                function addEvent(msg) {
                                                                                    var s = document.getElementById('eventStream');
                                                                                    var t = new Date().toLocaleTimeString();
                                                                                    s.insertAdjacentHTML('afterbegin', '<div class="event-item"><i class="bi bi-arrow-right text-info"></i><span class="flex-grow-1">' + msg + '</span><small class="text-secondary">' + t + '</small></div>');
                                                                                    while (s.children.length > 15) s.removeChild(s.lastChild);
                                                                                }
                                                                            </script>
                                                                        </body>

                                                                        </html>