CREATE TABLE IF NOT EXISTS users (
    email       VARCHAR(255) PRIMARY KEY,
    name        VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
