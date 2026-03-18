# Ordre de Priorite - Planification et Assignation de Voitures

Ce document decrit l'ordre de priorite applique dans la logique actuelle de planification.

## 1. Priorite Globale de Planification

1. Recuperer toutes les reservations de la date demandee.
2. Trier les reservations par heure d'arrivee croissante.
3. Regrouper les reservations par fenetre de temps d'attente (`temps_attente`).
4. A l'interieur de chaque groupe, trier par distance aeroport aller-retour (du plus proche au plus loin).
5. Assigner les voitures groupe par groupe.

## 2. Priorite d'Assignation des Voitures (Par Groupe)

1. Calculer l'heure de depart du groupe = heure d'arrivee maximale du groupe.
2. Prendre les reservations non affectees du groupe.
3. Trier ces reservations par nombre de passagers decroissant (les plus grandes d'abord).
4. Pour chaque reservation principale:
   - Chercher la meilleure voiture avec `trouverMeilleureVoiture(...)`.
   - Si aucune voiture n'est disponible, la reservation reste non planifiee.

## 3. Regles de Selection de la Meilleure Voiture

Une voiture est candidate si:

1. Sa capacite >= nombre de passagers de la reservation principale.
2. Elle est disponible a l'heure de depart du groupe (`heure_depart_groupe >= date_heure_retour` de son dernier trajet).

Puis application des priorites:

1. Capacite minimale suffisante (best-fit): on choisit les plus petites voitures qui conviennent.
2. Parmi elles, priorite aux voitures avec le moins de trajets deja effectues (`nbtrajet` minimal).
3. Ensuite seulement, preference diesel.
4. En cas d'egalite finale, choix aleatoire.

## 4. Remplissage d'une Voiture Deja Choisie

Apres avoir choisi une voiture pour la reservation principale:

1. On ajoute d'autres reservations du meme groupe tant que la capacite restante le permet.
2. Chaque reservation ajoutee diminue la capacite restante de la voiture.
3. Toutes ces reservations partagent la meme heure de depart groupe.
4. A la fin du circuit complet, la voiture recupere sa capacite totale pour un futur trajet.

## 5. Ordre de Passage des Hotels (Dans une Meme Voiture/Meme Groupe)

L'ordre est calcule en mode "proche en proche" (nearest-neighbor):

1. Depart depuis l'aeroport.
2. Aller d'abord a l'hotel le plus proche de l'aeroport parmi les hotels restants.
3. Ensuite, depuis l'hotel courant, aller a l'hotel restant le plus proche.
4. Refaire jusqu'a traiter toutes les reservations de cette voiture dans ce groupe.
5. Retour final vers l'aeroport.

## 6. Distances Calculees et Affichees

Pour chaque ligne de planification:

1. `distance` = distance du segment reel precedent -> hotel courant:
   - premier hotel: aeroport -> hotel1
   - suivants: hotel precedent -> hotel courant
2. `distanceAeroportHotel` = distance aeroport -> hotel de la reservation (information contextuelle).
3. `hotelPrecedent` = hotel visite juste avant (ou "Aeroport" pour la premiere reservation du trajet).

## 7. Heure de Retour

Pour une meme voiture dans un meme groupe:

1. Une seule heure de retour commune est calculee.
2. Base de calcul = heure de depart groupe.
3. Duree ajoutee = distance totale du circuit / vitesse moyenne (`vitesse_moyenne`).
4. Circuit total = aeroport -> hotels selon l'ordre proche en proche -> aeroport.
5. La voiture peut etre reutilisee pour un autre groupe seulement apres cette heure de retour.

## 8. Persistance en Base (Selection de Date)

Quand une date est selectionnee:

1. Les planifications sont recalculees pour cette date.
2. Les anciennes planifications de cette date sont supprimees en base.
3. Les nouvelles planifications sont inserees en base.
4. `nbtrajet` est enregistre pour chaque ligne de planification.

## 9. Reservations Sans Voiture

Une reservation est consideree "sans voiture" si son ID n'apparait dans aucune planification generee.
