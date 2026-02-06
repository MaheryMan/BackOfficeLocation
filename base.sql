-- Active: 1749037938113@@127.0.0.1@5432@location_S5
CREATE DATABASE location_S5
-- =========================
-- TABLE: TypeEnergie
-- =========================

CREATE TABLE type_energie (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL UNIQUE
);

-- =========================
-- TABLE: Voiture
-- =========================
CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(100) UNIQUE,
    id_type_energie INT NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),

    CONSTRAINT fk_voiture_type_energie
        FOREIGN KEY (id_type_energie)
        REFERENCES type_energie(id)
);

-- =========================
-- TABLE: Client
-- =========================
CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero_passport VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    contact VARCHAR(100)
);

-- =========================
-- TABLE: Hotel
-- =========================
CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    distance_aeroport DECIMAL(10,2) NOT NULL CHECK (distance_aeroport > 0)
);

-- =========================
-- TABLE: Reservation
-- =========================
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

-- =========================
-- TABLE: Unite
-- =========================
CREATE TABLE unite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- =========================
-- TABLE: Parametre
-- =========================
CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    valeur VARCHAR(255) NOT NULL,
    id_unite INT,

    CONSTRAINT fk_parametre_unite
        FOREIGN KEY (id_unite)
        REFERENCES unite(id)
);
