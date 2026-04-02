# 📖 Algorithme et Documentation : PlanificationService

Le fichier `PlanificationService.java` est le **cerveau** de l'application. Son rôle est de prendre une liste de clients qui arrivent à l'aéroport, une liste de voitures disponibles, et de créer un planning optimisé de trajets en respectant des règles de temps et de priorité.

---

## 🧠 1. L'Algorithme Principal (Le Grand Déroulement)

Quand on lance une planification pour une journée, voici les étapes que l'algorithme suit (c'est la méthode `getPlanification`) :

1. **Récolte des données :** Récupère toutes les réservations de la journée et toutes les voitures de la base de données.
2. **Tri Chronologique :** Trie toutes les réservations de la plus tôt à la plus tardive.
3. **Regroupement (Les "Vols") :** Regroupe les réservations qui arrivent dans la même fenêtre de temps (ex: tous ceux qui arrivent à 30 minutes d'intervalle forment un "Groupe").
4. **Tri Spatial :** Dans chaque groupe, on trie les réservations par distance (de l'hôtel le plus proche de l'aéroport au plus éloigné).
5. **Assignation (Le cœur du système) :** L'algorithme fait avancer le temps et décide quelle voiture prend quel groupe. Il gère deux cas :
   * **Les voitures neuves :** Elles prennent les nouveaux arrivants de leur heure de disponibilité.
   * **Les voitures de retour :** Elles reviennent à l'aéroport et prennent en **priorité absolue** les clients qui n'avaient pas eu de place avant (les reliquats).

---

## 🛠️ 2. Explication des Méthodes (Le Dictionnaire du Code)

Voici l'explication des méthodes contenues dans le fichier, classées par ordre logique.

### 📍 A. La Préparation des Données
* **`getPlanification(LocalDate date)`** : C'est le point d'entrée principal. C'est elle qui appelle toutes les autres méthodes pour générer le résultat final.
* **`getReservationsForDate(...)`** : Va chercher en base de données uniquement les réservations prévues pour la date demandée.
* **`regrouperParTempsAttente(...)`** : Regroupe les clients. Si le premier client arrive à 08h00 et que le temps d'attente est de 30 minutes, tous ceux qui arrivent entre 08h00 et 08h30 sont mis dans le même "panier".
* **`triReservationParHeureArrivee(...)`** : Trie simplement une liste de clients par heure d'arrivée.

### 🚗 B. Le Moteur d'Assignation (Le cœur de la logique)
* **`assignerVoitures(...)`** : C'est le chef d'orchestre. Il boucle sur les groupes de clients.
  * Il avance dans le temps et se pose une question : *"Est-ce qu'une voiture rentre de trajet ?"*
  * **Si OUI :** Il rassemble les clients oubliés (reliquats) et les nouveaux, et force la voiture de retour à prendre les oubliés en premier.
  * **Si NON :** Il prend une voiture neuve pour gérer le prochain groupe de vols normal.
* **`traiterAssignationCandidats(...)`** : C'est la méthode qui fait entrer les gens dans les voitures. 
  * Elle trie les clients du plus grand groupe au plus petit.
  * Elle appelle la méthode pour trouver une voiture.
  * Si la voiture n'est pas pleine, elle cherche d'autres petits groupes pour boucher les trous (Mode Tetris).
* **`trouverMeilleureVoiture(...)`** : Cherche la voiture idéale pour un groupe de clients. 
  * Ses règles : Capacité suffisante ? -> Moins de trajets déjà faits ? -> Moteur Diesel ?
  * **Règle d'or :** Une voiture neuve refusera toujours de prendre un "client oublié" (reliquat). C'est le travail des voitures de retour.
* **`trouverReservationPourCompleterVoiture(...)`** : L'algorithme du Tetris. S'il reste 3 places dans la voiture, cette méthode va chercher en priorité un groupe de 3 personnes, sinon de 2 personnes, sinon de 1 personne pour remplir le vide.

### 🗺️ C. Les Itinéraires et les Distances
* **`ajouterPlanificationsPourVoitureEtGroupe(...)`** : Une fois qu'une voiture est remplie de passagers, cette méthode génère les "billets" (les objets Planification) pour chaque hôtel et calcule l'heure exacte à laquelle la voiture sera de retour à l'aéroport.
* **`construireOrdreItineraireProcheEnProche(...)`** : Calcule le chemin du chauffeur. Il part de l'aéroport, va à l'hôtel le plus proche, puis depuis cet hôtel va au prochain hôtel le plus proche, etc.
* **`calculerDistanceTotaleCircuit(...)`** : Additionne les kilomètres. (Aéroport -> Hôtel A -> Hôtel B -> Retour Aéroport).
* **`calculerDateHeureRetour(...)`** : Mathématiques simples. Elle prend la distance totale du circuit, la divise par la vitesse de la voiture (ex: 40 km/h) pour savoir combien de minutes a duré le trajet, et l'ajoute à l'heure de départ.

### ⏳ D. Gestion du Temps et de la Disponibilité
* **`calculerDisponibiliteVoitureDepuis(...)`** : Regarde à quelle heure une voiture est physiquement prête. Elle combine l'heure de base de la voiture (ex: le chauffeur commence à 08h30) et l'heure de retour de son précédent trajet.
* **`estDisponibleSelonHeureVoiture(...)`** : Vérifie simplement si la voiture a le droit de rouler à l'heure de la réservation (on ne fait pas travailler un chauffeur avant son heure de service).
* **`parseDateTime(...)` / `formatDateTime(...)`** : Des petits utilitaires pour transformer du texte (`"2026-03-21 08:00:00"`) en véritable objet "Temps" compréhensible par Java (et inversement).

### 💾 E. Sauvegarde et Base de données
* **`regenerateAndSavePlanification(...)`** : Supprime les anciennes planifications de la base de données pour la journée concernée, puis calcule et enregistre les nouvelles (pour éviter les doublons si on relance le bouton).
* **`save(...)`** : La méthode technique qui fait l'insertion (`INSERT INTO`) de chaque ligne de planification dans la table SQL.
* **`getReservationsSansVoiture(...)`** : Une méthode de vérification qui liste les clients qui n'ont pas pu avoir de voiture du tout à la fin de la journée.