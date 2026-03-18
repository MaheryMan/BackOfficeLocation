package model;

import annotations.BaseName;
public class Parametre {
    @BaseName("temps_attente")
    private Integer tempsAttente;

    @BaseName("vitesse_moyenne")
    private Double vitesseMoyenne;
    
    public Double getVitesseMoyenne() {
        return vitesseMoyenne;
    }

    public void setVitesseMoyenne(Double vitesseMoyenne) {
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public Integer getTempsAttente() {
        return tempsAttente;
    }

    public void setTempsAttente(Integer tempsAttente) {
        this.tempsAttente = tempsAttente;
    }

    public Parametre() {
    }

    public Parametre(Integer tempsAttente, Double vitesseMoyenne) {
        this.tempsAttente = tempsAttente;
        this.vitesseMoyenne = vitesseMoyenne;
    }
}
