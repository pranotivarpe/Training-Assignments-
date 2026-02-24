const mysql = require('mysql2');

// MySQL connection configuration - update these values as per your MySQL setup
const connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',        // your MySQL username
    password: 'Pranu@2001',        // your MySQL password
    database: 'login_db'
});

connection.connect((err) => {
    if (err) {
        console.error('MySQL connection failed:', err.message);
        process.exit(1);
    }
    console.log('Connected to MySQL database.');
});

module.exports = connection;
