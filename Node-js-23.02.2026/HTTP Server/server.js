const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const HOST = 'localhost';
const PORT = 3000;

// ── Demo credentials (for assignment purposes) ──────────────────────────────
// In a real app credentials would be stored securely (hashed) in a database.
const VALID_USERNAME = 'admin';
const VALID_PASSWORD = 'password123';

// ── File reader helper ──────────────────────────────────────────────────────
function readFile(filename) {
    return fs.readFileSync(path.join(__dirname, filename), 'utf8');
}

// ── Parse URL-encoded form body ─────────────────────────────────────────────
function parseBody(raw) {
    const params = new URLSearchParams(raw);
    return {
        username: params.get('username') || '',
        password: params.get('password') || '',
    };
}

// ── Request handler ─────────────────────────────────────────────────────────
const server = http.createServer((req, res) => {
    const pathname = url.parse(req.url).pathname;

    // ── Serve CSS ──────────────────────────────────────────────────────────
    if (pathname === '/style.css') {
        res.writeHead(200, { 'Content-Type': 'text/css' });
        res.end(readFile('style.css'));
        return;
    }

    // Only handle the root path for everything else
    if (pathname !== '/') {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('404 Not Found');
        return;
    }

    // ── GET / → show login form ──────────────────────────────────────────
    if (req.method === 'GET') {
        // Remove the {{ALERT}} placeholder for a fresh load
        const html = readFile('login.html').replace('{{ALERT}}', '');
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.end(html);
        return;
    }

    // ── POST / → process login ───────────────────────────────────────────
    if (req.method === 'POST') {
        let body = '';

        req.on('data', chunk => { body += chunk.toString(); });

        req.on('end', () => {
            const { username, password } = parseBody(body);

            if (username === VALID_USERNAME && password === VALID_PASSWORD) {
                // Success – inject the username into the welcome page
                const html = readFile('welcome.html').replace('{{USERNAME}}', username);
                res.writeHead(200, { 'Content-Type': 'text/html' });
                res.end(html);
            } else {
                // Failure – inject an error alert into the login page
                const alert = '<div class="alert error">Invalid username or password. Please try again.</div>';
                const html = readFile('login.html').replace('{{ALERT}}', alert);
                res.writeHead(401, { 'Content-Type': 'text/html' });
                res.end(html);
            }
        });

        return;
    }

    // Any other HTTP method
    res.writeHead(405, { 'Content-Type': 'text/plain' });
    res.end('405 Method Not Allowed');
});

// ── Start server ─────────────────────────────────────────────────────────────
server.listen(PORT, HOST, () => {
    console.log(`HTTP server running at http://${HOST}:${PORT}/`);
    console.log('Demo credentials → username: admin  |  password: password123');
    console.log('Press Ctrl+C to stop.');
});
