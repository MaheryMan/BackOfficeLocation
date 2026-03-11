package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import model.Planification;
import model.Reservation;
import model.Voiture;

public class PlanificationService {
    private final ReservationService reservationService = new ReservationService();
    private final VoitureService voitureService = new VoitureService();
    private final ParametreService parametreService = new ParametreService();

    private static class EtatVoiture {
        int capaciteRestante;
        int capaciteTotale;
        List<LocalDateTime> horairesPlanifies;

        public EtatVoiture(int capaciteTotale) {
            this.capaciteTotale = capaciteTotale;
            this.capaciteRestante = capaciteTotale;
            this.horairesPlanifies = new ArrayList<>();
        }

        public boolean estHoraireCompatible(LocalDateTime nouvelHoraire) {
            if (horairesPlanifies.isEmpty()) {
                return true;
            }

            for (LocalDateTime horairePlanifie : horairesPlanifies) {
                // Vérification stricte : même heure ET même minute
                if (horairePlanifie.getHour() == nouvelHoraire.getHour() &&
                        horairePlanifie.getMinute() == nouvelHoraire.getMinute()) {
                    // C'est exactement la même heure, compatible
                    continue;
                } else {
                    // Horaire différent : la voiture n'est pas disponible (elle ne se libère que le
                    // lendemain)
                    return false;
                }
            }

            return true;
        }

        public boolean peutCombinerAvec(LocalDateTime horaire, int nombrePassagers) {
            // Vérifier si c'est exactement le même horaire (heure ET minute)
            for (LocalDateTime horairePlanifie : horairesPlanifies) {
                // Vérification stricte : même heure ET même minute
                if (horairePlanifie.getHour() == horaire.getHour() &&
                        horairePlanifie.getMinute() == horaire.getMinute()) {
                    // Vérifier la capacité
                    return capaciteRestante >= nombrePassagers;
                }
            }

            return false;
        }
        
        public boolean estUtilisee() {
            return !horairesPlanifies.isEmpty();
        }
    }

    public List<Planification> getPlanification(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);
        System.out.println("=== DEBUT getPlanification ===");
        System.out.println("Réservations trouvées: " + reservations.size());

        List<Voiture> voitures = voitureService.readAll();

        List<Reservation> reservationsTries = triReservationParHeureArrivee(reservations);

