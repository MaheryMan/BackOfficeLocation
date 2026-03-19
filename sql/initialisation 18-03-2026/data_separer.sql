-- =========================================================
-- BackOfficeLocation - Data de test report inter-groupe
-- Cas demande:
--   - 2 reservations: 08:00 et 08:15 => depart groupe 08:15
--   - 1 reservation: 10:00
--   - reliquat non assigne du groupe 08:15 reporte sur le groupe 10:00
-- =========================================================

BEGIN;

-- ---------------------------------------------------------
-- Reset donnees metier pour scenario reproductible
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
-- Parametres
-- Fenetre groupement = 30 min
-- ---------------------------------------------------------
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
-- IMPORTANT: Aeroport doit rester id=1
-- ---------------------------------------------------------
INSERT INTO lieu (libelle, code) VALUES
    ('Aeroport', 'AER'),
    ('Ivandry', 'IVA'),
    ('Analakely', 'ANA');

-- ---------------------------------------------------------
-- Hotels
-- ---------------------------------------------------------
INSERT INTO hotel (nom, id_lieu) VALUES
    ('Hotel Ivandry Palace', 2),
    ('Le Louvre Hotel', 3);

-- ---------------------------------------------------------
-- Distances
-- Distances courtes pour que les voitures soient re-disponibles a 10:00
-- ---------------------------------------------------------
INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES
    (1, 2, 10.0),
    (1, 3, 12.0),
    (2, 3, 5.0);

-- ---------------------------------------------------------
-- Flotte volontairement limitee sur le groupe 08:15
-- Capacites: 4 + 3 = 7
-- ---------------------------------------------------------
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
    ('V201', 1, 4),
    ('V202', 2, 3);

-- ---------------------------------------------------------
-- Clients
-- ---------------------------------------------------------
INSERT INTO client (nom, numero_passport, email, contact) VALUES
    ('Client A', 'MG-T001', 'client.a@test.mg', '+261320100001'),
    ('Client B', 'MG-T002', 'client.b@test.mg', '+261320100002'),
    ('Client C', 'MG-T003', 'client.c@test.mg', '+261320100003');

-- ---------------------------------------------------------
-- Reservations (date test)
-- Groupe 1: 08:00 et 08:15 -> depart groupe 08:15
--   R1 = 6 pax (08:00)
--   R2 = 4 pax (08:15)
-- Demande groupe 1 = 10, capacite immediate utilisable = 7
-- => reliquat attendu = 3 pax (sur une reservation 08h)
-- Groupe 2: 10:00
--   R3 = 4 pax (10:00)
-- Le reliquat du groupe 1 doit etre traite avec ce groupe 2, sans priorisation speciale
-- Capacite groupe 2 = 7, charge attendue = 4 + 3(reliquat) = 7 => 0 non assigne attendu
-- ---------------------------------------------------------
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
    (1, 1, '2026-03-19 08:00:00', 6),
    (2, 2, '2026-03-19 08:15:00', 4),
    (3, 1, '2026-03-19 10:00:00', 4);

-- ---------------------------------------------------------
-- Token optionnel
-- ---------------------------------------------------------
INSERT INTO token (token, date_heure_expiration) VALUES
    ('TOKEN_REPORT_GROUPE_001', '2026-04-01 23:59:59');

COMMIT;
