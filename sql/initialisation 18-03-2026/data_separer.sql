-- =========================================================
-- BackOfficeLocation - Data de test pour separation passagers
-- Objectif: tester le split d une reservation sur plusieurs voitures
-- =========================================================

BEGIN;

-- ---------------------------------------------------------
-- Reset donnees metier pour un scenario reproductible
-- ---------------------------------------------------------
TRUNCATE TABLE
    planification,
    reservation,
    distance,
    hotel,
    client,
    voiture,
    type_energie,
    lieu,
    parametre,
    token
RESTART IDENTITY CASCADE;

-- ---------------------------------------------------------
-- Parametres globaux
-- ---------------------------------------------------------
-- Fenetre de groupement: 30 minutes
-- Vitesse moyenne: 40 km/h
INSERT INTO parametre (temps_attente, vitesse_moyenne)
VALUES (30, 40);

-- ---------------------------------------------------------
-- Types energie
-- ---------------------------------------------------------
INSERT INTO type_energie (libelle) VALUES
    ('Diesel'),
    ('Essence');

-- ---------------------------------------------------------
-- Lieux
-- IMPORTANT: Aeroport doit etre id=1
-- ---------------------------------------------------------
INSERT INTO lieu (libelle, code) VALUES
    ('Aeroport', 'AER'),
    ('Ivandry', 'IVA'),
    ('Analakely', 'ANA'),
    ('Alarobia', 'ALR'),
    ('Ankorondrano', 'ANK');

-- ---------------------------------------------------------
-- Hotels
-- ---------------------------------------------------------
INSERT INTO hotel (nom, id_lieu) VALUES
    ('Hotel Ivandry Palace', 2),
    ('Le Louvre Hotel', 3),
    ('Ibis Antananarivo', 4),
    ('Ankorondrano Suites', 5);

-- ---------------------------------------------------------
-- Distances (graphe complet entre 5 lieux)
-- ---------------------------------------------------------
INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES
    (1, 2, 12.0),
    (1, 3, 16.0),
    (1, 4, 14.0),
    (1, 5, 10.0),
    (2, 3, 6.0),
    (2, 4, 5.0),
    (2, 5, 4.0),
    (3, 4, 7.0),
    (3, 5, 8.0),
    (4, 5, 6.0);

-- ---------------------------------------------------------
-- Flotte
-- NOTE: pas de voiture >= 10, pour forcer le split de la resa 10 pax
-- ---------------------------------------------------------
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
    ('V002', 2, 2),
    ('V005', 1, 5),
    ('V006', 2, 6),
    ('V008', 1, 8);

-- ---------------------------------------------------------
-- Clients
-- ---------------------------------------------------------
INSERT INTO client (nom, numero_passport, email, contact) VALUES
    ('Rakoto Jean', 'MG-P1001', 'rakoto.jean@test.mg', '+261320001001'),
    ('Rabe Marie', 'MG-P1002', 'rabe.marie@test.mg', '+261320001002'),
    ('Randria Paul', 'MG-P1003', 'randria.paul@test.mg', '+261320001003'),
    ('Rasoanirina Sophie', 'MG-P1004', 'rasoanirina.sophie@test.mg', '+261320001004'),
    ('Ratsimba Luc', 'MG-P1005', 'ratsimba.luc@test.mg', '+261320001005'),
    ('Razafindrabe Anna', 'MG-P1006', 'razafindrabe.anna@test.mg', '+261320001006'),
    ('Ravelona Eric', 'MG-P1007', 'ravelona.eric@test.mg', '+261320001007'),
    ('Ramanantsoa Mia', 'MG-P1008', 'ramanantsoa.mia@test.mg', '+261320001008'),
    ('Andria Toto', 'MG-P1009', 'andria.toto@test.mg', '+261320001009');

-- ---------------------------------------------------------
-- Reservations test principal (date: 2026-03-18)
-- ---------------------------------------------------------
-- Groupe unique (08:00 - 08:30) pour eviter tout cas de re-disponibilite
-- Capacite flotte = 2 + 5 + 6 + 8 = 21
-- Demande totale = 10 + 9 + 7 + 5 + 4 = 35
-- Attendu:
--  - split de reservations (ex: 10 -> 8 + 2)
--  - surcharge avec passagers restants sans voiture
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
    (1, 1, '2026-03-18 08:00:00', 10),
    (5, 1, '2026-03-18 08:05:00', 9),
    (6, 1, '2026-03-18 08:10:00', 7),
    (2, 2, '2026-03-18 08:15:00', 5),
    (8, 4, '2026-03-18 08:20:00', 4);

-- ---------------------------------------------------------
-- Tokens
-- ---------------------------------------------------------
INSERT INTO token (token, date_heure_expiration) VALUES
    ('TOKEN_SPLIT_VALID_001', '2026-03-30 23:59:59'),
    ('TOKEN_SPLIT_EXPIRED_001', '2026-03-10 10:00:00');

COMMIT;
