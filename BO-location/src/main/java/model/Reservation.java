package model;

import annotations.BaseName;
import java.sql.Timestamp;

public class Reservation {
    private Integer id;
    
    @BaseName("id_client")
    private Integer idClient;
    
    @BaseName("id_hotel")
    private Integer idHotel;
    
    @BaseName("date_heure_arrivee")
    private Timestamp dateHeureArrivee;
    
    @BaseName("nombre_passager")
    private Integer nombrePassager;

    public Reservation() {
    }

    public Reservation(Integer id, Integer idClient, Integer idHotel, Timestamp dateHeureArrivee, Integer nombrePassager) {
        this.id = id;
        this.idClient = idClient;
        this.idHotel = idHotel;
        this.dateHeureArrivee = dateHeureArrivee;
        this.nombrePassager = nombrePassager;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public Integer getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(Integer idHotel) {
        this.idHotel = idHotel;
    }

    public Timestamp getDateHeureArrivee() {
        return dateHeureArrivee;
    }

    public void setDateHeureArrivee(Timestamp dateHeureArrivee) {
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
                ", idClient=" + idClient +
                ", idHotel=" + idHotel +
                ", dateHeureArrivee=" + dateHeureArrivee +
                ", nombrePassager=" + nombrePassager +
                '}';
    }
}
