-- =========================================================
-- BackOfficeLocation - Evolution schema planification
-- Ajout des colonnes utilisees par la nouvelle logique
-- =========================================================

BEGIN;

ALTER TABLE planification
    ADD COLUMN IF NOT EXISTS date_heure_retour TIMESTAMP,
    ADD COLUMN IF NOT EXISTS nbtrajet INT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS passagers_affectes INT NOT NULL DEFAULT 1;

ALTER TABLE planification
    DROP CONSTRAINT IF EXISTS uq_planification_reservation;

ALTER TABLE planification
    DROP CONSTRAINT IF EXISTS planification_nbtrajet_check;

ALTER TABLE planification
    ADD CONSTRAINT planification_nbtrajet_check CHECK (nbtrajet > 0);

ALTER TABLE planification
    DROP CONSTRAINT IF EXISTS planification_passagers_affectes_check;

ALTER TABLE planification
    ADD CONSTRAINT planification_passagers_affectes_check CHECK (passagers_affectes > 0);

COMMIT;
