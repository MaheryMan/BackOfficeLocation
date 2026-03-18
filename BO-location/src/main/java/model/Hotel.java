package model;

import annotations.BaseName;

public class Hotel {
    private Integer id;
    private String nom;
    @BaseName("id_lieu")
    private Integer idLieu;

    public Hotel() {
    }

    public Hotel(Integer id, String nom, Integer idLieu) {
        this.id = id;
        this.nom = nom;
        this.idLieu = idLieu;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(Integer idLieu) {
        this.idLieu = idLieu;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", idLieu=" + idLieu +
                '}';
    }
}
