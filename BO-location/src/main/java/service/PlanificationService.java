package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Génère une planification pour une date donnée
     * @param date Date pour laquelle générer la planification
     * @return Liste des planifications (affectations voiture-réservation)
     * @throws SQLException
     */
    public List<Planification> getPlanification(LocalDate date) throws SQLException {
        // 1. Récupérer toutes les réservations du jour
        List<Reservation> reservations = getReservationsForDate(date);
        
        // 2. Récupérer toutes les voitures disponibles
        List<Voiture> voitures = voitureService.readAll();
        
        // 3. Trier les réservations selon les règles de priorité
        List<Reservation> reservationsSorted = trierReservations(reservations);
        
        // 4. Assigner les voitures aux réservations
        List<Planification> planifications = assignerVoitures(reservationsSorted, voitures);
        
        return planifications;
    }

    /**
     * Récupère toutes les réservations pour une date donnée
     */
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

    /**
     * Trie les réservations selon les règles de priorité:
     * 1. Nombre de passagers décroissant (les plus gros groupes d'abord)
     * 2. Distance croissante (les plus proches d'abord)
     */
    private List<Reservation> trierReservations(List<Reservation> reservations) {
        return reservations.stream()
            .sorted(Comparator
                .comparing(Reservation::getNombrePassager, Comparator.reverseOrder())
                .thenComparing(r -> r.getHotel().getDistanceAeroport()))
            .collect(Collectors.toList());
    }

    /**
     * Assigne les voitures aux réservations selon les règles:
     * - On ne sépare pas les passagers d'une réservation
     * - On assigne la voiture avec capacité la plus proche du nombre de passagers
     * - En cas d'égalité, on préfère le diesel
     * - En cas d'égalité diesel, on fait un choix aléatoire
     * - On peut combiner plusieurs réservations dans une même voiture
     */
    private List<Planification> assignerVoitures(List<Reservation> reservations, List<Voiture> voitures) {
        List<Planification> planifications = new ArrayList<>();
        
        // Carte pour suivre les capacités restantes des voitures
        Map<Voiture, Integer> capacitesRestantes = new HashMap<>();
        for (Voiture voiture : voitures) {
            capacitesRestantes.put(voiture, voiture.getCapacite());
        }
        
        // Parcourir les réservations triées
        for (Reservation reservation : reservations) {
            int nombrePassagers = reservation.getNombrePassager();
            
            // Chercher si on peut ajouter cette réservation à une voiture déjà utilisée
            Voiture voitureAssignee = null;
            
            // Option 1: Essayer de combiner avec une voiture déjà utilisée
            for (Map.Entry<Voiture, Integer> entry : capacitesRestantes.entrySet()) {
                Voiture voiture = entry.getKey();
                int capaciteRestante = entry.getValue();
                
                // Si la capacité restante peut accueillir cette réservation
                if (capaciteRestante >= nombrePassagers && capaciteRestante < voiture.getCapacite()) {
                    voitureAssignee = voiture;
                    break;
                }
            }
            
            // Option 2: Si pas de combinaison possible, assigner une nouvelle voiture
            if (voitureAssignee == null) {
                voitureAssignee = trouverMeilleureVoiture(nombrePassagers, capacitesRestantes);
            }
            
            // Créer la planification
            if (voitureAssignee != null) {
                Planification planification = new Planification(reservation, voitureAssignee);
                planifications.add(planification);
                
                // Mettre à jour la capacité restante
                int nouvelleCapacite = capacitesRestantes.get(voitureAssignee) - nombrePassagers;
                capacitesRestantes.put(voitureAssignee, nouvelleCapacite);
            }
        }
        
        return planifications;
    }

    /**
     * Trouve la meilleure voiture pour un nombre de passagers donné:
     * - Capacité suffisante (>= nombrePassagers)
     * - Capacité la plus proche du nombre de passagers
     * - En cas d'égalité: préférence diesel
     * - En cas d'égalité diesel: aléatoire
     */
    private Voiture trouverMeilleureVoiture(int nombrePassagers, Map<Voiture, Integer> capacitesRestantes) {
        List<Voiture> voituresCandidates = new ArrayList<>();
        
        // Filtrer les voitures avec capacité totale suffisante
        for (Map.Entry<Voiture, Integer> entry : capacitesRestantes.entrySet()) {
            Voiture voiture = entry.getKey();
            // On regarde la capacité totale, pas la capacité restante, pour une nouvelle affectation
            if (voiture.getCapacite() >= nombrePassagers && entry.getValue() == voiture.getCapacite()) {
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
        
        // Préférer les diesel
        List<Voiture> voituresDiesel = voituresCapaciteMin.stream()
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
        
        // Si aucune diesel, choisir aléatoirement parmi les candidates
        Random random = new Random();
        return voituresCapaciteMin.get(random.nextInt(voituresCapaciteMin.size()));
    }

    /**
     * Récupère les réservations sans voiture assignée pour une date donnée
     */
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

