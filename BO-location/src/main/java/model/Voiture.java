package model;

import annotations.BaseName;

public class Voiture {
    private Integer id;
    private String numero;
    
    @BaseName("id_type_energie")
    private Integer idTypeEnergie;
    
    private Integer capacite;

    public Voiture() {
    }

    public Voiture(Integer id, String numero, Integer idTypeEnergie, Integer capacite) {
        this.id = id;
        this.numero = numero;
        this.idTypeEnergie = idTypeEnergie;
        this.capacite = capacite;
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

    public Integer getIdTypeEnergie() {
        return idTypeEnergie;
    }

    public void setIdTypeEnergie(Integer idTypeEnergie) {
        this.idTypeEnergie = idTypeEnergie;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Voiture{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", idTypeEnergie=" + idTypeEnergie +
                ", capacite=" + capacite +
                '}';
    }
}
