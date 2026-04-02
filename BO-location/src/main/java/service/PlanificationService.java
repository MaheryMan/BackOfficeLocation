package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import database.ConnexDB;
import model.Planification;
import model.Reservation;
import model.Voiture;

public class PlanificationService {
    private static final int AEROPORT_LIEU_ID = 1;

    private final ReservationService reservationService = new ReservationService();
    private final VoitureService voitureService = new VoitureService();
    private final ParametreService parametreService = new ParametreService();
    private final DistanceService distanceService = new DistanceService();

    private static class EtatVoiture {
        int nombreTrajets;
        LocalDateTime disponibleAPartir;

        public EtatVoiture() {
            this.nombreTrajets = 0;
            this.disponibleAPartir = LocalDateTime.MIN;
        }

        public boolean estDisponible(LocalDateTime heureDepart) {
            return !disponibleAPartir.isAfter(heureDepart);
        }

        public int getNombreTrajets() {
            return nombreTrajets;
        }

        public void enregistrerTrajet(LocalDateTime dateHeureRetour) {
            this.nombreTrajets += 1;
            this.disponibleAPartir = dateHeureRetour;
        }
    }

    private static class AffectationPartielle {
        Reservation reservation;
        int passagersAffectes;

        AffectationPartielle(Reservation reservation, int passagersAffectes) {
            this.reservation = reservation;
            this.passagersAffectes = passagersAffectes;
        }
    }

    public List<Planification> getPlanification(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);
        List<Voiture> voitures = voitureService.readAll();
        List<Reservation> reservationsTries = triReservationParHeureArrivee(reservations);

