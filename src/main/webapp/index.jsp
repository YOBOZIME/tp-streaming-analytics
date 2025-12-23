<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Streaming Analytics - TP Big Data</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
            padding: 20px;
        }

        .container { max-width: 1200px; margin: 0 auto; padding: 40px 20px; }

        header { text-align: center; margin-bottom: 50px; }
        h1 { font-size: clamp(2rem, 5vw, 3rem); margin-bottom: 20px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }
        .subtitle { font-size: 1.1em; opacity: 0.9; margin-bottom: 40px; }

        .dashboard {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 50px;
        }

        .card {
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 25px;
            border: 1px solid rgba(255,255,255,0.2);
            transition: transform 0.3s ease, background 0.3s ease;
        }

        .card:hover { transform: translateY(-5px); background: rgba(255,255,255,0.12); }

        .card h2 { margin-bottom: 20px; font-size: 1.4em; display: flex; align-items: center; gap: 10px; }

        .card ul { list-style: none; }
        .card li { margin: 12px 0; padding: 10px; background: rgba(255,255,255,0.05); border-radius: 8px; transition: background 0.3s ease; }
        .card li:hover { background: rgba(255,255,255,0.12); }

        .card a { color: white; text-decoration: none; display: block; }

        .status { background: rgba(76,175,80,0.15); border-left: 5px solid #4CAF50; }
        .warning { background: rgba(255,193,7,0.15); border-left: 5px solid #FFC107; }
        .info { background: rgba(33,150,243,0.15); border-left: 5px solid #2196F3; }

        footer { text-align: center; margin-top: 50px; padding-top: 30px; border-top: 1px solid rgba(255,255,255,0.2); opacity: 0.8; font-size: 0.9em; }

        .btn {
            display: inline-block;
            background: white;
            color: #667eea;
            padding: 12px 28px;
            border-radius: 30px;
            text-decoration: none;
            font-weight: bold;
            margin: 5px;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .btn:hover {
            transform: scale(1.05);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }

        #testResult p { padding: 10px; border-radius: 5px; margin-top: 10px; }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>📊 Streaming Analytics Platform</h1>
        <p class="subtitle">TP Pratique - Big Data avec Jakarta EE & MongoDB</p>
    </header>

    <div class="dashboard">
        <div class="card status">
            <h2>✅ Status</h2>
            <ul>
                <li><strong>Server:</strong> Apache Tomcat 9.0.109</li>
                <li><strong>Application:</strong> tpstreaming</li>
                <li><strong>Context:</strong> <span id="context"></span></li>
                <li><strong>Time:</strong> <span id="time"></span></li>
            </ul>
        </div>

        <div class="card info">
            <h2>🔗 Navigation</h2>
            <ul>
                <li><a href="/tpstreaming/dashboard" id="dashboardLink">📈 Dashboard</a></li>
                <li><a href="/tpstreaming/test" id="testLink">🧪 Test Page</a></li>
                <li><a href="/tpstreaming/api/health" id="apiLink">🔧 API Health Check</a></li>
                <li><a href="http://localhost:8081" target="_blank">🗄️ MongoDB Admin</a></li>
            </ul>
        </div>

        <div class="card warning">
            <h2>⚡ Quick Actions</h2>
            <a class="btn" onclick="testConnection()">Test Connection</a>
            <a class="btn" onclick="clearCache()">Clear Browser Cache</a>
            <div id="testResult"></div>
        </div>
    </div>

    <footer>
        <p>TP Streaming Analytics - © 2024</p>
        <p>Docker MongoDB | Tomcat 9 | Java 17</p>
    </footer>
</div>

<script>
    // Update context and time
    const context = window.location.pathname.split('/')[1] || 'tpstreaming';
    document.getElementById('context').textContent = context;
    function updateTime() {
        document.getElementById('time').textContent = new Date().toLocaleString();
    }
    setInterval(updateTime, 1000); // Refresh every second
    updateTime();

    // Update links dynamically
    document.getElementById('dashboardLink').href = '/' + context + '/dashboard';
    document.getElementById('testLink').href = '/' + context + '/test';
    document.getElementById('apiLink').href = '/' + context + '/api/health';

    function testConnection() {
        const resultDiv = document.getElementById('testResult');
        resultDiv.innerHTML = '<p style="color:white;">Testing connection...</p>';

        fetch('/' + context + '/api/health')
            .then(res => {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(data => {
                resultDiv.innerHTML =
                    `<p style="color:#4CAF50; background: rgba(255,255,255,0.1);">
                     ✅ Connection successful!<br>Status: ${data.status}<br>Service: ${data.service}</p>`;
            })
            .catch(err => {
                resultDiv.innerHTML =
                    `<p style="color:#f44336; background: rgba(255,255,255,0.1);">
                     ❌ Connection failed: ${err.message}<br>
                     Try accessing directly: <a href="/${context}/api/health" style="color:white;">/${context}/api/health</a>
                     </p>`;
            });
    }

    function clearCache() {
        if ('caches' in window) caches.keys().then(names => names.forEach(name => caches.delete(name)));
        localStorage.clear();
        sessionStorage.clear();
        document.getElementById('testResult').innerHTML =
            '<p style="color:#FFC107;">Cache cleared! Refresh the page.</p>';
    }

    // Auto-test on page load
    window.addEventListener('load', () => setTimeout(testConnection, 1000));
</script>
</body>
</html>