        List<List<Reservation>> groupes = regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(),
                reservationsTries);
        
        System.out.println("Nombre de groupes: " + groupes.size());
        for (int i = 0; i < groupes.size(); i++) {
            System.out.println("Groupe " + (i+1) + ": " + groupes.get(i).size() + " réservations");
            for (Reservation r : groupes.get(i)) {
                System.out.println("  - Res ID " + r.getId() + " Client " + r.getClient().getNom() + " à " + r.getDateHeureArrivee());
            }
        }

        List<Planification> planifications = assignerVoitures(groupes, voitures);
        System.out.println("Planifications créées: " + planifications.size());
        System.out.println("=== FIN getPlanification ===");
        
        return planifications;
    }

    private List<Reservation> getReservationsForDate(LocalDate date) throws SQLException {
        List<Reservation> allReservations = reservationService.readAll();
        List<Reservation> reservationsFiltered = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = date.format(formatter);

        for (Reservation reservation : allReservations) {
            if (reservation.getDateHeureArrivee() != null &&
                    reservation.getDateHeureArrivee().startsWith(dateStr)) {
                reservationsFiltered.add(reservation);
            }
        }

        return reservationsFiltered;
    }

    // Raha anova ordre de priorite dia ito fostiny no ampifamadihana

    private List<Reservation> trierReservations(List<Reservation> reservations) {
        return reservations.stream()
                .sorted(Comparator
                        .comparing((Reservation r) -> r.getHotel().getDistanceAeroport())
                        .thenComparing((Reservation r) -> parseDateTime(r.getDateHeureArrivee()))
                        .thenComparing(Reservation::getNombrePassager, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<Reservation> triReservationParHeureArrivee(List<Reservation> reservations) {
        return reservations.stream()
                .sorted(Comparator.comparing(r -> parseDateTime(r.getDateHeureArrivee())))
                .collect(Collectors.toList());
    }

    public List<List<Reservation>> regrouperParTempsAttente(int tempsAttenteMinutes, List<Reservation> reservations) {
        List<List<Reservation>> groupes = new ArrayList<>();

        for (Reservation reservation : reservations) {
            LocalDateTime horaireArrivee = parseDateTime(reservation.getDateHeureArrivee());
            System.out.println("Réservation: " + reservation.getDateHeureArrivee() + 
                               " → parsée à: " + horaireArrivee);
            boolean ajoute = false;

            for (List<Reservation> groupe : groupes) {
                LocalDateTime horaireGroupe = parseDateTime(groupe.get(0).getDateHeureArrivee());
                long minutesDiff = ChronoUnit.MINUTES.between(horaireGroupe, horaireArrivee);

                if (Math.abs(minutesDiff) <= tempsAttenteMinutes) {
                    groupe.add(reservation);
                    ajoute = true;
                    break;
                }
            }

            if (!ajoute) {
                List<Reservation> nouveauGroupe = new ArrayList<>();
                nouveauGroupe.add(reservation);
                groupes.add(nouveauGroupe);
            }
        }

        return groupes;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {

            String normalized = dateTimeStr.replace("T", " ");
            // Supprimer les décimales (ex: "2026-03-04 08:00:00.0" → "2026-03-04 08:00:00")
            if (normalized.contains(".")) {
                normalized = normalized.substring(0, normalized.indexOf("."));
            }
            if (normalized.length() == 16) {
                normalized = normalized + ":00";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(normalized, formatter);
        } catch (Exception e) {
            System.err.println("Erreur parsing: " + dateTimeStr + " - " + e.getMessage());
            return LocalDateTime.now();
        }
    }

    /**
     * Assigne les voitures aux réservations selon les règles:
     * - On ne sépare pas les passagers d'une réservation
     * - On assigne la voiture avec capacité la plus proche du nombre de passagers
     * - En cas d'égalité, on préfère le diesel
     * - En cas d'égalité diesel, on fait un choix aléatoire
     * - On peut combiner plusieurs réservations dans une même voiture uniquement
     * SI:
     * * C'est la même heure (différence < 5 minutes)
     * * Une fois assignée, la voiture n'est disponible que le lendemain
     */
    private List<Planification> assignerVoitures(List<List<Reservation>> groupes, List<Voiture> voitures) {
        List<Planification> planifications = new ArrayList<>();

        // Carte pour suivre l'état de chaque voiture GLOBAL (toute la journée)
        Map<Voiture, EtatVoiture> etatsVoitures = new HashMap<>();
        for (Voiture voiture : voitures) {
            etatsVoitures.put(voiture, new EtatVoiture(voiture.getCapacite()));
        }

        // Parcourir les groupes de réservations (fenêtre de temps d'attente)
        for (List<Reservation> reservations : groupes) {
            Reservation dernierVol = reservations.get(reservations.size() - 1);
            
            // NOUVELLE LOGIQUE: Trier par nombre de passagers DÉCROISSANT
            List<Reservation> reservationsTriees = reservations.stream()
                    .sorted(Comparator.comparing(Reservation::getNombrePassager).reversed())
                    .collect(Collectors.toList());
            
            // Voitures utilisées dans CE GROUPE (peuvent être combinées)
            Map<Voiture, Integer> voituresDuGroupe = new HashMap<>();
            
            // Liste des réservations non assignées (skippées)
            List<Reservation> reservationsNonAssignees = new ArrayList<>();
            
            // PHASE 1: Essayer d'assigner dans les voitures existantes du groupe
            for (Reservation reservation : reservationsTriees) {
                int nombrePassagers = reservation.getNombrePassager();
                LocalDateTime horaireReservation = parseDateTime(reservation.getDateHeureArrivee());

                Voiture voitureAssignee = null;

                // Si aucune voiture du groupe n'existe encore, chercher une nouvelle
                if (voituresDuGroupe.isEmpty()) {
                    voitureAssignee = trouverMeilleureVoiture(nombrePassagers, horaireReservation, etatsVoitures);
                    
                    if (voitureAssignee != null) {
                        voituresDuGroupe.put(voitureAssignee, 1);
                    }
                } else {
                    // Essayer de mettre dans les voitures DÉJÀ utilisées du groupe
                    for (Map.Entry<Voiture, Integer> entry : voituresDuGroupe.entrySet()) {
                        Voiture voiture = entry.getKey();
                        EtatVoiture etat = etatsVoitures.get(voiture);

                        // Vérifier seulement la capacité (même groupe = même départ)
                        if (etat.capaciteRestante >= nombrePassagers) {
                            voitureAssignee = voiture;
                            break;
                        }
                    }
                    
                    // Si aucune voiture existante ne peut contenir → SKIP pour l'instant
                    if (voitureAssignee == null) {
                        reservationsNonAssignees.add(reservation);
                        continue;
                    }
                }

                // Créer la planification si assignée
                if (voitureAssignee != null) {
                    Planification planification = new Planification(reservation, voitureAssignee,
                            dernierVol.getDateHeureArrivee());
                    planifications.add(planification);

                    // Mettre à jour l'état de la voiture
                    EtatVoiture etat = etatsVoitures.get(voitureAssignee);
                    etat.capaciteRestante -= nombrePassagers;
                    etat.horairesPlanifies.add(horaireReservation);
                }
            }
            
            // PHASE 2: Assigner les réservations skippées avec de nouvelles voitures
            for (Reservation reservation : reservationsNonAssignees) {
                int nombrePassagers = reservation.getNombrePassager();
                LocalDateTime horaireReservation = parseDateTime(reservation.getDateHeureArrivee());

                Voiture voitureAssignee = trouverMeilleureVoiture(nombrePassagers, horaireReservation, etatsVoitures);
                
                if (voitureAssignee != null) {
                    voituresDuGroupe.put(voitureAssignee, 1);
                    
                    Planification planification = new Planification(reservation, voitureAssignee,
                            dernierVol.getDateHeureArrivee());
                    planifications.add(planification);

                    // Mettre à jour l'état de la voiture
                    EtatVoiture etat = etatsVoitures.get(voitureAssignee);
                    etat.capaciteRestante -= nombrePassagers;
                    etat.horairesPlanifies.add(horaireReservation);
                }
            }
        }

        return planifications;
    }

    private Voiture trouverMeilleureVoiture(int nombrePassagers, LocalDateTime horaire,
            Map<Voiture, EtatVoiture> etatsVoitures) {
        List<Voiture> voituresCandidates = new ArrayList<>();

        for (Map.Entry<Voiture, EtatVoiture> entry : etatsVoitures.entrySet()) {
            Voiture voiture = entry.getKey();
            EtatVoiture etat = entry.getValue();

            // UNE VOITURE NE PEUT ÊTRE SÉLECTIONNÉE COMME NOUVELLE QUE SI:
            // 1. Elle a la capacité suffisante
            // 2. L'horaire est compatible (pas utilisée OU même horaire)
            // 3. Elle a sa capacité TOTALE disponible (jamais utilisée pour ce créneau)
            if (voiture.getCapacite() >= nombrePassagers &&
                    etat.estHoraireCompatible(horaire) &&
                    etat.capaciteRestante == voiture.getCapacite()) {
                voituresCandidates.add(voiture);
            }
        }

        if (voituresCandidates.isEmpty()) {
            return null;
        }

        // Trier par capacité croissante (pour avoir la plus proche)
        voituresCandidates.sort(Comparator.comparing(Voiture::getCapacite));

        // Trouver la capacité minimale qui convient
        int capaciteMin = voituresCandidates.get(0).getCapacite();

        // Filtrer pour ne garder que celles avec la capacité minimale
        List<Voiture> voituresCapaciteMin = voituresCandidates.stream()
                .filter(v -> v.getCapacite() == capaciteMin)
                .collect(Collectors.toList());

        // Si une seule voiture, la retourner
        if (voituresCapaciteMin.size() == 1) {
            return voituresCapaciteMin.get(0);
        }

        // Prioriser les voitures DÉJÀ UTILISÉES pour ne pas gaspiller de véhicule
        List<Voiture> voituresDejaUtilisees = new ArrayList<>();
        List<Voiture> voituresNeuves = new ArrayList<>();
        
        for (Voiture v : voituresCapaciteMin) {
            if (etatsVoitures.get(v).estUtilisee()) {
                voituresDejaUtilisees.add(v);
            } else {
                voituresNeuves.add(v);
            }
        }
        
        List<Voiture> voituresPrioritaires = voituresDejaUtilisees.isEmpty() ? voituresNeuves : voituresDejaUtilisees;

        // Préférer les diesel parmi les prioritaires
        List<Voiture> voituresDiesel = voituresPrioritaires.stream()
                .filter(Voiture::estDiesel)
                .collect(Collectors.toList());

        if (!voituresDiesel.isEmpty()) {
            // Si toutes sont diesel ou plusieurs diesel, choisir aléatoirement
            if (voituresDiesel.size() == 1) {
                return voituresDiesel.get(0);
            } else {
                Random random = new Random();
                return voituresDiesel.get(random.nextInt(voituresDiesel.size()));
            }
        }

        // Si aucune diesel, choisir aléatoirement parmi les prioritaires
        Random random = new Random();
        return voituresPrioritaires.get(random.nextInt(voituresPrioritaires.size()));
    }

    public List<Reservation> getReservationsSansVoiture(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);
        List<Voiture> voitures = voitureService.readAll();
        
        // IMPORTANT: Trier AVANT de regrouper pour avoir les mêmes groupes
        List<Reservation> reservationsTries = triReservationParHeureArrivee(reservations);
        
        List<Planification> planifications = assignerVoitures(
                regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(), reservationsTries), voitures);

        // Identifier les réservations non planifiées
        List<Integer> resaIdsPlanifiees = planifications.stream()
                .map(Planification::getResaId)
                .collect(Collectors.toList());

        return reservations.stream()
                .filter(r -> !resaIdsPlanifiees.contains(r.getId()))
                .collect(Collectors.toList());
    }

}
