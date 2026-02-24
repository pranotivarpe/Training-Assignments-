-- Run this script in your MySQL server to set up the login database

-- Create the database
CREATE DATABASE IF NOT EXISTS login_db;
USE login_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample users (plain-text passwords for simplicity)
INSERT INTO users (username, password) VALUES
('admin', 'admin123'),
('siddhesh', 'pass@123'),
('john', 'john2026');
