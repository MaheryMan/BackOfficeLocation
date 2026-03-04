create table distance (
    id SERIAL PRIMARY KEY,
    from_id_aeroport int not null,
    to_id_hotel int not null,
    distance decimal(10,2) not null check (distance > 0),

    CONSTRAINT fk_distance_from_aeroport
        FOREIGN KEY (from_id_aeroport)
        REFERENCES aeroport(id),

    CONSTRAINT fk_distance_to_hotel
        FOREIGN KEY (to_id_hotel)
        REFERENCES hotel(id)
);

drop table reservation;

create table reservation(
    id SERIAL PRIMARY KEY,
    id_client int not null,
    id_hotel int not null,
    id_voiture int not null,
    date_heure_arrivee timestamp not null,
    nombre_passager int not null check (nombre_passager > 0),   
    
    CONSTRAINT fk_reservation_client
        FOREIGN KEY (id_client)
        REFERENCES client(id),

    CONSTRAINT fk_reservation_hotel
        FOREIGN KEY (id_hotel)
        REFERENCES hotel(id),

    CONSTRAINT fk_reservation_voiture
        FOREIGN KEY (id_voiture)
        REFERENCES voiture(id)
);