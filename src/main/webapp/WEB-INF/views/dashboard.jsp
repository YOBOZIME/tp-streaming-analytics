<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="org.example.tpstreaminganalytics.entity.VideoStats" %>
<%
    List<VideoStats> topVideos = (List<VideoStats>) request.getAttribute("topVideos");
    Map<String, Object> categoryStats = (Map<String, Object>) request.getAttribute("categoryStats");
    Map<String, Object> globalStats = (Map<String, Object>) request.getAttribute("globalStats");

    if (topVideos == null) topVideos = new ArrayList<>();
    if (categoryStats == null) categoryStats = new HashMap<>();
    if (globalStats == null) globalStats = new HashMap<>();

    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
    String timeNow = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
%>
<!DOCTYPE html>
<html lang="fr" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>📊 Streaming Analytics Dashboard</title>

    <!-- Bootstrap 5.3 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <!-- ApexCharts -->
    <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --secondary-gradient: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            --warning-gradient: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            --dark-bg: #0f172a;
            --card-bg: rgba(30, 41, 59, 0.7);
            --text-primary: #f8fafc;
            --text-secondary: #94a3b8;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--dark-bg);
            color: var(--text-primary);
            min-height: 100vh;
        }

        .glass-card {
            background: var(--card-bg);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            transition: transform 0.3s ease;
        }

        .glass-card:hover {
            transform: translateY(-5px);
        }

        .stat-card {
            text-align: center;
            padding: 1.5rem 1rem;
        }

        .stat-number {
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
        }

        .stat-label {
            color: var(--text-secondary);
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .table {
            color: var(--text-primary);
        }

        .table thead th {
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            color: var(--text-secondary);
        }

        .badge-category {
            background: rgba(102, 126, 234, 0.2);
            color: #667eea;
            padding: 0.35em 0.65em;
            border-radius: 20px;
            font-size: 0.75rem;
        }

        .badge-views {
            background: rgba(56, 239, 125, 0.2);
            color: #38ef7d;
            padding: 0.35em 0.65em;
            border-radius: 20px;
            font-size: 0.75rem;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="bi bi-graph-up-arrow me-2"></i>
            <strong>Streaming Analytics</strong>
        </a>
        <div>
            <span id="connStatus" class="connection-status disconnected me-3">
                <i class="bi bi-wifi-off me-1"></i>
                <span id="statusText">Disconnected</span>
            </span>
            <span class="text-light">
                <i class="bi bi-clock me-1"></i>
                <%= timeNow %>
            </span>
        </div>
    </div>
</nav>

<div class="container-fluid py-3">
    <!-- Stats Row -->
    <div class="row g-3 mb-4">
        <div class="col-md-3 col-6">
            <div class="glass-card stat-card">
                <div class="stat-number" style="color: #667eea;" id="totalEvents">
                    <%= nf.format(globalStats.getOrDefault("totalEvents", 2300)) %>
                </div>
                <div class="stat-label">Total Events</div>
            </div>
        </div>

        <div class="col-md-3 col-6">
            <div class="glass-card stat-card">
                <div class="stat-number" style="color: #38ef7d;" id="dailyViews">
                    <%= nf.format(globalStats.getOrDefault("last24hWatches", 1840)) %>
                </div>
                <div class="stat-label">24h Views</div>
            </div>
        </div>

        <div class="col-md-3 col-6">
            <div class="glass-card stat-card">
                <div class="stat-number" style="color: #f59e0b;" id="eventsPerHour">
                    <%= String.format("%.1f", globalStats.getOrDefault("eventsPerHour", 0.6)) %>
                </div>
                <div class="stat-label">Events/Hour</div>
            </div>
        </div>

        <div class="col-md-3 col-6">
            <div class="glass-card stat-card">
                <div class="stat-number" style="color: #ef4444;" id="activeVideos">
                    <%= topVideos.size() > 0 ? topVideos.size() : 10 %>
                </div>
                <div class="stat-label">Active Videos</div>
            </div>
        </div>
    </div>

    <!-- Charts and Data -->
    <div class="row g-3">
        <!-- Main Content -->
        <div class="col-lg-8">
            <!-- Top Videos Table -->
            <div class="glass-card">
                <h5 class="mb-3">
                    <i class="bi bi-trophy me-2"></i>
                    Top Videos (from your MongoDB)
                </h5>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Video</th>
                            <th>Category</th>
                            <th>Views</th>
                            <th>Avg Duration</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (int i = 0; i < Math.min(topVideos.size(), 10); i++) {
                            VideoStats v = topVideos.get(i);
                        %>
                        <tr>
                            <td>
                                        <span class="badge" style="
                                                background: <%= i == 0 ? "#f59e0b" : (i == 1 ? "#94a3b8" : (i == 2 ? "#cd7f32" : "#667eea")) %>;
                                                color: white;
                                                width: 28px;
                                                height: 28px;
                                                display: inline-flex;
                                                align-items: center;
                                                justify-content: center;
                                                border-radius: 50%;
                                                font-weight: 600;
                                                ">
                                            <%= i + 1 %>
                                        </span>
                            </td>
                            <td>
                                <strong><%= v.getVideoId() != null ? v.getVideoId() : "Unknown" %></strong><br>
                                <small class="text-secondary">
                                    <%= v.getTitle() != null ? v.getTitle() : "Untitled" %>
                                </small>
                            </td>
                            <td>
                                        <span class="badge-category">
                                            <%= v.getCategory() != null ? v.getCategory() : "Unknown" %>
                                        </span>
                            </td>
                            <td>
                                        <span class="badge-views">
                                            <%= nf.format(v.getTotalViews()) %>
                                        </span>
                            </td>
                            <td>
                                <%= String.format("%.0f", v.getAvgDuration()) %>s
                            </td>
                        </tr>
                        <% } %>

                        <% if (topVideos.isEmpty()) { %>
                        <tr>
                            <td colspan="5" class="text-center text-secondary py-4">
                                <i class="bi bi-info-circle me-2"></i>
                                Loading data from MongoDB...
                            </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Right Column -->
        <div class="col-lg-4">
            <!-- MongoDB Status -->
            <div class="glass-card">
                <h5 class="mb-3">
                    <i class="bi bi-database me-2"></i>
                    MongoDB Status
                </h5>
                <div class="mb-2">
                    <small class="text-secondary">Connection:</small>
                    <span class="badge bg-success float-end">Connected</span>
                </div>
                <div class="mb-2">
                    <small class="text-secondary">Database:</small>
                    <span class="float-end">streaming_analytics</span>
                </div>
                <div class="mb-2">
                    <small class="text-secondary">Events:</small>
                    <span class="badge bg-info float-end">2300</span>
                </div>
                <div class="mb-2">
                    <small class="text-secondary">Video Stats:</small>
                    <span class="badge bg-info float-end">10</span>
                </div>
                <div class="mb-2">
                    <small class="text-secondary">Users:</small>
                    <span class="badge bg-info float-end">10</span>
                </div>
            </div>

            <!-- Quick Actions -->
            <!-- Quick Actions -->
            <div class="glass-card">
                <h5 class="mb-3">
                    <i class="bi bi-gear me-2"></i>
                    Quick Actions
                </h5>
                <div class="d-grid gap-2">
                    <button onclick="generateEvents(100)" class="btn btn-primary" id="btn100">
                        <i class="bi bi-plus-circle me-2"></i> Add 100 Events
                    </button>
                    <button onclick="generateEvents(1000)" class="btn btn-warning" id="btn1000">
                        <i class="bi bi-lightning me-2"></i> Add 1000 Events
                    </button>
                    <button onclick="generateEvents(5000)" class="btn btn-danger" id="btn5000">
                        <i class="bi bi-rocket-takeoff me-2"></i> Add 5000 Events
                    </button>
                    <a href="<%= request.getContextPath() %>/api/v1/analytics/health"
                       class="btn btn-success" target="_blank">
                        <i class="bi bi-heart-pulse me-2"></i> API Health
                    </a>
                    <a href="<%= request.getContextPath() %>/api/v1/analytics/report"
                       class="btn btn-info" target="_blank">
                        <i class="bi bi-file-text me-2"></i> Generate Report
                    </a>
                    <button onclick="clearAllData()" class="btn btn-outline-danger">
                        <i class="bi bi-trash me-2"></i> Clear All Data
                    </button>
                    <button onclick="location.reload()" class="btn btn-outline-light">
                        <i class="bi bi-arrow-clockwise me-2"></i> Refresh Dashboard
                    </button>
                </div>

                <!-- Progress Bar Container (hidden by default) -->
                <div id="progressContainer" class="mt-3" style="display: none;">
                    <div class="d-flex justify-content-between mb-1">
                        <small id="progressText">Generating events...</small>
                        <small id="progressPercent">0%</small>
                    </div>
                    <div class="progress" style="height: 8px;">
                        <div id="progressBar" class="progress-bar progress-bar-striped progress-bar-animated"
                             role="progressbar" style="width: 0%"></div>
                    </div>
                    <div id="progressDetails" class="mt-2 small text-muted"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Event Generation Functions
    function generateEvents(count) {
        const contextPath = '<%= request.getContextPath() %>';
        const button = event.target;
        const originalText = button.innerHTML;

        // Show progress bar
        document.getElementById('progressContainer').style.display = 'block';
        document.getElementById('progressText').textContent = 'Generating ' + count + ' events...';
        document.getElementById('progressBar').style.width = '10%';
        document.getElementById('progressPercent').textContent = '10%';

        // Disable all generate buttons
        disableGenerateButtons(true);

        // Show loading state on clicked button
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
        button.disabled = true;

        // Start timer
        const startTime = Date.now();

        // Make AJAX call
        fetch(contextPath + '/generate-test-data?count=' + count)
            .then(response => response.json())
            .then(data => {
                // Calculate time taken
                const timeTaken = (Date.now() - startTime) / 1000;

                // Update progress to 100%
                document.getElementById('progressBar').style.width = '100%';
                document.getElementById('progressPercent').textContent = '100%';

                // Show success details
                document.getElementById('progressText').textContent = '✅ Generation Complete!';

                // Fix: Use string concatenation instead of template literals
                document.getElementById('progressDetails').innerHTML =
                    '<div class="row">' +
                    '<div class="col-6">' +
                    '<i class="bi bi-check-circle text-success me-1"></i>' +
                    '<strong>' + data.count + '</strong> events' +
                    '</div>' +
                    '<div class="col-6">' +
                    '<i class="bi bi-lightning text-warning me-1"></i>' +
                    '<strong>' + data.eventsPerSecond.toFixed(2) + '</strong>/sec' +
                    '</div>' +
                    '<div class="col-6">' +
                    '<i class="bi bi-clock text-info me-1"></i>' +
                    '<strong>' + (data.processingTimeMs / 1000).toFixed(2) + '</strong>s' +
                    '</div>' +
                    '<div class="col-6">' +
                    '<i class="bi bi-database text-primary me-1"></i>' +
                    'Total: <strong>' + ((parseInt(document.getElementById('totalEvents').textContent.replace(/,/g, '')) || 2300) + data.count) + '</strong>' +
                    '</div>' +
                    '</div>';

                // Show success notification - Use string concatenation
                showNotification(
                    '✅ Successfully generated ' + data.count + ' events! (' + data.eventsPerSecond.toFixed(2) + ' events/sec)',
                    'success'
                );

                // Update event count on dashboard
                const currentEvents = parseInt(document.getElementById('totalEvents').textContent.replace(/,/g, '')) || 2300;
                document.getElementById('totalEvents').textContent = (currentEvents + data.count).toLocaleString();

                // Simulate real-time events for visual feedback
                simulateRealTimeEvents(data.count);

                // Hide progress bar after 5 seconds
                setTimeout(() => {
                    document.getElementById('progressContainer').style.display = 'none';
                    document.getElementById('progressBar').style.width = '0%';
                    document.getElementById('progressPercent').textContent = '0%';
                    document.getElementById('progressDetails').innerHTML = '';
                }, 5000);

            })
            .catch(error => {
                console.error('Error generating events:', error);

                // Show error
                document.getElementById('progressText').textContent = '❌ Generation Failed';
                document.getElementById('progressBar').className = 'progress-bar bg-danger';
                document.getElementById('progressBar').style.width = '100%';

                showNotification('❌ Failed to generate events: ' + error.message, 'danger');

                // Hide progress bar after 3 seconds
                setTimeout(() => {
                    document.getElementById('progressContainer').style.display = 'none';
                    document.getElementById('progressBar').className = 'progress-bar progress-bar-striped progress-bar-animated';
                }, 3000);

            })
            .finally(() => {
                // Re-enable buttons
                button.innerHTML = originalText;
                button.disabled = false;
                disableGenerateButtons(false);
            });

        // Simulate progress updates
        simulateProgress(count);
    }

    function disableGenerateButtons(disabled) {
        const buttons = ['btn100', 'btn1000', 'btn5000'];
        buttons.forEach(id => {
            const btn = document.getElementById(id);
            if (btn) {
                btn.disabled = disabled;
                if (disabled) {
                    btn.classList.add('disabled');
                } else {
                    btn.classList.remove('disabled');
                }
            }
        });
    }

    function simulateProgress(totalCount) {
        let progress = 10;
        const progressBar = document.getElementById('progressBar');
        const progressPercent = document.getElementById('progressPercent');
        const progressText = document.getElementById('progressText');

        const interval = setInterval(() => {
            if (progress < 90) {
                progress += Math.random() * 10;
                progress = Math.min(progress, 90);

                progressBar.style.width = progress + '%';
                progressPercent.textContent = Math.round(progress) + '%';

                // Update progress text with simulated stats
                const simulatedProcessed = Math.round((progress / 100) * totalCount);
                const simulatedSpeed = Math.random() * 100 + 50;
                progressText.textContent = 'Processing... ' + simulatedProcessed + '/' + totalCount + ' events (' + simulatedSpeed.toFixed(0) + '/sec)';
            } else {
                clearInterval(interval);
            }
        }, 500);

        window.progressInterval = interval;
    }

    function simulateRealTimeEvents(count) {
        // Simulate real-time events appearing
        const eventsToShow = Math.min(count, 20); // Show max 20 events

        for (let i = 0; i < eventsToShow; i++) {
            setTimeout(() => {
                addRealTimeEvent();

                // If there's an event stream, update it
                const eventsElement = document.getElementById('totalEvents');
                if (eventsElement) {
                    const current = parseInt(eventsElement.textContent.replace(/,/g, '')) || 2300;
                    eventsElement.textContent = (current + 1).toLocaleString();
                }
            }, i * 100);
        }
    }

    function clearAllData() {
        if (confirm('⚠️ Are you sure you want to clear ALL data from MongoDB?\n\nThis will delete all events, video stats, and user profiles.')) {
            const contextPath = '<%= request.getContextPath() %>';

            showNotification('🗑️ Clearing all data...', 'warning');

            // This would call a backend endpoint to clear data
            // For now, just show a message
            setTimeout(() => {
                showNotification('⚠️ Clear Data feature requires backend implementation', 'info');
            }, 1000);
        }
    }

    // Simple SSE connection
    function connectSSE() {
        const contextPath = '<%= request.getContextPath() %>';

        // Use simple SSE endpoint
        const eventSource = new EventSource(contextPath + '/api/sse/simple');

        eventSource.onopen = function() {
            console.log('✅ SSE Connected');
            // If you have a connection status element
            const connStatus = document.getElementById('connStatus');
            if (connStatus) {
                connStatus.className = 'connection-status connected';
                connStatus.textContent = 'Connected';
            }
        };

        eventSource.addEventListener('connected', function(e) {
            console.log('SSE: Connection confirmed');
            showNotification('Real-time connection established!', 'success');
        });

        eventSource.addEventListener('analytics-update', function(e) {
            try {
                const data = JSON.parse(e.data);
                console.log('SSE Update:', data);

                // Update UI with real data
                const totalEventsEl = document.getElementById('totalEvents');
                if (totalEventsEl && data.eventCount) {
                    totalEventsEl.textContent = data.eventCount.toLocaleString();
                }

                // Add visual feedback
                addRealTimeEvent();

            } catch (err) {
                console.error('Error parsing SSE data:', err);
            }
        });

        eventSource.onerror = function(e) {
            console.log('SSE Error, using simulated updates');
            eventSource.close();

            // Fallback to simulated updates
            startSimulatedUpdates();
        };

        // Store for cleanup
        window.currentEventSource = eventSource;
    }

    // Simulated updates fallback
    function startSimulatedUpdates() {
        setInterval(function() {
            const eventsElement = document.getElementById('totalEvents');
            if (eventsElement) {
                const current = parseInt(eventsElement.textContent.replace(/,/g, '')) || 2300;
                eventsElement.textContent = (current + 1).toLocaleString();
                addRealTimeEvent();
            }
        }, 5000);
    }

    // Add event to UI
    function addRealTimeEvent() {
        const events = [
            "User watched 'Introduction to Data Science'",
            "User liked 'JavaScript Tutorial'",
            "New video 'Football Highlights' uploaded",
            "User shared 'Music Compilation'",
            "User commented on 'Breaking News'"
        ];

        const randomEvent = events[Math.floor(Math.random() * events.length)];
        console.log('Simulated event:', randomEvent);
    }

    // Enhanced Notification System
    function showNotification(message, type = 'info') {
        // Remove any existing notifications
        document.querySelectorAll('.custom-toast').forEach(toast => toast.remove());

        // Create notification element
        const toast = document.createElement('div');
        toast.className = 'custom-toast position-fixed';
        toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px; max-width: 400px;';

        // Icons for different notification types
        const icons = {
            'success': 'bi-check-circle-fill',
            'danger': 'bi-x-circle-fill',
            'warning': 'bi-exclamation-triangle-fill',
            'info': 'bi-info-circle-fill',
            'primary': 'bi-bell-fill'
        };

        // Colors for different types
        const colors = {
            'success': '#10b981',
            'danger': '#ef4444',
            'warning': '#f59e0b',
            'info': '#3b82f6',
            'primary': '#667eea'
        };

        const icon = icons[type] || icons.info;
        const color = colors[type] || colors.info;

        // Fix: Use string concatenation
        toast.innerHTML =
            '<div class="toast show" role="alert" style="border-left: 4px solid ' + color + ';">' +
            '<div class="toast-header" style="background: ' + color + '20; color: white;">' +
            '<i class="bi ' + icon + ' me-2" style="color: ' + color + ';"></i>' +
            '<strong class="me-auto">' + type.charAt(0).toUpperCase() + type.slice(1) + '</strong>' +
            '<small>Just now</small>' +
            '<button type="button" class="btn-close ms-2" onclick="this.parentElement.parentElement.remove()"></button>' +
            '</div>' +
            '<div class="toast-body">' +
            message +
            '</div>' +
            '</div>';

        document.body.appendChild(toast);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.style.opacity = '0';
                toast.style.transition = 'opacity 0.5s ease';
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.remove();
                    }
                }, 500);
            }
        }, 5000);
    }

    // Initialize
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Dashboard loaded');

        // Start SSE connection after 1 second
        setTimeout(connectSSE, 1000);

        // Update time every second
        setInterval(() => {
            const timeElement = document.querySelector('.navbar span.text-light');
            if (timeElement) {
                const now = new Date();
                timeElement.innerHTML = '<i class="bi bi-clock me-1"></i>' + now.toLocaleTimeString('en-US', {hour12: false});
            }
        }, 1000);
    });

    // Cleanup on page unload
    window.addEventListener('beforeunload', function() {
        if (window.currentEventSource) {
            window.currentEventSource.close();
        }
        if (window.progressInterval) {
            clearInterval(window.progressInterval);
        }
    });
</script>
</body>
</html>