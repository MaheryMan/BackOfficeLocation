DROP TABLE parametre;
DROP TABLE unite;

CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    temps_attente INT
);

INSERT INTO parametre (temps_attente) VALUES (30);