-- =========================================================
-- BackOfficeLocation - Suppression des donnees de test
-- Dossier: initialisation 18-03-2026
-- Ne supprime pas les tables, uniquement les donnees
-- =========================================================

BEGIN;

-- Methode simple et sure pour vider toutes les donnees
-- RESTART IDENTITY remet les sequences a 1
TRUNCATE TABLE
    planification,
    reservation,
    distance,
    hotel,
    client,
    voiture,
    type_energie,
    lieu,
    parametre,
    token
RESTART IDENTITY CASCADE;

COMMIT;
