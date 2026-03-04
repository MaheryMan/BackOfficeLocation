# Scripts SQL - Documentation

## Structure des fichiers

### 1. `init.sql`
Crée les tables de base du système:
- `type_energie` - Types d'énergie des voitures
- `voiture` - Véhicules disponibles
- `client` - Clients
- `hotel` - Hôtels avec distance à l'aéroport
- `reservation` - Réservations des clients
- `unite` - Unités de mesure
- `parametre` - Paramètres système

### 2. `update.sql`
Ajoute les tables supplémentaires:
- `lieu` - Lieux/points d'intérêt
- `distance` - Distances entre lieux
- `planification` - Affectation voiture-réservation

### 3. `data.sql`
Données de test incluant:
- 10 clients
- 10 hôtels avec distances variées
- 10 voitures (capacités 5-20, essence/diesel)
- 10 réservations de test (dont 7 pour le 2026-03-04)

### 4. `data_lieu_distance.sql` (optionnel)
Données pour le système de lieux et distances (Sprint 4)

### 5. `reset.sql`
Script de réinitialisation complète (⚠️ supprime toutes les données)

### 6. `run_all.sql`
Script pour exécuter toute l'initialisation dans l'ordre

## Ordre d'exécution

### Initialisation complète
```sql
\i reset.sql      -- (optionnel) Nettoyer la base
\i init.sql       -- Créer tables de base
\i update.sql     -- Ajouter tables supplémentaires
\i data.sql       -- Insérer données de test
```

### Ou utiliser le script automatique
```sql
\i run_all.sql
```

## Données de test pour Sprint 3

### Voitures disponibles
| ID | Numéro | Type    | Capacité |
|----|--------|---------|----------|
| 1  | V001   | Diesel  | 15       |
| 2  | V002   | Diesel  | 13       |
| 3  | V003   | Essence | 12       |
| 4  | V004   | Diesel  | 8        |
| 5  | V005   | Essence | 8        |
| 6  | V006   | Diesel  | 5        |
| 7  | V007   | Essence | 5        |
| 8  | V008   | Diesel  | 7        |
| 9  | V009   | Essence | 10       |
| 10 | V010   | Diesel  | 20       |

### Réservations du 2026-03-04
| ID | Passagers | Hôtel                      | Distance | Heure  |
|----|-----------|----------------------------|----------|--------|
| 1  | 12        | Hotel Ivandry Palace       | 5.2 km   | 08:00  |
| 2  | 7         | Novotel Convention         | 10.8 km  | 09:30  |
| 3  | 5         | Radisson Blu               | 12.5 km  | 10:00  |
| 4  | 3         | Le Louvre Hotel            | 8.7 km   | 11:00  |
| 5  | 10        | Hotel Carlton              | 11.3 km  | 14:00  |
| 6  | 8         | Hotel Colbert              | 10.5 km  | 15:30  |
| 7  | 4         | Palissandre Hotel & Spa    | 13.2 km  | 16:00  |

## Test de la planification

Après l'initialisation, tester avec:
```
GET http://localhost:8080/api/planification?date=2026-03-04
```

Résultat attendu: Affectations optimales selon les règles:
1. Priorité aux groupes importants (12, 10, 8, 7, 5, 4, 3)
2. Puis par distance croissante
3. Voitures avec capacité la plus proche
4. Préférence diesel en cas d'égalité
