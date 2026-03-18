-- =========================================================
-- BackOfficeLocation - Donnees de test
-- Dossier: initialisation 18-03-2026
-- =========================================================

BEGIN;

-- ---------------------------------------------------------
-- Parametres globaux de planification
-- ---------------------------------------------------------
INSERT INTO parametre (temps_attente, vitesse_moyenne)
VALUES (30, 40);

-- ---------------------------------------------------------
-- Types d'energie
-- ---------------------------------------------------------
INSERT INTO type_energie (libelle) VALUES
    ('Diesel'),
    ('Essence');

-- ---------------------------------------------------------
-- Lieux (4 max, tous relies entre eux)
-- IMPORTANT: Aeroport en premier pour avoir id=1
-- ---------------------------------------------------------
INSERT INTO lieu (libelle, code) VALUES
    ('Aeroport', 'AER'),
    ('Ivandry', 'IVA'),
    ('Analakely', 'ANA'),
    ('Alarobia', 'ALR');

-- ---------------------------------------------------------
-- Hotels relies aux lieux
-- ---------------------------------------------------------
INSERT INTO hotel (nom, id_lieu) VALUES
    ('Hotel Ivandry Palace', 2),
    ('Le Louvre Hotel', 3),
    ('Ibis Antananarivo', 4);

-- ---------------------------------------------------------
-- Distances (km)
-- ---------------------------------------------------------
-- Graphe complet entre les 4 lieux (chaque paire est reliee)
INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES
    (1, 2, 14.0),
    (1, 3, 17.0),
    (1, 4, 16.0),
    (2, 3, 5.0),
    (2, 4, 4.0),
    (3, 4, 6.0);

-- ---------------------------------------------------------
-- Flotte de voitures
-- ---------------------------------------------------------
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
    ('V001', 1, 4),
    ('V002', 2, 6),
    ('V003', 1, 8),
    ('V004', 2, 12);

-- ---------------------------------------------------------
-- Clients
-- ---------------------------------------------------------
INSERT INTO client (nom, numero_passport, email, contact) VALUES
    ('Rakoto Jean', 'MG-P0001', 'rakoto.jean@test.mg', '+261320000001'),
    ('Rabe Marie', 'MG-P0002', 'rabe.marie@test.mg', '+261320000002'),
    ('Randria Paul', 'MG-P0003', 'randria.paul@test.mg', '+261320000003'),
    ('Rasoanirina Sophie', 'MG-P0004', 'rasoanirina.sophie@test.mg', '+261320000004');

-- ---------------------------------------------------------
-- Reservations de test (jour principal: 2026-03-18)
-- ---------------------------------------------------------
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
    -- Groupe 08h00 - 08h30 (dans la fenetre de 30 min)
    (1, 1, '2026-03-18 08:00:00', 4),
    (2, 2, '2026-03-18 08:20:00', 3),

    -- Groupe 10h00 - 10h30
    (3, 3, '2026-03-18 10:00:00', 6),
    (4, 1, '2026-03-18 10:25:00', 2);

-- ---------------------------------------------------------
-- Tokens de test
-- ---------------------------------------------------------
INSERT INTO token (token, date_heure_expiration) VALUES
    ('TOKEN_TEST_VALID_001', '2026-03-25 23:59:59'),
    ('TOKEN_TEST_EXPIRED_001', '2026-03-10 12:00:00');

COMMIT;