-- =========================================================
-- BackOfficeLocation - Initialisation complete du schema
-- Dossier: initialisation 18-03-2026
-- PostgreSQL
-- =========================================================

-- ---------------------------------------------------------
-- Nettoyage (ordre inverse des dependances)
-- ---------------------------------------------------------
DROP TABLE IF EXISTS planification CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS distance CASCADE;
DROP TABLE IF EXISTS hotel CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS voiture CASCADE;
DROP TABLE IF EXISTS type_energie CASCADE;
DROP TABLE IF EXISTS lieu CASCADE;
DROP TABLE IF EXISTS parametre CASCADE;
DROP TABLE IF EXISTS token CASCADE;

-- ---------------------------------------------------------
-- Table: type_energie
-- ---------------------------------------------------------
CREATE TABLE type_energie (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE
);

-- ---------------------------------------------------------
-- Table: voiture
-- ---------------------------------------------------------
CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(100) NOT NULL UNIQUE,
    id_type_energie INT,
    capacite INT NOT NULL CHECK (capacite > 0),
    CONSTRAINT fk_voiture_type_energie
        FOREIGN KEY (id_type_energie)
        REFERENCES type_energie(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Table: client
-- ---------------------------------------------------------
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero_passport VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    contact VARCHAR(100)
);

-- ---------------------------------------------------------
-- Table: lieu
-- ---------------------------------------------------------
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE
);

-- ---------------------------------------------------------
-- Table: hotel
-- ---------------------------------------------------------
CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    id_lieu INT NOT NULL,
    CONSTRAINT fk_hotel_lieu
        FOREIGN KEY (id_lieu)
        REFERENCES lieu(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- ---------------------------------------------------------
-- Table: distance
-- ---------------------------------------------------------
CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    from_id_lieu INT NOT NULL,
    to_id_lieu INT NOT NULL,
    distance DECIMAL(10,2) NOT NULL CHECK (distance > 0),
    CONSTRAINT fk_distance_from_lieu
        FOREIGN KEY (from_id_lieu)
        REFERENCES lieu(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_distance_to_lieu
        FOREIGN KEY (to_id_lieu)
        REFERENCES lieu(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT ck_distance_not_same_lieu CHECK (from_id_lieu <> to_id_lieu),
    CONSTRAINT uq_distance_pair UNIQUE (from_id_lieu, to_id_lieu)
);

-- ---------------------------------------------------------
-- Table: reservation
-- ---------------------------------------------------------
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    id_hotel INT NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    nombre_passager INT NOT NULL CHECK (nombre_passager > 0),
    CONSTRAINT fk_reservation_client
        FOREIGN KEY (id_client)
        REFERENCES client(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_hotel
        FOREIGN KEY (id_hotel)
        REFERENCES hotel(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- ---------------------------------------------------------
-- Table: planification
-- ---------------------------------------------------------
CREATE TABLE planification (
    id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL,
    voiture_id INT NOT NULL,
    date_heure TIMESTAMP NOT NULL,
    distance_aeroport DECIMAL(10,2) NOT NULL CHECK (distance_aeroport > 0),
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_retour TIMESTAMP,
    nbtrajet INT NOT NULL DEFAULT 1 CHECK (nbtrajet > 0),
    CONSTRAINT fk_planification_voiture
        FOREIGN KEY (voiture_id)
        REFERENCES voiture(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_planification_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservation(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT uq_planification_reservation UNIQUE (reservation_id)
);

-- ---------------------------------------------------------
-- Table: parametre
-- ---------------------------------------------------------
CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    temps_attente INT NOT NULL CHECK (temps_attente >= 0),
    vitesse_moyenne DECIMAL(5,2) NOT NULL CHECK (vitesse_moyenne > 0)
);

-- ---------------------------------------------------------
-- Table: token
-- ---------------------------------------------------------
CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    date_heure_expiration TIMESTAMP NOT NULL
);

-- ---------------------------------------------------------
-- Index utiles
-- ---------------------------------------------------------
CREATE INDEX idx_reservation_date_heure_arrivee ON reservation(date_heure_arrivee);
CREATE INDEX idx_reservation_client ON reservation(id_client);
CREATE INDEX idx_reservation_hotel ON reservation(id_hotel);
CREATE INDEX idx_hotel_lieu ON hotel(id_lieu);
CREATE INDEX idx_distance_from_to ON distance(from_id_lieu, to_id_lieu);
CREATE INDEX idx_planification_date_heure ON planification(date_heure);