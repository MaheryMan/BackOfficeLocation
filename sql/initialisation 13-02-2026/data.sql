-- Active: 1749037938113@@127.0.0.1@5432@location_S5
-- Données d'initialisation pour le sprint 2
-- Date: 13/02/2026

-- =========================
-- INSERTION: TypeEnergie
-- =========================
INSERT INTO type_energie (libelle) VALUES 
    ('Diesel'),
    ('Essence'),
    ('Electrique'),
    ('Hybride');

-- =========================
-- INSERTION: Voiture
-- =========================
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES 
    ('V001', 1, 5),
    ('V002', 2, 4),
    ('V003', 3, 7),
    ('V004', 1, 5),
    ('V005', 4, 5);
