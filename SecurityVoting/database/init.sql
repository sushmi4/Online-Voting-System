-- =====================================================
-- Online Voting System - Database Schema & Seed Data
-- Run:  mysql -u root -p < database/init.sql
-- =====================================================

-- Drop and recreate a clean database
DROP DATABASE IF EXISTS onlinevoting_db;
CREATE DATABASE onlinevoting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE onlinevoting_db;

-- Dedicated application user (adjust password if you change config.properties)
CREATE USER IF NOT EXISTS 'voting_app'@'localhost' IDENTIFIED BY 'VotingApp_secure_2026!';
GRANT ALL PRIVILEGES ON onlinevoting_db.* TO 'voting_app'@'localhost';
FLUSH PRIVILEGES;

-- -----------------------------------------------------
-- voters
-- -----------------------------------------------------
CREATE TABLE voters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    voter_id VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    dob VARCHAR(20) NULL,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    image_path VARCHAR(500) NULL,
    address TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',   -- Pending / Approved / Rejected
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------
-- admins (no hardcoded credentials anywhere in the app)
-- -----------------------------------------------------
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,                  -- PBKDF2 salted hash
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed admin account:  username = admin , password = admin123
INSERT INTO admins (username, password)
VALUES ('admin', 'pbkdf2$120000$z35CS+Iywa8sPk5Ym2WFfg==$6Kb6Fvp6HjdkX2LCRzyOhJiXnVtPP9MsuCHOV2Xrqas=');

-- -----------------------------------------------------
-- user_groups (candidate groups)
-- -----------------------------------------------------
CREATE TABLE user_groups (
    sn INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL UNIQUE,
    group_image_path VARCHAR(500) NULL
);

-- -----------------------------------------------------
-- votes (one vote per voter per election year)
-- -----------------------------------------------------
CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voter_id VARCHAR(50) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    vote_year INT NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_voter_year (voter_id, vote_year),
    CONSTRAINT fk_votes_voter FOREIGN KEY (voter_id) REFERENCES voters(voter_id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_group FOREIGN KEY (group_name) REFERENCES user_groups(group_name) ON DELETE CASCADE
);
