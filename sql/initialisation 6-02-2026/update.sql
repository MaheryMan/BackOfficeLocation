-- =========================
-- TABLE: Lieu (doit être créée en premier)
-- =========================
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE
);

-- =========================
-- TABLE: Distance
-- =========================
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
    
    -- Éviter les doublons
    CONSTRAINT unique_from_to UNIQUE (from_id_lieu, to_id_lieu)
);

-- =========================
-- Modification de la table Reservation (si besoin)
-- =========================
DROP TABLE IF EXISTS planification CASCADE;
DROP TABLE IF EXISTS hotel CASCADE;

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
-- TABLE: Planification
-- =========================
CREATE TABLE IF NOT EXISTS planification (
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
    
    -- Une réservation ne peut être affectée qu'à une seule voiture
    CONSTRAINT unique_reservation UNIQUE (id_reservation)
);