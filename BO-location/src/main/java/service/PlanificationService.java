package service;

import java.sql.Connection;
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

import database.ConnexDB;

import model.Planification;
import model.Reservation;
import model.Voiture;

public class PlanificationService {
    private final ReservationService reservationService = new ReservationService();
    private final VoitureService voitureService = new VoitureService();
    private final ParametreService parametreService = new ParametreService();
    private final DistanceService distanceService = new DistanceService();

    private static class EtatVoiture {
        int capaciteRestante;
        List<LocalDateTime> horairesPlanifies;

        public EtatVoiture(int capaciteTotale) {
            this.capaciteRestante = capaciteTotale;
            this.horairesPlanifies = new ArrayList<>();
        }

        public boolean estHoraireCompatible(LocalDateTime nouvelHoraire) {
            // Une voiture n'est disponible pour un nouveau groupe que si elle n'a pas encore
            // été utilisée dans la journée.
            return horairesPlanifies.isEmpty();
        }

        public boolean peutCombinerAvec(LocalDateTime horaire, int nombrePassagers) {
            return capaciteRestante >= nombrePassagers;
        }

        public boolean peutAccepterReservation(LocalDateTime horaire, int nombrePassagers) {
            return capaciteRestante >= nombrePassagers;
        }

        public void assignerReservation(LocalDateTime horaire, int nombrePassagers) {
            capaciteRestante -= nombrePassagers;
            horairesPlanifies.add(horaire);
        }
    }

    public List<Planification> getPlanification(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);

        List<Voiture> voitures = voitureService.readAll();

        List<Reservation> reservationsTries = triReservationParHeureArrivee(reservations);

