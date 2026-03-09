-- ========================================
-- Données de test pour Client et Hotel
-- ========================================

-- Insertion des clients
INSERT INTO client (nom, numero_passport, email, contact) VALUES
('Rakoto Jean', 'P123456', 'rakoto.jean@email.mg', '+261 32 11 222 33'),
('Rabe Marie', 'P234567', 'rabe.marie@email.mg', '+261 33 44 555 66'),
('Randria Paul', 'P345678', 'randria.paul@email.mg', '+261 34 77 888 99'),
('Rasoanirina Sophie', 'P456789', 'rasoanirina.sophie@email.mg', '+261 32 12 345 67'),
('Andriamanitra Luc', 'P567890', 'andriamanitra.luc@email.mg', '+261 33 98 765 43'),
('Raharison Emma', 'P678901', 'raharison.emma@email.mg', '+261 34 11 223 34'),
('Razafindrakoto Michel', 'P789012', 'razafindrakoto.michel@email.mg', '+261 32 55 667 78'),
('Rakotondrabe Alice', 'P890123', 'rakotondrabe.alice@email.mg', '+261 33 99 887 76'),
('Ramaroson David', 'P901234', 'ramaroson.david@email.mg', '+261 34 22 334 45'),
('Rafalimanana Clara', 'P012345', 'rafalimanana.clara@email.mg', '+261 32 88 776 65');

-- Insertion des hotels
INSERT INTO hotel (nom, distance_aeroport) VALUES
('Hotel Ivandry Palace', 5.2),
('Radisson Blu Antananarivo', 12.5),
('Novotel Convention', 10.8),
('Hotel Carlton Madagascar', 11.3),
('Le Louvre Hotel', 8.7),
('Palissandre Hotel & Spa', 13.2),
('Hotel Colbert', 10.5),
('Tamboho Hotel', 9.3),
('Tana Waterfront', 15.7),
('Sakamanga Hotel', 12.1);

-- Insertion des types d'énergie (AVANT les voitures)
INSERT INTO type_energie (libelle) VALUES
('essence'),
('diesel');

-- Insertion des voitures
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
('V001', 2, 15),  -- Diesel, grande capacité
('V002', 2, 13),  -- Diesel, capacité moyenne-haute
('V003', 1, 12),  -- Essence, capacité moyenne-haute
('V004', 2, 8),   -- Diesel, capacité moyenne
('V005', 1, 8),   -- Essence, capacité moyenne
('V006', 2, 5),   -- Diesel, petite capacité
('V007', 1, 5),   -- Essence, petite capacité
('V008', 2, 7),   -- Diesel, petite capacité
('V009', 1, 10),  -- Essence, capacité moyenne
('V010', 2, 20);  -- Diesel, très grande capacité



-- Insertion des réservations de test
INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES
-- Réservations pour le 2026-03-04 (aujourd'hui)
(1, 1, '2026-03-04 08:00:00', 12),  -- Gros groupe, proche aéroport
(2, 3, '2026-03-04 09:30:00', 7),   -- Groupe moyen
(3, 2, '2026-03-04 10:00:00', 5),   -- Petit groupe, loin
(4, 5, '2026-03-04 11:00:00', 3),   -- Petit groupe
(5, 4, '2026-03-04 14:00:00', 10),  -- Groupe moyen-grand
(6, 7, '2026-03-04 15:30:00', 8),   -- Groupe moyen
(7, 6, '2026-03-04 16:00:00', 4),   -- Petit groupe

-- Réservations pour le 2026-03-05
(8, 8, '2026-03-05 08:00:00', 15),  -- Gros groupe
(9, 9, '2026-03-05 10:00:00', 6),   -- Groupe moyen
(10, 10, '2026-03-05 12:00:00', 9); -- Groupe moyen


INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
('V005', 1, 23);