

DROP TABLE IF EXISTS planification CASCADE;
DROP TABLE IF EXISTS distance CASCADE;
DROP TABLE IF EXISTS lieu CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS voiture CASCADE;


CREATE TABLE type_energie (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(100) UNIQUE,
    id_type_energie INT NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),

    CONSTRAINT fk_voiture_type_energie
        FOREIGN KEY (id_type_energie)
        REFERENCES type_energie(id)
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero_passport VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    contact VARCHAR(100)
);

CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    distance_aeroport DECIMAL(10,2) NOT NULL CHECK (distance_aeroport > 0)
);


CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    id_hotel INT NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    nombre_passager INT NOT NULL CHECK (nombre_passager > 0),

    CONSTRAINT fk_reservation_client
        FOREIGN KEY (id_client)
        REFERENCES client(id),

    CONSTRAINT fk_reservation_hotel
        FOREIGN KEY (id_hotel)
        REFERENCES hotel(id)
);


CREATE TABLE unite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    valeur VARCHAR(255) NOT NULL,
    id_unite INT,

    CONSTRAINT fk_parametre_unite
        FOREIGN KEY (id_unite)
        REFERENCES unite(id)
);


CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE
);


CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    from_id_lieu INT NOT NULL,
    to_id_lieu INT NOT NULL,
    distance DECIMAL(10,2) NOT NULL CHECK (distance > 0),

    CONSTRAINT fk_distance_from_lieu
        FOREIGN KEY (from_id_lieu)
        REFERENCES lieu(id),
    
    CONSTRAINT fk_distance_to_lieu
        FOREIGN KEY (to_id_lieu)
        REFERENCES lieu(id),
    
    CONSTRAINT unique_from_to UNIQUE (from_id_lieu, to_id_lieu)
);


CREATE TABLE planification (
    id SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_voiture INT NOT NULL,
    date_affectation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_planification_reservation
        FOREIGN KEY (id_reservation)
        REFERENCES reservation(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_planification_voiture
        FOREIGN KEY (id_voiture)
        REFERENCES voiture(id)
        ON DELETE CASCADE,
    
    CONSTRAINT unique_reservation UNIQUE (id_reservation)
);


\echo '[5/5] Insertion des données de test...'


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

-- ========================================
-- Données: Types d'énergie
-- ========================================
INSERT INTO type_energie (libelle) VALUES
('essence'),
('diesel');

-- ========================================
-- Données: Voitures
-- ========================================
INSERT INTO voiture (numero, id_type_energie, capacite) VALUES
('V001', 1, 15), 
('V002', 2, 15), 
('V003', 1, 16),
('V004', 2, 5);


INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES

(1, 1, '2026-03-04 08:00:00', 8),  --v002
(2, 1, '2026-03-04 08:30:00', 7),  --v001
(3, 1, '2026-03-04 08:00:00', 5),  --v002
(4, 3, '2026-03-04 11:00:00', 1),   --v004
(5, 4, '2026-03-04 14:00:00', 10); --v003  // v002


(8, 8, '2026-03-05 08:00:00', 15),  -- Gros groupe
(9, 9, '2026-03-05 10:00:00', 6),   -- Groupe moyen
(10, 10, '2026-03-05 12:00:00', 9); -- Groupe moyen

-- ========================================
-- Données: Lieux (optionnel pour Sprint 4)
-- ========================================
INSERT INTO lieu (libelle, code) VALUES
('Aéroport Ivato', 'AIV'),
('Centre-ville Tana', 'CVT'),
('Analakely', 'ANK'),
('Ivandry', 'IVD'),
('Ambohijatovo', 'AMB'),
('Anosy', 'ANS'),
('Tsimbazaza', 'TSB');




