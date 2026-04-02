-- =========================================================
-- BackOfficeLocation - Donnees de test
-- Dossier: initialisation 18-03-2026
-- =========================================================

BEGIN;


INSERT INTO lieu (libelle, code) VALUES
    ('Aeroport', 'AER'),
    ('Ivandry', 'IVA');

-- ---------------------------------------------------------
-- Hotels relies aux lieux
-- ---------------------------------------------------------
INSERT INTO hotel (nom, id_lieu) VALUES
    ('Hotel Ivandry Palace', 1);

INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES
    (1, 2, 25);

INSERT INTO voiture (numero, id_type_energie, capacite, heure_disponibilite) VALUES
    ('V001', 2, 5, '09:40:00'),
    ('V002', 2, 12, '09:50:00');
 

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
 
    (1, 1, '2026-03-20 10:00:00', 10),
    (2, 1, '2026-03-20 10:10:00', 7),
  

    (3, 1, '2026-03-20 10:15:00', 3),
    (4, 1, '2026-03-20 10:30:00', 5);

-- ---------------------------------------------------------
-- Tokens de test
-- ---------------------------------------------------------
INSERT INTO token (token, date_heure_expiration) VALUES
    ('TOKEN_TEST_VALID_001', '2026-03-25 23:59:59'),
    ('TOKEN_TEST_EXPIRED_001', '2026-03-10 12:00:00');

COMMIT;