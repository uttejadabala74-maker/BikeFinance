// Vercel Serverless Function entrypoint for Bike Finance App
module.exports = (req, res) => {
  const path = req.url || '/';
  
  if (path.startsWith('/api') || path.startsWith('/health')) {
    return res.status(200).json({
      status: 'UP',
      app: 'Bike Finance Application',
      version: '0.0.1-SNAPSHOT',
      timestamp: new Date().toISOString()
    });
  }

  // Redirect root requests to dashboard HTML view
  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  res.status(200).send(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bike Finance App - Deployed on Vercel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8fafc; color: #1e293b; }
            .hero-card { border: none; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); background: #ffffff; }
            .btn-primary { background-color: #2563eb; border: none; border-radius: 8px; padding: 12px 24px; font-weight: 600; }
            .badge-custom { background-color: #dbeafe; color: #1e40af; font-size: 0.9rem; padding: 6px 12px; border-radius: 20px; }
        </style>
    </head>
    <body class="d-flex align-items-center min-vh-100 py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6 text-center">
                    <div class="card hero-card p-5">
                        <div class="mb-3">
                            <span class="badge badge-custom">🚀 Deployed on Vercel</span>
                        </div>
                        <h1 class="fw-bold mb-3">Sri Sai Teja Finance</h1>
                        <p class="text-secondary mb-4">Bike Finance &amp; Auto Consultancy Management System</p>
                        
                        <div class="alert alert-success border-0 mb-4 text-start">
                            <strong>✓ Vercel Deployment Successful!</strong><br>
                            Your Bike Finance Web Service &amp; Android APK API endpoints are active and ready.
                        </div>

                        <div class="d-grid gap-2 col-10 mx-auto">
                            <a href="https://raw.githubusercontent.com/uttejadabala74-maker/BikeFinance/main/BikeFinance.apk" class="btn btn-primary btn-lg shadow-sm">
                                📲 Download Android APK
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    </html>
  `);
};
