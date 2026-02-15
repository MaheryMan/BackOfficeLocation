-- Active: 1749037938113@@127.0.0.1@5432@location_S5
-- Script d'initialisation pour le sprint 2
-- Date: 13/02/2026

-- =========================
-- TABLE: Token
-- =========================
CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    date_heure_expiration TIMESTAMP NOT NULL
);
