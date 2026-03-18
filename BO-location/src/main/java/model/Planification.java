package model;

public class Planification {
    private Integer id;
    private Integer resaId;
    private Integer voitureId;
    private Reservation reservation;
    private Voiture voiture;
    private String dateHeure;
    private Double distance;
    private String dateHeureDepart;
    private String dateHeureRetour;
    private Double distanceAeroportHotel;
    private String hotelPrecedent;
    private Integer nbTrajet;

    public String getDateHeureDepart() {
        return dateHeureDepart;
    }

    public void setDateHeureDepart(String dateHeureDepart) {
        this.dateHeureDepart = dateHeureDepart;
    }

    public String getDateHeureRetour() {
        return dateHeureRetour;
    }

    public void setDateHeureRetour(String dateHeureRetour) {
        this.dateHeureRetour = dateHeureRetour;
    }

    public Double getDistanceAeroportHotel() {
        return distanceAeroportHotel;
    }

    public void setDistanceAeroportHotel(Double distanceAeroportHotel) {
        this.distanceAeroportHotel = distanceAeroportHotel;
    }

    public String getHotelPrecedent() {
        return hotelPrecedent;
    }

    public void setHotelPrecedent(String hotelPrecedent) {
        this.hotelPrecedent = hotelPrecedent;
    }

    public Integer getNbTrajet() {
        return nbTrajet;
    }

    public void setNbTrajet(Integer nbTrajet) {
        this.nbTrajet = nbTrajet;
    }

    public Planification() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Planification(Reservation reservation, Voiture voiture) {
        this.reservation = reservation;
        this.voiture = voiture;
        this.resaId = reservation.getId();
        this.voitureId = voiture.getId();
        this.dateHeure = reservation.getDateHeureArrivee();
    }

    public Planification(Reservation reservation, Voiture voiture, String dateHeureDepart, Double distance) {
        this.dateHeureDepart = dateHeureDepart;
        this.reservation = reservation;
        this.voiture = voiture;
        this.resaId = reservation.getId();
        this.voitureId = voiture.getId();
        this.dateHeure = reservation.getDateHeureArrivee();
        this.distance = distance;

    }
    public Integer getResaId() {
        return resaId;
    }

    public void setResaId(Integer resaId) {
        this.resaId = resaId;
    }

    public Integer getReservationId() {
        return resaId;
    }

    public void setReservationId(Integer reservationId) {
        this.resaId = reservationId;
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
                "id=" + id +
                ", reservationId=" + resaId +
                ", voitureId=" + voitureId +
                ", dateHeure='" + dateHeure + '\'' +
                ", distance=" + distance +
                ", dateHeureDepart='" + dateHeureDepart + '\'' +
                ", dateHeureRetour='" + dateHeureRetour + '\'' +
                ", distanceAeroportHotel=" + distanceAeroportHotel +
                ", hotelPrecedent='" + hotelPrecedent + '\'' +
                ", nbTrajet=" + nbTrajet +
                ", nombrePassager=" + (reservation != null ? reservation.getNombrePassager() : "N/A") +
                ", capaciteVoiture=" + (voiture != null ? voiture.getCapacite() : "N/A") +
                '}';
    }
}