        List<List<Reservation>> groupes = regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(),
                reservationsTries);
                
        for (List<Reservation> groupe : groupes) {
            //comparer par distance aéroport
            groupe.sort(Comparator.comparing(this::getDistanceAeroportAllerRetourPourTri));
        }

        List<Planification> planifications = assignerVoitures(groupes, voitures);
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
                        .comparing(this::getDistanceAeroportAllerRetourPourTri)
                        .thenComparing((Reservation r) -> parseDateTime(r.getDateHeureArrivee()))
                        .thenComparing(Reservation::getNombrePassager, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private double getDistanceAeroportAllerRetourPourTri(Reservation reservation) {
        if (reservation == null || reservation.getHotel() == null || reservation.getHotel().getIdLieu() == null) {
            return Double.MAX_VALUE;
        }

        try {
            Double distanceSimple = distanceService.getDistanceDepuisAeroport(reservation.getHotel().getIdLieu());
            if (distanceSimple == null) {
                return Double.MAX_VALUE;
            }
            return distanceSimple * 2.0;
        } catch (SQLException e) {
            return Double.MAX_VALUE;
        }
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
        * Assigne les voitures par groupe (bin packing simple):
        * - On choisit une voiture pour une réservation non affectée
        * - On remplit cette voiture avec d'autres réservations du même groupe tant qu'il reste de la capacité
        * - Puis on passe à une nouvelle voiture
        * - L'heure de départ de tout le groupe est l'heure max des réservations du groupe
        */
    private List<Planification> assignerVoitures(List<List<Reservation>> groupes, List<Voiture> voitures) {
        List<Planification> planifications = new ArrayList<>();

        // Carte pour suivre l'état de chaque voiture
        Map<Voiture, EtatVoiture> etatsVoitures = new HashMap<>();
        for (Voiture voiture : voitures) {
            etatsVoitures.put(voiture, new EtatVoiture(voiture.getCapacite()));
        }

        // Parcourir chaque groupe de réservations
        for (List<Reservation> reservations : groupes) {
            LocalDateTime heureDepartGroupe = reservations.stream()
                    .map(r -> parseDateTime(r.getDateHeureArrivee()))
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
            String heureDepartGroupeStr = formatDateTime(heureDepartGroupe);

            // Bin packing: travailler d'abord avec les grosses reservations
            List<Reservation> reservationsNonAffectees = new ArrayList<>(reservations);
            reservationsNonAffectees.sort(Comparator.comparing(Reservation::getNombrePassager).reversed());

            while (!reservationsNonAffectees.isEmpty()) {
                Reservation reservationPrincipale = reservationsNonAffectees.remove(0);
                int nombrePassagers = reservationPrincipale.getNombrePassager();
                LocalDateTime horaireReservation = parseDateTime(reservationPrincipale.getDateHeureArrivee());

                Voiture voitureAssignee = trouverMeilleureVoiture(nombrePassagers, horaireReservation, etatsVoitures);
                if (voitureAssignee == null) {
                    continue;
                }

                EtatVoiture etat = etatsVoitures.get(voitureAssignee);

                try {
                    Planification planification = new Planification(
                            reservationPrincipale,
                            voitureAssignee,
                            heureDepartGroupeStr,
                            distanceService.getDistanceDepuisAeroport(reservationPrincipale.getHotel().getIdLieu())
                    );
                    planifications.add(planification);
                    etat.assignerReservation(horaireReservation, nombrePassagers);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Une fois la voiture choisie, on tente de la remplir au maximum
                int i = 0;
                while (i < reservationsNonAffectees.size()) {
                    Reservation resaSuivante = reservationsNonAffectees.get(i);
                    int nbPassagersSuivant = resaSuivante.getNombrePassager();
                    LocalDateTime horaireSuivant = parseDateTime(resaSuivante.getDateHeureArrivee());

                    if (etat.peutAccepterReservation(horaireSuivant, nbPassagersSuivant)) {
                        try {
                            Planification planifSuivante = new Planification(
                                    resaSuivante,
                                    voitureAssignee,
                                    heureDepartGroupeStr,
                                    distanceService.getDistanceDepuisAeroport(resaSuivante.getHotel().getIdLieu())
                            );
                            planifications.add(planifSuivante);
                            etat.assignerReservation(horaireSuivant, nbPassagersSuivant);
                            reservationsNonAffectees.remove(i);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            i++;
                        }
                    } else {
                        i++;
                    }
                }
            }
        }

        return planifications;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Voiture trouverMeilleureVoiture(int nombrePassagers, LocalDateTime horaire,
            Map<Voiture, EtatVoiture> etatsVoitures) {
        List<Voiture> voituresCandidates = new ArrayList<>();

        for (Map.Entry<Voiture, EtatVoiture> entry : etatsVoitures.entrySet()) {
            Voiture voiture = entry.getKey();
            EtatVoiture etat = entry.getValue();

            // Vérifier que la voiture a la capacité totale disponible (pas encore utilisée
            // pour ce créneau)
            // ET que l'horaire est compatible avec les autres réservations de cette voiture
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

    public List<Reservation> getReservationsSansVoiture(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);
        List<Voiture> voitures = voitureService.readAll();
        List<Planification> planifications = assignerVoitures(
                regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(), reservations), voitures);

        // Identifier les réservations non planifiées
        List<Integer> resaIdsPlanifiees = planifications.stream()
                .map(Planification::getResaId)
                .collect(Collectors.toList());

        return reservations.stream()
                .filter(r -> !resaIdsPlanifiees.contains(r.getId()))
                .collect(Collectors.toList());
    }

    public Planification save(Planification planification) throws SQLException {
        Connection conn = ConnexDB.getConnection();
        try {
            String sql = "INSERT INTO planification (reservation_id, voiture_id, date_heure, distance_aeroport, date_heure_depart) VALUES (?, ?, ?, ?, ?)";
            var pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, planification.getResaId());
            pstmt.setInt(2, planification.getVoitureId());
            pstmt.setString(3, planification.getDateHeure());
            pstmt.setDouble(4, planification.getDistance());
            pstmt.setString(5, planification.getDateHeureDepart());
            pstmt.executeUpdate();

            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                planification.setId(id);
                System.out.println("Planification saved with ID: " + id);
                return planification;
            } else {
                throw new SQLException("Failed to retrieve generated ID for Planification");
            }
        } finally {
            conn.close();
        }
    }
}
