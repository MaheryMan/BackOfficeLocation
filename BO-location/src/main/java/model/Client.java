package model;

import annotations.BaseName;

public class Client {
    private Integer id;
    private String nom;
    
    @BaseName("numero_passport")
    private String numeroPassport;
    
    private String email;
    private String contact;

    public Client() {
    }

    public Client(Integer id, String nom, String numeroPassport, String email, String contact) {
        this.id = id;
        this.nom = nom;
        this.numeroPassport = numeroPassport;
        this.email = email;
        this.contact = contact;
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

    public String getNumeroPassport() {
        return numeroPassport;
    }

    public void setNumeroPassport(String numeroPassport) {
        this.numeroPassport = numeroPassport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", numeroPassport='" + numeroPassport + '\'' +
                ", email='" + email + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
