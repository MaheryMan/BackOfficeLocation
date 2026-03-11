-- ========================================
-- Données de test pour valider la logique de planification
-- Date: 10/02/2026
-- ========================================
-- SCENARIO: Tester le blocage de voiture et la combinaison de réservations

-- Insertion des types d'énergie
INSERT INTO type_energie (libelle) VALUES
('Diesel'),
('Essence');

-- ========================================
-- VOITURES: 3 voitures pour le test
-- ========================================
-- V01: capacité 5 (Diesel)
-- V02: capacité 15 (Diesel)
-- V03: capacité 2 (Essence)
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
('V01', 1, 5),      -- Diesel, capacité 5
('V02', 1, 15),     -- Diesel, capacité 15
('V03', 2, 2);      -- Essence, capacité 2

-- ========================================
-- CLIENTS: 5 clients pour 5 réservations
-- ========================================
INSERT INTO client (nom, numero_passport, email, contact) VALUES
('Client Un', 'P001', 'client1@email.mg', '+261 32 00 00 01'),
('Client Deux', 'P002', 'client2@email.mg', '+261 32 00 00 02'),
('Client Trois', 'P003', 'client3@email.mg', '+261 32 00 00 03'),
('Client Quatre', 'P004', 'client4@email.mg', '+261 32 00 00 04'),
('Client Cinq', 'P005', 'client5@email.mg', '+261 32 00 00 05');

-- ========================================
-- HOTELS: 5 hotels pour les réservations
-- ========================================
INSERT INTO hotel (nom, distance_aeroport) VALUES
('Hotel A', 5.0),
('Hotel B', 6.0),
('Hotel C', 7.0),
('Hotel D', 8.0),
('Hotel E', 9.0);

-- ========================================
-- RÉSERVATIONS: Scenario de test
-- Date: 2026-03-11 (aujourd'hui dans l'exercice)
-- ========================================

-- ==== 8h00 - 3 réservations (même heure) ====
-- Res 1: 5 passagers (devrait aller V01 car 5 est plus proche que 15)
-- Res 2: 10 passagers (devrait aller V02)
-- Res 3: 3 passagers (DEVRAIT combiner avec V02 car même 08:00 et capacité restante suffisante)
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
(1, 1, '2026-03-11 08:00:00', 5),    -- Res 1: 5 passagers
(2, 2, '2026-03-11 08:00:00', 10),   -- Res 2: 10 passagers
(3, 3, '2026-03-11 08:00:00', 3);    -- Res 3: 3 passagers

-- ==== 10h00 - 1 réservation ====
-- Res 4: 2 passagers (DEVRAIT ALLER V03 car V01 et V02 sont bloquées)
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
(4, 4, '2026-03-11 10:00:00', 2);    -- Res 4: 2 passagers

-- ==== 14h00 - 1 réservation ====
-- Res 5: 5 passagers (DEVRAIT ÊTRE SANS VOITURE car V01, V02 bloquées, V03 aussi bloquée)
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
(5, 5, '2026-03-11 14:00:00', 5);    -- Res 5: 5 passagers

-- ========================================
-- RÉSULTATS ATTENDUS (avec logique corrigée)
-- ========================================
-- Planification attendue:
-- - Res 1 (5 pax, 08:00) → V01 (capacité 5)      ✓ Assignée (capacité la plus proche)
-- - Res 2 (10 pax, 08:00) → V02 (capacité 15)    ✓ Assignée
-- - Res 3 (3 pax, 08:00) → V02 (combine)         ✓ Assignée (même horaire, capacité restante 5 >= 3)
-- - Res 4 (2 pax, 10:00) → V03 (capacité 2)      ✓ Assignée (V01, V02 bloquées)
-- - Res 5 (5 pax, 14:00) → AUCUNE                ✗ Sans voiture (toutes bloquées)
-- 
-- Voitures bloquées après 08:00: V01, V02
-- Voitures bloquées après 10:00: V01, V02, V03
-- Passagers affectés: 5 + 10 + 3 + 2 = 20
-- Passagers sans véhicule: 5
-- ========================================
