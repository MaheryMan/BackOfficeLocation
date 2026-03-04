-- ========================================
-- Script complet d'initialisation de la base de données
-- A exécuter dans l'ordre
-- ========================================

-- Étape 1: Créer la base de données si elle n'existe pas
-- CREATE DATABASE location_S5;

-- Étape 2: Créer les tables de base
\i init.sql

-- Étape 3: Ajouter les tables supplémentaires (lieu, distance, planification)
\i update.sql

-- Étape 4: Insérer les données de test
\i data.sql

-- Étape 5: (Optionnel) Insérer les données de lieux et distances
-- \i data_lieu_distance.sql

-- ========================================
-- Vérification des données
-- ========================================
SELECT 'Clients' as table_name, COUNT(*) as count FROM client
UNION ALL
SELECT 'Hotels', COUNT(*) FROM hotel
UNION ALL
SELECT 'Type Energie', COUNT(*) FROM type_energie
UNION ALL
SELECT 'Voitures', COUNT(*) FROM voiture
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservation
UNION ALL
SELECT 'Lieux', COUNT(*) FROM lieu
UNION ALL
SELECT 'Distances', COUNT(*) FROM distance;
