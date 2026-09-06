// Vercel Serverless Function - Sri Sai Teja Finance Dashboard
module.exports = (req, res) => {
  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  res.status(200).send(`<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Executive Dashboard - Sri Sai Teja Finance &amp; Auto Consultancy</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-body: #f8fafc;
            --bg-sidebar: #0f172a;
            --bg-sidebar-hover: #1e293b;
            --bg-card: #ffffff;
            --text-primary: #0f172a;
            --text-secondary: #64748b;
            --text-muted: #94a3b8;
            --brand-primary: #2563eb;
            --brand-primary-hover: #1d4ed8;
            --brand-accent: #10b981;
            --border-color: #e2e8f0;
            --success: #10b981;
            --warning: #f59e0b;
            --danger: #ef4444;
            --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
            --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1);
            --radius-md: 10px;
            --radius-lg: 16px;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Plus Jakarta Sans', sans-serif; }
        body { background-color: var(--bg-body); color: var(--text-primary); min-height: 100vh; }
        .app-container { display: flex; min-height: 100vh; width: 100%; }
        
        .sidebar { width: 260px; background-color: var(--bg-sidebar); color: #ffffff; display: flex; flex-direction: column; position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; transition: transform 0.3s ease; }
        .sidebar-header { padding: 24px 20px; border-bottom: 1px solid rgba(255, 255, 255, 0.1); }
        .sidebar-brand-title { font-size: 16px; font-weight: 800; color: #ffffff; display: flex; align-items: center; gap: 10px; }
        .sidebar-brand-subtitle { font-size: 11px; color: var(--text-muted); margin-top: 4px; font-weight: 500; text-transform: uppercase; letter-spacing: 0.5px; }
        .sidebar-nav { padding: 16px 12px; flex-grow: 1; }
        .nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; color: #94a3b8; text-decoration: none; border-radius: var(--radius-md); font-size: 14px; font-weight: 600; margin-bottom: 4px; transition: all 0.2s ease; }
        .nav-item:hover { background-color: var(--bg-sidebar-hover); color: #ffffff; }
        .nav-item.active { background-color: var(--brand-primary); color: #ffffff; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3); }

        .main-wrapper { margin-left: 260px; flex-grow: 1; display: flex; flex-direction: column; min-width: 0; }
        .top-header { height: 70px; background-color: var(--bg-card); border-bottom: 1px solid var(--border-color); display: flex; align-items: center; justify-content: space-between; padding: 0 30px; position: sticky; top: 0; z-index: 90; }
        .mobile-toggle { display: none; background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-primary); }
        .search-bar { display: flex; align-items: center; background-color: #f1f5f9; border: 1px solid var(--border-color); border-radius: 24px; padding: 8px 16px; width: 380px; gap: 10px; }
        .search-bar input { border: none; background: transparent; outline: none; width: 100%; font-size: 14px; color: var(--text-primary); }
        .business-badge { display: flex; align-items: center; gap: 10px; background-color: #f0fdf4; border: 1px solid #bbf7d0; padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 700; color: #166534; }

        .content-area { padding: 30px; flex-grow: 1; }
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
        .page-title { font-size: 24px; font-weight: 800; color: var(--text-primary); letter-spacing: -0.5px; }
        .page-subtitle { font-size: 14px; color: var(--text-secondary); margin-top: 4px; }

        .grid-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background-color: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--shadow-sm); display: flex; flex-direction: column; justify-content: space-between; transition: transform 0.2s ease; }
        .stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
        .stat-header { display: flex; justify-content: space-between; align-items: center; color: var(--text-secondary); font-size: 13px; font-weight: 600; margin-bottom: 12px; }
        .stat-icon { width: 38px; height: 38px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
        .icon-blue { background: #dbeafe; color: #2563eb; }
        .icon-green { background: #d1fae5; color: #10b981; }
        .icon-amber { background: #fef3c7; color: #d97706; }
        .icon-red { background: #fee2e2; color: #ef4444; }
        .icon-purple { background: #f3e8ff; color: #9333ea; }
        .stat-value { font-size: 26px; font-weight: 800; color: var(--text-primary); }
        .stat-footer { font-size: 12px; color: var(--text-muted); margin-top: 10px; font-weight: 500; }

        .card { background-color: var(--bg-card); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 24px; box-shadow: var(--shadow-sm); margin-bottom: 24px; }
        .card-title { font-size: 18px; font-weight: 700; color: var(--text-primary); margin-bottom: 16px; display: flex; align-items: center; justify-content: space-between; }
        
        .table-responsive { width: 100%; overflow-x: auto; border-radius: var(--radius-md); border: 1px solid var(--border-color); }
        .custom-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 14px; }
        .custom-table th { background-color: #f8fafc; color: var(--text-secondary); font-weight: 700; padding: 14px 16px; border-bottom: 1px solid var(--border-color); font-size: 11px; text-transform: uppercase; }
        .custom-table td { padding: 14px 16px; border-bottom: 1px solid var(--border-color); color: var(--text-primary); }

        .btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 10px 20px; border-radius: var(--radius-md); font-size: 14px; font-weight: 700; cursor: pointer; border: 1px solid transparent; text-decoration: none; }
        .btn-primary { background-color: var(--brand-primary); color: #ffffff; }
        .btn-success { background-color: var(--brand-accent); color: #ffffff; }
        .btn-outline { background-color: transparent; border-color: var(--border-color); color: var(--text-primary); }
        .badge { display: inline-flex; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 700; }
        .badge-success { background: #d1fae5; color: #065f46; }
        .badge-info { background: #dbeafe; color: #1e40af; }

        @media (max-width: 992px) {
            .sidebar { transform: translateX(-100%); }
            .sidebar.open { transform: translateX(0); }
            .main-wrapper { margin-left: 0; }
            .mobile-toggle { display: block; }
        }
    </style>
</head>
<body>

<div class="app-container">
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <div class="sidebar-brand-title">🏍️ Sri Sai Teja</div>
            <div class="sidebar-brand-subtitle">Finance &amp; Auto Consultancy</div>
        </div>
        <nav class="sidebar-nav">
            <a href="/" class="nav-item active"><span class="nav-icon">📊</span> Executive Dashboard</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">🛵</span> Bikes Register</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">👥</span> Customers</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">💰</span> Finance Accounts</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">📅</span> EMI Schedule</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">💵</span> Payments</a>
            <a href="#" onclick="alert('Module Active')" class="nav-item"><span class="nav-icon">📜</span> Traffic Challans</a>
            <a href="https://raw.githubusercontent.com/uttejadabala74-maker/BikeFinance/main/BikeFinance.apk" class="nav-item" style="color: #34d399;"><span class="nav-icon">📲</span> Download APK App</a>
        </nav>
    </aside>

    <div class="main-wrapper">
        <header class="top-header">
            <button class="mobile-toggle" onclick="document.getElementById('sidebar').classList.toggle('open')">☰</button>
            <div class="search-bar">
                <span>🔍</span>
                <input type="text" placeholder="Search reg no, customer, phone, loan #...">
            </div>
            <div class="header-actions">
                <a href="https://raw.githubusercontent.com/uttejadabala74-maker/BikeFinance/main/BikeFinance.apk" class="btn btn-success" style="padding: 6px 14px; font-size: 13px;">📲 APK App</a>
                <div class="business-badge">
                    <span>🏢</span> Sri Sai Teja Finance
                </div>
            </div>
        </header>

        <main class="content-area">
            <div class="page-header">
                <div>
                    <h1 class="page-title">Executive Dashboard</h1>
                    <p class="page-subtitle">Real-time business performance &amp; collection overview</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="https://raw.githubusercontent.com/uttejadabala74-maker/BikeFinance/main/BikeFinance.apk" class="btn btn-primary">📲 Get Mobile APK App</a>
                </div>
            </div>

            <!-- 10 Metric Cards -->
            <div class="grid-cards">
                <div class="stat-card">
                    <div class="stat-header">Total Bikes <span class="stat-icon icon-blue">🛵</span></div>
                    <div class="stat-value">24</div>
                    <div class="stat-footer">Registered vehicles</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Available Bikes <span class="stat-icon icon-green">✅</span></div>
                    <div class="stat-value">8</div>
                    <div class="stat-footer">Ready for sale</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Financed Bikes <span class="stat-icon icon-purple">📄</span></div>
                    <div class="stat-value">16</div>
                    <div class="stat-footer">Active loans</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Total Customers <span class="stat-icon icon-blue">👥</span></div>
                    <div class="stat-value">18</div>
                    <div class="stat-footer">Registered clients</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Active Loans <span class="stat-icon icon-blue">💼</span></div>
                    <div class="stat-value">16</div>
                    <div class="stat-footer">Ongoing finance accounts</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Pending EMIs <span class="stat-icon icon-amber">⏳</span></div>
                    <div class="stat-value">124</div>
                    <div class="stat-footer">Awaiting payment</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Overdue EMIs <span class="stat-icon icon-red">⚠️</span></div>
                    <div class="stat-value" style="color: var(--danger);">3</div>
                    <div class="stat-footer">Action required</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Total Collected <span class="stat-icon icon-green">💰</span></div>
                    <div class="stat-value" style="color: var(--success);">₹ 3,48,500.00</div>
                    <div class="stat-footer">All-time received</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Total Outstanding <span class="stat-icon icon-red">📉</span></div>
                    <div class="stat-value" style="color: var(--brand-primary);">₹ 8,92,400.00</div>
                    <div class="stat-footer">Pending collections</div>
                </div>
                <div class="stat-card">
                    <div class="stat-header">Total Challans <span class="stat-icon icon-amber">📜</span></div>
                    <div class="stat-value">5</div>
                    <div class="stat-footer">Traffic offences logged</div>
                </div>
            </div>

            <!-- Two Column Tables -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 24px;">
                <div class="card">
                    <div class="card-title">
                        <span>💵 Recent Collections</span>
                        <span class="badge badge-success">Live</span>
                    </div>
                    <div class="table-responsive">
                        <table class="custom-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Customer</th>
                                    <th>Amount</th>
                                    <th>Mode</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>2026-09-06</td>
                                    <td>K. Ramesh Naidu</td>
                                    <td style="font-weight: 700; color: var(--success);">₹ 8,500.00</td>
                                    <td><span class="badge badge-info">UPI / PhonePe</span></td>
                                </tr>
                                <tr>
                                    <td>2026-09-05</td>
                                    <td>M. Sathya Krishna</td>
                                    <td style="font-weight: 700; color: var(--success);">₹ 9,200.00</td>
                                    <td><span class="badge badge-info">Cash</span></td>
                                </tr>
                                <tr>
                                    <td>2026-09-04</td>
                                    <td>P. Venkatesh</td>
                                    <td style="font-weight: 700; color: var(--success);">₹ 7,800.00</td>
                                    <td><span class="badge badge-info">Bank Transfer</span></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="card">
                    <div class="card-title">
                        <span>📅 Upcoming Due Dates (Next 30 Days)</span>
                        <span class="badge badge-info">Schedule</span>
                    </div>
                    <div class="table-responsive">
                        <table class="custom-table">
                            <thead>
                                <tr>
                                    <th>Due Date</th>
                                    <th>Customer</th>
                                    <th>Bike Model</th>
                                    <th>Amount</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td style="font-weight: 700;">2026-09-10</td>
                                    <td>B. Srinivas Rao</td>
                                    <td>Bajaj Pulsar 150</td>
                                    <td>₹ 8,900.00</td>
                                </tr>
                                <tr>
                                    <td style="font-weight: 700;">2026-09-12</td>
                                    <td>G. Ananda Kumar</td>
                                    <td>TVS Apache RTR</td>
                                    <td>₹ 9,500.00</td>
                                </tr>
                                <tr>
                                    <td style="font-weight: 700;">2026-09-15</td>
                                    <td>V. Ravi Teja</td>
                                    <td>Hero Splendor Plus</td>
                                    <td>₹ 6,400.00</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

</body>
</html>`);
};
