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
