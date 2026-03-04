package model;

public class Planification {
    private Integer resaId;
    private Integer voitureId;
    private Reservation reservation;
    private Voiture voiture;
    private String dateHeure;
    private Double distance;

    public Planification() {
    }

    public Planification(Reservation reservation, Voiture voiture) {
        this.reservation = reservation;
        this.voiture = voiture;
        this.resaId = reservation.getId();
        this.voitureId = voiture.getId();
        this.dateHeure = reservation.getDateHeureArrivee();
        this.distance = reservation.getHotel().getDistanceAeroport();
    }

    public Integer getResaId() {
        return resaId;
    }

    public void setResaId(Integer resaId) {
        this.resaId = resaId;
    }

    public Integer getVoitureId() {
        return voitureId;
    }

    public void setVoitureId(Integer voitureId) {
        this.voitureId = voitureId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public String getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(String dateHeure) {
        this.dateHeure = dateHeure;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Planification{" +
                "resaId=" + resaId +
                ", voitureId=" + voitureId +
                ", dateHeure='" + dateHeure + '\'' +
                ", distance=" + distance +
                ", nombrePassager=" + (reservation != null ? reservation.getNombrePassager() : "N/A") +
                ", capaciteVoiture=" + (voiture != null ? voiture.getCapacite() : "N/A") +
                '}';
    }
}
