package model;

import annotations.BaseName;

public class Hotel {
    private Integer id;
    private String nom;
    
    @BaseName("distance_aeroport")
    private Double distanceAeroport;

    public Hotel() {
    }

    public Hotel(Integer id, String nom, Double distanceAeroport) {
        this.id = id;
        this.nom = nom;
        this.distanceAeroport = distanceAeroport;
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

    public Double getDistanceAeroport() {
        return distanceAeroport;
    }

    public void setDistanceAeroport(Double distanceAeroport) {
        this.distanceAeroport = distanceAeroport;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", distanceAeroport=" + distanceAeroport +
                '}';
    }
}
