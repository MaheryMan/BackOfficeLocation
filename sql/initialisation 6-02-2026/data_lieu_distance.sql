-- ========================================
-- Données pour les Lieux et Distances
-- ========================================

-- Insertion des lieux (points d'intérêt)
INSERT INTO lieu (libelle, code) VALUES
('Aéroport Ivato', 'AIV'),
('Centre-ville Tana', 'CVT'),
('Analakely', 'ANK'),
('Ivandry', 'IVD'),
('Ambohijatovo', 'AMB'),
('Anosy', 'ANS'),
('Tsimbazaza', 'TSB');

-- Insertion des distances entre les lieux (en km)
-- Format: (depuis, vers, distance)
INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES
-- Depuis Aéroport Ivato (1)
(1, 2, 16.5),  -- Aéroport → Centre-ville
(1, 3, 17.2),  -- Aéroport → Analakely
(1, 4, 10.3),  -- Aéroport → Ivandry
(1, 5, 15.8),  -- Aéroport → Ambohijatovo
(1, 6, 18.5),  -- Aéroport → Anosy
(1, 7, 14.2),  -- Aéroport → Tsimbazaza

-- Depuis Centre-ville (2)
(2, 1, 16.5),  -- Centre-ville → Aéroport
(2, 3, 1.2),   -- Centre-ville → Analakely
(2, 4, 8.5),   -- Centre-ville → Ivandry
(2, 5, 2.3),   -- Centre-ville → Ambohijatovo
(2, 6, 3.5),   -- Centre-ville → Anosy
(2, 7, 4.8),   -- Centre-ville → Tsimbazaza

-- Depuis Analakely (3)
(3, 1, 17.2),  -- Analakely → Aéroport
(3, 2, 1.2),   -- Analakely → Centre-ville
(3, 4, 9.1),   -- Analakely → Ivandry
(3, 5, 1.5),   -- Analakely → Ambohijatovo
(3, 6, 2.8),   -- Analakely → Anosy
(3, 7, 3.6),   -- Analakely → Tsimbazaza

-- Depuis Ivandry (4)
(4, 1, 10.3),  -- Ivandry → Aéroport
(4, 2, 8.5),   -- Ivandry → Centre-ville
(4, 3, 9.1),   -- Ivandry → Analakely
(4, 5, 7.8),   -- Ivandry → Ambohijatovo
(4, 6, 10.2),  -- Ivandry → Anosy
(4, 7, 6.5),   -- Ivandry → Tsimbazaza

-- Depuis Ambohijatovo (5)
(5, 1, 15.8),  -- Ambohijatovo → Aéroport
(5, 2, 2.3),   -- Ambohijatovo → Centre-ville
(5, 3, 1.5),   -- Ambohijatovo → Analakely
(5, 4, 7.8),   -- Ambohijatovo → Ivandry
(5, 6, 1.8),   -- Ambohijatovo → Anosy
(5, 7, 2.5),   -- Ambohijatovo → Tsimbazaza

-- Depuis Anosy (6)
(6, 1, 18.5),  -- Anosy → Aéroport
(6, 2, 3.5),   -- Anosy → Centre-ville
(6, 3, 2.8),   -- Anosy → Analakely
(6, 4, 10.2),  -- Anosy → Ivandry
(6, 5, 1.8),   -- Anosy → Ambohijatovo
(6, 7, 3.2),   -- Anosy → Tsimbazaza

-- Depuis Tsimbazaza (7)
(7, 1, 14.2),  -- Tsimbazaza → Aéroport
(7, 2, 4.8),   -- Tsimbazaza → Centre-ville
(7, 3, 3.6),   -- Tsimbazaza → Analakely
(7, 4, 6.5),   -- Tsimbazaza → Ivandry
(7, 5, 2.5),   -- Tsimbazaza → Ambohijatovo
(7, 6, 3.2);   -- Tsimbazaza → Anosy
