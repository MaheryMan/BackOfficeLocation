package model;

import annotations.BaseName;
public class Parametre {
    @BaseName("temps_attente")
    private Integer tempsAttente;

    public Integer getTempsAttente() {
        return tempsAttente;
    }

    public void setTempsAttente(Integer tempsAttente) {
        this.tempsAttente = tempsAttente;
    }

    public Parametre() {
    }

    public Parametre(Integer tempsAttente) {
        this.tempsAttente = tempsAttente;
    }
}
