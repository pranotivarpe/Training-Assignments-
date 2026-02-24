const http = require('http');
const db = require('./db');

const PORT = 3000;

// Helper: parse JSON body from request
function parseBody(req) {
    return new Promise((resolve, reject) => {
        let body = '';
        req.on('data', (chunk) => (body += chunk));
        req.on('end', () => {
            try {
                resolve(JSON.parse(body));
            } catch (e) {
                reject(new Error('Invalid JSON body'));
            }
        });
        req.on('error', reject);
    });
}

// Helper: send JSON response
function sendJSON(res, statusCode, data) {
    res.writeHead(statusCode, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(data));
}

const server = http.createServer(async (req, res) => {
    const { method, url } = req;

    // ── POST /login ──────────────────────────────────────────────
    if (method === 'POST' && url === '/login') {
        let body;

        try {
            body = await parseBody(req);
        } catch (err) {
            return sendJSON(res, 400, { success: false, message: 'Invalid JSON body' });
        }

        const { username, password } = body;

        // Validate that both fields are provided
        if (!username || !password) {
            return sendJSON(res, 400, {
                success: false,
                message: 'username and password are required'
            });
        }

        // Query MySQL for matching user
        const sql = 'SELECT * FROM users WHERE username = ? AND password = ?';
        db.query(sql, [username, password], (err, results) => {
            if (err) {
                console.error('DB query error:', err.message);
                return sendJSON(res, 500, { success: false, message: 'Internal server error' });
            }

            if (results.length > 0) {
                const user = results[0];
                return sendJSON(res, 200, {
                    success: true,
                    message: 'Login successful',
                    user: { id: user.id, username: user.username }
                });
            } else {
                return sendJSON(res, 401, {
                    success: false,
                    message: 'Invalid username or password'
                });
            }
        });

        // ── 404 for all other routes ─────────────────────────────────
    } else {
        sendJSON(res, 404, { success: false, message: `Route ${method} ${url} not found` });
    }
});

server.listen(PORT, () => {
    console.log(`Login API server running at http://localhost:${PORT}`);
    console.log('POST http://localhost:3000/login');
    console.log('Body: { "username": "admin", "password": "admin123" }');
});
