# Résumé des Modifications : Planification des Navettes

## 1. Pourquoi avons-nous modifié le code ?
Dans l'ancienne version, l'algorithme était uniquement centré sur l'**heure d'arrivée des vols**. Les voitures attendaient l'arrivée du prochain groupe de passagers pour se remplir. 

**Le problème :** Si une voiture revenait à l'aéroport à 8h00, mais que le prochain vol n'était qu'à 10h00, les passagers non assignés du vol de 7h00 devaient attendre jusqu'à 10h00 ! 

## 2. La Nouvelle Règle de Gestion
Nous avons introduit une logique centrée sur la **disponibilité des véhicules**. 

Désormais, quand une voiture a terminé son circuit et revient à l'aéroport (ex: à 8h00), elle devient **active**. Elle va regarder en priorité la "salle d'attente" (les réservations non assignées) pour faire partir ces clients au plus vite.

### Les 4 principes clés de la mise à jour :
* **Priorité aux clients en attente :** La voiture de retour prend en priorité les réservations qui n'ont pas eu de place dans les trajets précédents.
* **Temps d'attente maximum de 30 minutes :** La voiture va regrouper les clients en attente avec les éventuels nouveaux vols qui atterrissent dans les 30 minutes suivant son retour (ex: entre 8h00 et 8h30).
* **Départ immédiat si plein :** Si la voiture remplit toutes ses places avant les 30 minutes, elle n'attend pas la fin du délai et part immédiatement.
* **Remplissage optimisé conservé :** La règle du "Tetris" ne change pas. On prend toujours le plus grand groupe de passagers possible, puis on bouche les trous avec les petits groupes (le "maximum inférieur").

## 3. Ce qui a été modifié dans le code Java

Pour intégrer cette nouvelle règle sans casser l'ancien système (comme le calcul des distances ou des vitesses), nous avons modifié le fichier `PlanificationService.java`.

La grande méthode `assignerVoitures` a été séparée en deux méthodes distinctes pour être plus propre et plus intelligente :

1. **La nouvelle méthode `assignerVoitures` (Le Chef d'Orchestre) :**
   * Au lieu de juste lire les vols les uns après les autres, elle utilise maintenant une **boucle temporelle intelligente**.
   * À chaque étape, elle se pose une question : *"Qu'est-ce qui arrive en premier ? Le prochain vol OU une voiture qui rentre de son trajet ?"*
   * Si c'est une voiture qui rentre, elle crée un "groupe virtuel" avec tous les passagers en attente et ceux qui arrivent dans les 30 prochaines minutes.

2. **La nouvelle méthode `traiterAssignationCandidats` (Le Placeur) :**
   * Nous avons extrait la logique complexe de "qui monte dans quelle voiture" dans cette nouvelle méthode.
   * Elle reçoit une liste de candidats (soit un vol normal, soit le fameux groupe virtuel de la voiture de retour).
   * Elle trie les réservations, choisit la meilleure voiture selon vos critères (capacité, trajets, diesel), la remplit, et calcule l'heure exacte de départ et de retour en utilisant la vitesse (40 km/h) et la distance totale du circuit.

## 4. Ce qui n'a PAS changé
* L'ordre de passage des hôtels (du plus proche au plus loin de proche en proche) est resté le même.
* Le calcul de l'heure de retour (qui multiplie bien la distance aller ET retour) fonctionne toujours parfaitement avec les paramètres de la base de données.