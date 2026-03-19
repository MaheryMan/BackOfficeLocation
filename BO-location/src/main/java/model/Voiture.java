package model;
import annotations.BaseName;

import java.util.List;

public class Voiture {
    private Integer id;
    
    private String numero;
    
    @BaseName("id_type_energie")
    private TypeEnergie typeEnergie;

    private int capacite;

    private String heureDisponibilite;

    public String getHeureDisponibilite() {
        return heureDisponibilite;
    }

    public void setHeureDisponibilite(String heureDisponibilite) {
        this.heureDisponibilite = heureDisponibilite;
    }

    public Voiture() {
    }

    public Voiture(Integer id, String numero, TypeEnergie typeEnergie, int capacite, String heureDisponibilite) {
        this.id = id;
        this.numero = numero;
        this.typeEnergie = typeEnergie;
        this.capacite = capacite;
        this.heureDisponibilite = heureDisponibilite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TypeEnergie getTypeEnergie() {
        return typeEnergie;
    }

    public void setTypeEnergie(TypeEnergie typeEnergie) {
        this.typeEnergie = typeEnergie;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public boolean estDiesel() {
        return typeEnergie != null && "diesel".equalsIgnoreCase(typeEnergie.getLibelle());
    }
}