        List<List<Reservation>> groupes = regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(),
                reservationsTries);
                
        for (List<Reservation> groupe : groupes) {
            groupe.sort(Comparator.comparing(this::getDistanceAeroportAllerRetourPourTri));
        }

        return assignerVoitures(groupes, voitures);
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

    private List<Planification> assignerVoitures(List<List<Reservation>> groupes, List<Voiture> voitures) {
        List<Planification> planifications = new ArrayList<>();
        var parametre = parametreService.getParametre();
        Double vitesseMoyenne = parametre != null ? parametre.getVitesseMoyenne() : null;
        int tempsAttenteRetour = 30;

        Map<Voiture, EtatVoiture> etatsVoitures = new HashMap<>();
        for (Voiture voiture : voitures) {
            etatsVoitures.put(voiture, new EtatVoiture());
        }

        List<Reservation> reliquats = new ArrayList<>();
        int indexGroupe = 0;

        while (indexGroupe < groupes.size() || !reliquats.isEmpty()) {
            
            LocalDateTime nextGroupTime = null;
            if (indexGroupe < groupes.size()) {
                nextGroupTime = parseDateTime(groupes.get(indexGroupe).get(0).getDateHeureArrivee());
            }

            LocalDateTime earliestReturnTime = null;
            if (!reliquats.isEmpty()) {
                for (Map.Entry<Voiture, EtatVoiture> entry : etatsVoitures.entrySet()) {
                    if (entry.getValue().getNombreTrajets() > 0) {
                        LocalDateTime disp = calculerDisponibiliteVoitureDepuis(entry.getKey(), entry.getValue(), LocalDateTime.MIN);
                        if (earliestReturnTime == null || disp.isBefore(earliestReturnTime)) {
                            earliestReturnTime = disp;
                        }
                    }
                }
            }

            boolean processReturningCar = false;
            if (earliestReturnTime != null) {
                if (nextGroupTime == null) {
                    processReturningCar = true;
                } else if (!earliestReturnTime.isAfter(nextGroupTime)) {
                    processReturningCar = true; 
                }
            }

            if (!processReturningCar && indexGroupe >= groupes.size()) {
                break; 
            }

            if (processReturningCar) {
                LocalDateTime limiteAttente = earliestReturnTime.plusMinutes(tempsAttenteRetour);

                List<Reservation> prioritaires = new ArrayList<>(reliquats);
                prioritaires = fusionnerReservationsParId(prioritaires);
                reliquats.clear();

                List<Reservation> normaux = new ArrayList<>();
                while (indexGroupe < groupes.size()) {
                    List<Reservation> nextGrp = groupes.get(indexGroupe);
                    LocalDateTime grpArrival = parseDateTime(nextGrp.get(0).getDateHeureArrivee());
                    if (!grpArrival.isAfter(limiteAttente)) {
                        normaux.addAll(nextGrp);
                        indexGroupe++; 
                    } else {
                        break;
                    }
                }
                normaux = fusionnerReservationsParId(normaux);

                reliquats.addAll(traiterAssignationCandidats(prioritaires, normaux, etatsVoitures, planifications, vitesseMoyenne, limiteAttente));

            } else {
                List<Reservation> prioritaires = new ArrayList<>();
                List<Reservation> normaux = new ArrayList<>(groupes.get(indexGroupe));
                normaux = fusionnerReservationsParId(normaux);

                LocalDateTime heureDepartGroupe = normaux.stream()
                        .map(r -> parseDateTime(r.getDateHeureArrivee()))
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());

                reliquats.addAll(traiterAssignationCandidats(prioritaires, normaux, etatsVoitures, planifications, vitesseMoyenne, heureDepartGroupe));
                
                indexGroupe++;
            }
        }

        return planifications;
    }

    private List<Reservation> traiterAssignationCandidats(
            List<Reservation> prioritaires,
            List<Reservation> normaux,
            Map<Voiture, EtatVoiture> etatsVoitures,
            List<Planification> planifications,
            Double vitesseMoyenne,
            LocalDateTime limiteTempsRecherche) {

        List<Reservation> nonAssignes = new ArrayList<>();
        
        Set<Integer> idsPrioritaires = new HashSet<>();
        if (prioritaires != null) {
            for (Reservation p : prioritaires) {
                if (p.getId() != null) idsPrioritaires.add(p.getId());
            }
        }

        List<Reservation> prioritairesOrdonnees = new ArrayList<>(prioritaires != null ? prioritaires : new ArrayList<>());
        prioritairesOrdonnees.sort(Comparator.comparing(Reservation::getNombrePassager).reversed());

        List<Reservation> normauxOrdonnees = new ArrayList<>(normaux != null ? normaux : new ArrayList<>());
        normauxOrdonnees.sort(Comparator.comparing(Reservation::getNombrePassager).reversed());

        List<Reservation> reservationsOrdonnees = new ArrayList<>();
        reservationsOrdonnees.addAll(prioritairesOrdonnees);
        reservationsOrdonnees.addAll(normauxOrdonnees);

        if (reservationsOrdonnees.isEmpty()) {
            return nonAssignes;
        }

        Map<Reservation, Integer> passagersRestants = new HashMap<>();
        for (Reservation reservation : reservationsOrdonnees) {
            passagersRestants.put(reservation, getNombrePassagers(reservation));
        }

        for (Reservation reservationPrincipale : reservationsOrdonnees) {
            while (passagersRestants.getOrDefault(reservationPrincipale, 0) > 0) {
                int passagersRestantsPrincipale = passagersRestants.getOrDefault(reservationPrincipale, 0);

                List<Reservation> actifs = reservationsOrdonnees.stream()
                        .filter(r -> passagersRestants.getOrDefault(r, 0) > 0)
                        .collect(Collectors.toList());
                        
                if (actifs.isEmpty()) break;

                // CORRECTION : On respecte strictement la limite de temps prévue pour le groupe, 
                // on ne repousse plus la recherche dans le futur !
                LocalDateTime heureRecherche = limiteTempsRecherche;

                boolean isReliquat = idsPrioritaires.contains(reservationPrincipale.getId());

                Voiture voitureAssignee = trouverMeilleureVoiture(
                        passagersRestantsPrincipale,
                        heureRecherche,
                        etatsVoitures,
                        isReliquat); 

                if (voitureAssignee == null) {
                    break; 
                }

                EtatVoiture etat = etatsVoitures.get(voitureAssignee);
                int capaciteRestante = voitureAssignee.getCapacite();
                int numeroTrajet = etat.getNombreTrajets() + 1;

                List<AffectationPartielle> reservationsAffecteesVoiture = new ArrayList<>();
                int affectesPrincipale = Math.min(passagersRestantsPrincipale, capaciteRestante);
                
                if (affectesPrincipale <= 0) break;

                reservationsAffecteesVoiture.add(new AffectationPartielle(reservationPrincipale, affectesPrincipale));
                passagersRestants.put(reservationPrincipale, passagersRestantsPrincipale - affectesPrincipale);
                capaciteRestante -= affectesPrincipale;

                while (capaciteRestante > 0) {
                    Reservation resaSuivante = trouverReservationPourCompleterVoiture(
                            passagersRestants, 
                            reservationPrincipale, 
                            capaciteRestante,
                            voitureAssignee,
                            etat,
                            idsPrioritaires); 
                            
                    if (resaSuivante == null) {
                        break;
                    }

                    int nbPassagersSuivant = passagersRestants.getOrDefault(resaSuivante, 0);
                    if (nbPassagersSuivant <= 0) break;

                    int passagersAffectes = Math.min(nbPassagersSuivant, capaciteRestante);
                    reservationsAffecteesVoiture.add(new AffectationPartielle(resaSuivante, passagersAffectes));
                    capaciteRestante -= passagersAffectes;
                    passagersRestants.put(resaSuivante, nbPassagersSuivant - passagersAffectes);
                }

                LocalDateTime maxArrivalOfAssigned = reservationsAffecteesVoiture.stream()
                    .map(a -> parseDateTime(a.reservation.getDateHeureArrivee()))
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);
                
                LocalDateTime dispoVoiture = calculerDisponibiliteVoitureDepuis(voitureAssignee, etat, LocalDateTime.MIN);
                
                LocalDateTime heureDepartEffective = maxArrivalOfAssigned;
                if (dispoVoiture.isAfter(heureDepartEffective)) {
                    heureDepartEffective = dispoVoiture;
                }

                String heureDepartStr = formatDateTime(heureDepartEffective);

                LocalDateTime dateRetourTrajet = ajouterPlanificationsPourVoitureEtGroupe(
                        planifications,
                        reservationsAffecteesVoiture,
                        voitureAssignee,
                        heureDepartStr,
                        vitesseMoyenne,
                        numeroTrajet);
                etat.enregistrerTrajet(dateRetourTrajet);
            }
        }

        for (Map.Entry<Reservation, Integer> entry : passagersRestants.entrySet()) {
            int restants = entry.getValue() != null ? entry.getValue() : 0;
            if (restants > 0) {
                nonAssignes.add(clonerReservationAvecPassagers(entry.getKey(), restants));
            }
        }

        return nonAssignes;
    }

    private Voiture trouverMeilleureVoiture(int nombrePassagers, LocalDateTime horaire,
            Map<Voiture, EtatVoiture> etatsVoitures, boolean pourReliquat) {
        List<Voiture> voituresCandidates = new ArrayList<>();

        for (Map.Entry<Voiture, EtatVoiture> entry : etatsVoitures.entrySet()) {
            Voiture voiture = entry.getKey();
            EtatVoiture etat = entry.getValue();

            if (pourReliquat && etat.getNombreTrajets() == 0) {
                continue;
            }

            if (voiture.getCapacite() > 0
                    && estDisponibleSelonHeureVoiture(voiture, horaire)
                    && etat.estDisponible(horaire)) {
                voituresCandidates.add(voiture);
            }
        }

        if (voituresCandidates.isEmpty()) {
            return null;
        }

        int maxAffectable = voituresCandidates.stream()
            .mapToInt(v -> Math.min(v.getCapacite(), nombrePassagers))
            .max()
            .orElse(0);

        if (maxAffectable <= 0) {
            return null;
        }

        List<Voiture> voituresCapaciteMin = voituresCandidates.stream()
            .filter(v -> Math.min(v.getCapacite(), nombrePassagers) == maxAffectable)
                .collect(Collectors.toList());

        if (voituresCapaciteMin.size() == 1) {
            return voituresCapaciteMin.get(0);
        }

        int minTrajets = voituresCapaciteMin.stream()
            .mapToInt(v -> etatsVoitures.get(v).getNombreTrajets())
            .min()
            .orElse(Integer.MAX_VALUE);

        List<Voiture> voituresMoinsTrajets = voituresCapaciteMin.stream()
            .filter(v -> etatsVoitures.get(v).getNombreTrajets() == minTrajets)
            .collect(Collectors.toList());

        if (voituresMoinsTrajets.size() == 1) {
            return voituresMoinsTrajets.get(0);
        }

        List<Voiture> voituresDiesel = voituresMoinsTrajets.stream()
                .filter(Voiture::estDiesel)
                .collect(Collectors.toList());

        if (!voituresDiesel.isEmpty()) {
            return choisirVoitureDeterministe(voituresDiesel);
        }

        return choisirVoitureDeterministe(voituresMoinsTrajets);
    }

    private Reservation trouverReservationPourCompleterVoiture(
            Map<Reservation, Integer> passagersRestants,
            Reservation reservationPrincipale,
            int capaciteRestante,
            Voiture voiture,
            EtatVoiture etatVoiture,
            Set<Integer> idsPrioritaires) {
        
        boolean estVoitureNeuve = (etatVoiture.getNombreTrajets() == 0);

        List<Reservation> candidates = passagersRestants.entrySet().stream()
            .filter(e -> e.getKey() != reservationPrincipale && e.getValue() != null && e.getValue() > 0)
            .map(Map.Entry::getKey)
            .filter(r -> {
                if (estVoitureNeuve && idsPrioritaires.contains(r.getId())) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return null;
        }

        Comparator<Reservation> tieBreaker = Comparator
            .comparing((Reservation r) -> parseDateTime(r.getDateHeureArrivee()))
            .thenComparing(r -> r.getId() != null ? r.getId() : Integer.MAX_VALUE);

        List<Reservation> superieurOuEgal = candidates.stream()
            .filter(r -> passagersRestants.getOrDefault(r, 0) >= capaciteRestante)
            .sorted(Comparator
                .comparing((Reservation r) -> passagersRestants.getOrDefault(r, 0))
                .thenComparing(tieBreaker))
            .collect(Collectors.toList());

        if (!superieurOuEgal.isEmpty()) {
            return superieurOuEgal.get(0);
        }

        List<Reservation> inferieur = candidates.stream()
            .filter(r -> passagersRestants.getOrDefault(r, 0) < capaciteRestante)
            .sorted(Comparator
                .comparing((Reservation r) -> passagersRestants.getOrDefault(r, 0), Comparator.reverseOrder())
                .thenComparing(tieBreaker))
            .collect(Collectors.toList());

        if (!inferieur.isEmpty()) {
            return inferieur.get(0);
        }

        return null;
    }

    private Voiture choisirVoitureDeterministe(List<Voiture> voitures) {
        return voitures.stream()
                .min(Comparator
                        .comparing((Voiture v) -> v.getId() != null ? v.getId() : Integer.MAX_VALUE)
                        .thenComparing(v -> v.getNumero() != null ? v.getNumero() : ""))
                .orElse(null);
    }

    private List<Reservation> fusionnerReservationsParId(List<Reservation> reservations) {
        Map<Integer, Reservation> reservationsFusionnees = new HashMap<>();
        int fallbackId = -1;

        for (Reservation reservation : reservations) {
            if (reservation == null) {
                continue;
            }

            Integer cleReservation = reservation.getId();
            if (cleReservation == null) {
                cleReservation = fallbackId--;
            }

            int nbPassagers = getNombrePassagers(reservation);
            Reservation existante = reservationsFusionnees.get(cleReservation);
            if (existante == null) {
                reservationsFusionnees.put(cleReservation, clonerReservationAvecPassagers(reservation, nbPassagers));
            } else {
                existante.setNombrePassager(getNombrePassagers(existante) + nbPassagers);
            }
        }

        return new ArrayList<>(reservationsFusionnees.values());
    }

    private LocalDateTime ajouterPlanificationsPourVoitureEtGroupe(
            List<Planification> planifications,
            List<AffectationPartielle> reservationsAffecteesVoiture,
            Voiture voitureAssignee,
            String heureDepartGroupeStr,
            Double vitesseMoyenneKmH,
            int numeroTrajet) {
        if (reservationsAffecteesVoiture == null || reservationsAffecteesVoiture.isEmpty()) {
            return parseDateTime(heureDepartGroupeStr);
        }

        Map<Reservation, Integer> passagersAffectesParReservation = new HashMap<>();
        List<Reservation> reservationsPourItineraire = new ArrayList<>();
        for (AffectationPartielle affectation : reservationsAffecteesVoiture) {
            passagersAffectesParReservation.put(affectation.reservation, affectation.passagersAffectes);
            reservationsPourItineraire.add(affectation.reservation);
        }

        List<Reservation> reservationsTrieesParProximite = construireOrdreItineraireProcheEnProche(reservationsPourItineraire);
        double distanceTotaleCircuit = calculerDistanceTotaleCircuit(reservationsTrieesParProximite);
        
        LocalDateTime dateHeureRetourCommune = calculerDateHeureRetour(
                heureDepartGroupeStr,
                distanceTotaleCircuit,
                vitesseMoyenneKmH);
        String dateHeureRetourCommuneStr = formatDateTime(dateHeureRetourCommune);

        Integer lieuPrecedentId = null;
        String hotelPrecedentNom = null;
        for (Reservation reservation : reservationsTrieesParProximite) {
            Integer lieuActuelId = getLieuIdReservation(reservation);
            double distanceAeroportHotel = getDistanceAeroportPourSomme(lieuActuelId);

            double distanceSegment;
            if (lieuPrecedentId == null) {
                distanceSegment = distanceAeroportHotel;
            } else {
                distanceSegment = getDistanceEntreLieuxPourItineraire(lieuPrecedentId, lieuActuelId);
            }

            Planification planification = new Planification(
                    reservation,
                    voitureAssignee,
                    heureDepartGroupeStr,
                    distanceSegment
            );
            planification.setDateHeureRetour(dateHeureRetourCommuneStr);
            planification.setDistanceAeroportHotel(distanceAeroportHotel);
            planification.setHotelPrecedent(hotelPrecedentNom);
            planification.setNbTrajet(numeroTrajet);
            planification.setPassagersAffectes(passagersAffectesParReservation.getOrDefault(reservation, 0));
            planification.setPassagersDemandes(getNombrePassagers(reservation));
            planifications.add(planification);

            lieuPrecedentId = lieuActuelId;
            hotelPrecedentNom = reservation.getHotel() != null ? reservation.getHotel().getNom() : null;
        }

        return dateHeureRetourCommune;
    }

    private List<Reservation> construireOrdreItineraireProcheEnProche(List<Reservation> reservations) {
        List<Reservation> restantes = new ArrayList<>(reservations);
        List<Reservation> ordre = new ArrayList<>();
        Integer lieuCourantId = AEROPORT_LIEU_ID;

        while (!restantes.isEmpty()) {
            Reservation plusProche = null;
            double distanceMin = Double.MAX_VALUE;

            for (Reservation candidate : restantes) {
                Integer lieuCandidateId = getLieuIdReservation(candidate);
                double distance = getDistancePourOrdre(lieuCourantId, lieuCandidateId);
                if (distance < distanceMin) {
                    distanceMin = distance;
                    plusProche = candidate;
                }
            }

            if (plusProche == null) {
                ordre.addAll(restantes);
                break;
            }

            ordre.add(plusProche);
            restantes.remove(plusProche);
            Integer lieuSelectionneId = getLieuIdReservation(plusProche);
            if (lieuSelectionneId != null) {
                lieuCourantId = lieuSelectionneId;
            }
        }

        return ordre;
    }

    private double getDistancePourOrdre(Integer fromLieuId, Integer toLieuId) {
        if (toLieuId == null) {
            return Double.MAX_VALUE;
        }

        if (fromLieuId == null) {
            return getDistanceAeroportPourSomme(toLieuId);
        }

        return getDistanceEntreLieuxPourItineraire(fromLieuId, toLieuId);
    }

    private Integer getLieuIdReservation(Reservation reservation) {
        if (reservation == null || reservation.getHotel() == null) {
            return null;
        }
        return reservation.getHotel().getIdLieu();
    }

    private double getDistanceAeroportPourSomme(Integer lieuId) {
        if (lieuId == null) {
            return 0.0;
        }

        try {
            Double distance = distanceService.getDistanceDepuisAeroport(lieuId);
            return distance != null ? distance : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    private double getDistanceEntreLieuxPourItineraire(Integer fromLieuId, Integer toLieuId) {
        if (fromLieuId == null || toLieuId == null) {
            return 0.0;
        }

        try {
            Double distance = distanceService.getDistanceEntreLieux(fromLieuId, toLieuId);
            return distance != null ? distance : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    private double calculerDistanceTotaleCircuit(List<Reservation> reservationsTrieesParProximite) {
        if (reservationsTrieesParProximite == null || reservationsTrieesParProximite.isEmpty()) {
            return 0.0;
        }

        double distanceTotale = 0.0;
        Integer lieuPrecedentId = null;

        for (Reservation reservation : reservationsTrieesParProximite) {
            Integer lieuActuelId = getLieuIdReservation(reservation);
            if (lieuActuelId == null) {
                continue;
            }

            if (lieuPrecedentId == null) {
                distanceTotale += getDistanceAeroportPourSomme(lieuActuelId);
            } else {
                distanceTotale += getDistanceEntreLieuxPourItineraire(lieuPrecedentId, lieuActuelId);
            }

            lieuPrecedentId = lieuActuelId;
        }

        if (lieuPrecedentId != null) {
            distanceTotale += getDistanceAeroportPourSomme(lieuPrecedentId);
        }

        return distanceTotale;
    }

    private LocalDateTime calculerDateHeureRetour(String dateHeureDepart, double distanceTotaleKm, Double vitesseMoyenneKmH) {
        LocalDateTime dateHeureDepartParsed = parseDateTime(dateHeureDepart);
        double vitesse = (vitesseMoyenneKmH != null && vitesseMoyenneKmH > 0) ? vitesseMoyenneKmH : 40.0;
        long dureeMinutes = Math.max(0L, Math.round((distanceTotaleKm / vitesse) * 60.0));
        return dateHeureDepartParsed.plusMinutes(dureeMinutes);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private int getNombrePassagers(Reservation reservation) {
        return reservation != null && reservation.getNombrePassager() != null
                ? reservation.getNombrePassager()
                : 0;
    }

    private Reservation clonerReservationAvecPassagers(Reservation source, int passagers) {
        Reservation clone = new Reservation();
        clone.setId(source.getId());
        clone.setClient(source.getClient());
        clone.setHotel(source.getHotel());
        clone.setDateHeureArrivee(source.getDateHeureArrivee());
        clone.setNombrePassager(passagers);
        return clone;
    }

    private LocalDateTime calculerDisponibiliteVoitureDepuis(
            Voiture voiture,
            EtatVoiture etat,
            LocalDateTime reference) {
        LocalDateTime disponibilite = reference;

        if (etat != null && etat.disponibleAPartir != null && etat.disponibleAPartir.isAfter(disponibilite)) {
            disponibilite = etat.disponibleAPartir;
        }

        LocalTime heureMini = extraireHeureDisponibiliteVoiture(voiture);
        if (heureMini != null) {
            LocalDateTime borneHoraireJour = LocalDateTime.of(disponibilite.toLocalDate(), heureMini);
            if (borneHoraireJour.isAfter(disponibilite)) {
                disponibilite = borneHoraireJour;
            }
        }

        return disponibilite;
    }

    private LocalTime extraireHeureDisponibiliteVoiture(Voiture voiture) {
        if (voiture == null || voiture.getHeureDisponibilite() == null || voiture.getHeureDisponibilite().isBlank()) {
            return null;
        }

        String valeur = voiture.getHeureDisponibilite().trim();
        if (valeur.contains("-") || valeur.contains("T") || valeur.contains(" ")) {
            return extraireHeureDepuisDateHeure(valeur);
        }

        try {
            return LocalTime.parse(valeur, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception ignored) {
        }

        try {
            return LocalTime.parse(valeur, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean estDisponibleSelonHeureVoiture(Voiture voiture, LocalDateTime horaireReservation) {
        if (voiture == null || horaireReservation == null) {
            return true;
        }

        String heureDisponibilite = voiture.getHeureDisponibilite();
        if (heureDisponibilite == null || heureDisponibilite.isBlank()) {
            return true;
        }

        String valeur = heureDisponibilite.trim();
        LocalTime heureReservation = horaireReservation.toLocalTime();

        if (valeur.contains("-") || valeur.contains("T") || valeur.contains(" ")) {
            LocalTime heureDepuisDateHeure = extraireHeureDepuisDateHeure(valeur);
            if (heureDepuisDateHeure != null) {
                return !heureReservation.isBefore(heureDepuisDateHeure);
            }
        }

        try {
            LocalTime heure = LocalTime.parse(valeur, DateTimeFormatter.ofPattern("HH:mm:ss"));
            return !heureReservation.isBefore(heure);
        } catch (Exception ignored) {
        }

        try {
            LocalTime heure = LocalTime.parse(valeur, DateTimeFormatter.ofPattern("HH:mm"));
            return !heureReservation.isBefore(heure);
        } catch (Exception ignored) {
            return true;
        }
    }

    private LocalTime extraireHeureDepuisDateHeure(String valeur) {
        String normalized = valeur.replace("T", " ");
        if (normalized.contains(".")) {
            normalized = normalized.substring(0, normalized.indexOf('.'));
        }

        try {
            if (normalized.length() == 16) {
                normalized = normalized + ":00";
            }
            return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalTime();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Reservation> getReservationsSansVoiture(LocalDate date) throws SQLException {
        List<Reservation> reservations = getReservationsForDate(date);
        List<Voiture> voitures = voitureService.readAll();
        List<Reservation> reservationsTries = triReservationParHeureArrivee(reservations);
        List<Planification> planifications = assignerVoitures(
            regrouperParTempsAttente(parametreService.getParametre().getTempsAttente(), reservationsTries), voitures);

        Map<Integer, Integer> passagersAffectesParReservation = new HashMap<>();
        for (Planification planification : planifications) {
            int passagersAffectes = planification.getPassagersAffectes() != null
                ? planification.getPassagersAffectes()
                : getNombrePassagers(planification.getReservation());
            passagersAffectesParReservation.merge(planification.getResaId(), passagersAffectes, Integer::sum);
        }

        return reservations.stream()
            .map(r -> {
                int total = getNombrePassagers(r);
                int affectes = passagersAffectesParReservation.getOrDefault(r.getId(), 0);
                int restants = Math.max(0, total - affectes);
                if (restants > 0) {
                    return clonerReservationAvecPassagers(r, restants);
                }
                return null;
            })
            .filter(r -> r != null)
            .collect(Collectors.toList());
    }

    public List<Planification> regenerateAndSavePlanification(LocalDate date) throws SQLException {
        List<Planification> planifications = getPlanification(date);
        remplacerPlanificationsPourDate(date, planifications);
        return planifications;
    }

    private void remplacerPlanificationsPourDate(LocalDate date, List<Planification> planifications) throws SQLException {
        Connection conn = ConnexDB.getConnection();
        boolean autoCommitInitial = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM planification WHERE DATE(date_heure) = ?";
            try (var deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setDate(1, java.sql.Date.valueOf(date));
                deleteStmt.executeUpdate();
            }

            for (Planification planification : planifications) {
                save(conn, planification);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommitInitial);
            conn.close();
        }
    }

    public Planification save(Planification planification) throws SQLException {
        Connection conn = ConnexDB.getConnection();
        try {
            return save(conn, planification);
        } finally {
            conn.close();
        }
    }

    private Planification save(Connection conn, Planification planification) throws SQLException {
        String sql = "INSERT INTO planification (reservation_id, voiture_id, date_heure, distance,distance_aeroport, date_heure_depart, date_heure_retour, nbtrajet, passagers_affectes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, planification.getResaId());
            pstmt.setInt(2, planification.getVoitureId());
            pstmt.setTimestamp(3, toSqlTimestamp(planification.getDateHeure()));
            pstmt.setDouble(4, planification.getDistance());
            pstmt.setDouble(5, planification.getDistanceAeroportHotel());
            pstmt.setTimestamp(6, toSqlTimestamp(planification.getDateHeureDepart()));
            pstmt.setTimestamp(7, toSqlTimestamp(planification.getDateHeureRetour()));
            pstmt.setInt(8, planification.getNbTrajet() != null ? planification.getNbTrajet() : 1);
            pstmt.setInt(9, planification.getPassagersAffectes() != null
                    ? planification.getPassagersAffectes()
                    : getNombrePassagers(planification.getReservation()));
            pstmt.executeUpdate();

            try (var rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    planification.setId(id);
                    System.out.println("Planification saved with ID: " + id);
                    return planification;
                }
            }
        }

        throw new SQLException("Failed to retrieve generated ID for Planification");
    }

    private Timestamp toSqlTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Timestamp.valueOf(parseDateTime(value));
    }
}