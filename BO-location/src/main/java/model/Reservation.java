package model;

import annotations.BaseName;
import java.sql.SQLException;
import java.util.List;
import service.ReservationService;

public class Reservation {
    private Integer id;
    
    private Client client;
    
    private Hotel hotel;
    
    @BaseName("date_heure_arrivee")
    private String dateHeureArrivee;
    
    @BaseName("nombre_passager")
    private Integer nombrePassager;

    public Reservation() {
    }

    public Reservation(Integer id, Client client, Hotel hotel, String dateHeureArrivee, Integer nombrePassager) {
        this.id = id;
        this.client = client;
        this.hotel = hotel;
        this.dateHeureArrivee = dateHeureArrivee;
        this.nombrePassager = nombrePassager;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getDateHeureArrivee() {
        return dateHeureArrivee;
    }

    public void setDateHeureArrivee(String dateHeureArrivee) {
        this.dateHeureArrivee = dateHeureArrivee;
    }

    public Integer getNombrePassager() {
        return nombrePassager;
    }

    public void setNombrePassager(Integer nombrePassager) {
        this.nombrePassager = nombrePassager;
    }

    

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", client=" + client +
                ", hotel=" + hotel +
                ", dateHeureArrivee=" + dateHeureArrivee +
                ", nombrePassager=" + nombrePassager +
                '}';
    }


    // public Voiture trouverVoiturePourPassengers(int nombrePassager, String dateHeureArrivee) {
    //     ReservationService reservationService = new ReservationService();
    //     try {
    //         return reservationService.trouverVoiturePourPassengers(nombrePassager, dateHeureArrivee);
    //     } catch (SQLException e) {
    //         throw new RuntimeException("Erreur lors de la recherche d'une voiture pour " + nombrePassager + " passagers à la date " + dateHeureArrivee, e);
    //     }
    // }

    // public List<Reservation> getReservationsByVoiture(Voiture voiture) {
    //     ReservationService reservationService = new ReservationService();
    
    // try {
    //     return reservationService.getReservationsByVoiture(voiture);
    // } catch (SQLException e) {
    //     throw new RuntimeException("Erreur lors de la recuperation des reservations pour la voiture", e);
    // }
    // }


  


}
