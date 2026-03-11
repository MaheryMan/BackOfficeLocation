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


    private static class EtatVoiture {
        int capaciteRestante;
        int capaciteTotale;
        LocalDateTime horaireUtilisation; // Horaire unique d'utilisation de la voiture pour aujourd'hui
        
        public EtatVoiture(int capaciteTotale) {
            this.capaciteTotale = capaciteTotale;
            this.capaciteRestante = capaciteTotale;
            this.horaireUtilisation = null; // Voiture libres au départ
        }

        /**
         * Vérifie si la voiture peut être utilisée pour un nouvel horaire
         * Règle: une voiture utilisée à un horaire ne peut être réutilisée que si c'est LE MÊME horaire
         */
        public boolean estDisponiblePour(LocalDateTime horaire) {
            if (horaireUtilisation == null) {
                // Voiture jamais utilisée
                return true;
            }
            
            // Voiture déjà utilisée: vérifier que c'est exactement le MÊME horaire (heure ET minute)
            return horaireUtilisation.getHour() == horaire.getHour() && 
                   horaireUtilisation.getMinute() == horaire.getMinute();
        }
        
        /**
         * Vérifie si on peut combiner une nouvelle réservation pour le même horaire
         * (seulement si l'horaire est identique et il y a de la capacité)
         */
        public boolean peutAccepterReservation(LocalDateTime horaire, int nombrePassagers) {
            // La voiture doit être disponible pour cet horaire
            if (!estDisponiblePour(horaire)) {
                return false;
            }
            
            // Si c'est la première utilisation ou même horaire, vérifier la capacité
            return capaciteRestante >= nombrePassagers;
        }
        
        /**
         * Assigne une réservation à cette voiture et met à jour l'état
         */
        public void assignerReservation(LocalDateTime horaire, int nombrePassagers) {
            this.horaireUtilisation = horaire;
            this.capaciteRestante -= nombrePassagers;
        }
    }

    public List<Planification> getPlanification(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);

        List<Voiture> voitures = voitureService.readAll();
      
        List<Reservation> reservationsSorted = trierReservations(reservations);
    
        List<Planification> planifications = assignerVoitures(reservationsSorted, voitures);
        
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


    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {

            String normalized = dateTimeStr.replace("T", " ");
            if (normalized.length() == 16) {
                normalized = normalized + ":00";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(normalized, formatter);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * Assigne les voitures aux réservations selon les règles:
     * - On ne sépare pas les passagers d'une réservation
     * - On assigne la voiture avec capacité la plus proche du nombre de passagers
     * - En cas d'égalité, on préfère le diesel
     * - En cas d'égalité diesel, on fait un choix aléatoire
     * - On peut combiner plusieurs réservations dans une même voiture uniquement SI:
     *   * C'est la même heure (différence < 5 minutes)
     *   * Une fois assignée, la voiture n'est disponible que le lendemain
     */
    private List<Planification> assignerVoitures(List<Reservation> reservations, List<Voiture> voitures) {
        List<Planification> planifications = new ArrayList<>();
        
        // Carte pour suivre l'état de chaque voiture
        Map<Voiture, EtatVoiture> etatsVoitures = new HashMap<>();
        for (Voiture voiture : voitures) {
            etatsVoitures.put(voiture, new EtatVoiture(voiture.getCapacite()));
        }
        
        // Parcourir les réservations triées
        for (Reservation reservation : reservations) {
            int nombrePassagers = reservation.getNombrePassager();
            LocalDateTime horaireReservation = parseDateTime(reservation.getDateHeureArrivee());
            
            Voiture voitureAssignee = null;
            
            // Chercher d'abord la meilleure voiture (neuve ou existante au même horaire)
            // Priorité: capacité la plus proche du nombre de passagers
            voitureAssignee = trouverMeilleureVoiture(nombrePassagers, horaireReservation, etatsVoitures);
            
            // Créer la planification
            if (voitureAssignee != null) {
                Planification planification = new Planification(reservation, voitureAssignee);
                planifications.add(planification);
                
                // Mettre à jour l'état de la voiture
                EtatVoiture etat = etatsVoitures.get(voitureAssignee);
                etat.assignerReservation(horaireReservation, nombrePassagers);
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
            
            // Critères de sélection:
            // 1. La voiture a la capacité suffisante
            // 2. La voiture peut accepter la réservation (jamais utilisée OU même horaire avec capacité restante)
            if (voiture.getCapacite() >= nombrePassagers && 
                etat.peutAccepterReservation(horaire, nombrePassagers)) {
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
        
        // Prioriser les voitures DÉJÀ UTILISÉES (capacité restante)
        // plutôt que les voitures neuves pour ne pas gaspiller de véhicule
        List<Voiture> voituresDejaUtilisees = new ArrayList<>();
        List<Voiture> voituresNeuves = new ArrayList<>();
        
        for (Voiture v : voituresCapaciteMin) {
            if (etatsVoitures.get(v).horaireUtilisation != null) {
                voituresDejaUtilisees.add(v);
            } else {
                voituresNeuves.add(v);
            }
        }
        
        // Utiliser les voitures déjà utilisées en priorité
        List<Voiture> voituresPrioritaires = voituresDejaUtilisees.isEmpty() ? voituresNeuves : voituresDejaUtilisees;
        
        // Parmi les prioritaires, préférer les diesel
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
        List<Planification> planifications = assignerVoitures(trierReservations(reservations), voitures);
        
        // Identifier les réservations non planifiées
        List<Integer> resaIdsPlanifiees = planifications.stream()
            .map(Planification::getResaId)
            .collect(Collectors.toList());
        
        return reservations.stream()
            .filter(r -> !resaIdsPlanifiees.contains(r.getId()))
            .collect(Collectors.toList());
    }
}
