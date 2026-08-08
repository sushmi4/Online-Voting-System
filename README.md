# Online Voting System

A desktop Java Swing application for secure online voting with voter registration,
admin approval, vote casting, live results and OTP-based password reset.

## Features

- Voter registration (with age verification, profile photo upload)
- Admin approval/rejection of voters
- One vote per voter per election (enforced by the database)
- Live election results (public + admin views)
- Secure authentication
  - PBKDF2 salted password hashing
  - Failed-login lockout (5 attempts -> 5 min lock)
  - Idle session timeout (default 30 min)
  - No hardcoded admin credentials (stored hashed in the DB)
  - OTP verification via email for password reset
- Configurable election year, voter limit and session timeout

## Tech Stack

- Java 21 (Swing)
- MySQL 8
- Libraries: `mysql-connector-j`, `javax.mail`, `jcalendar`

## Setup

### 1. Database

Create the schema, application user and seed admin:

```
mysql -u root -p < SecurityVoting/database/init.sql
```

This creates database `onlinevoting_db`, user `voting_app` and an admin account:

- username: `admin`
- password: `admin123`  (change it after first login)

### 2. Configuration

```
cd SecurityVoting
copy config.properties.example config.properties
```

Edit `config.properties` and set your DB password and (optionally) SMTP settings.

For Gmail OTP emails: set `smtp.host=smtp.gmail.com`, `smtp.port=587`,
`smtp.from=<your-gmail>`, `smtp.password=<your Gmail App Password>`.

### 3. Build & Run

```
cd SecurityVoting
javac -encoding UTF-8 -cp "lib\*" -d bin src\*.java
java -cp "bin;lib\*;." MainApp
```

Or open the folder in VS Code (Java extension) and press Run.

## Database Schema (summary)

- `voters` – registration data, status, lockout counters
- `admins` – administrator accounts (PBKDF2 hashes)
- `user_groups` – candidate groups for the election
- `votes` – one row per voter per election year (`UNIQUE(voter_id, vote_year)`)

## Security Notes

- `config.properties` contains credentials — it is git-ignored; use the `.example`
  template for commits.
- The reset-password OTP flow requires SMTP settings; without them the email
  cannot be delivered.
