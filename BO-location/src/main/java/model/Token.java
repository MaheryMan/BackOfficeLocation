package model;

import annotations.BaseName;
import java.sql.Timestamp;

public class Token {
    private Integer id;
    private String token;
    
    @BaseName("date_heure_expiration")
    private Timestamp dateHeureExpiration;

    public Token() {
    }

    public Token(Integer id, String token, Timestamp dateHeureExpiration) {
        this.id = id;
        this.token = token;
        this.dateHeureExpiration = dateHeureExpiration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getDateHeureExpiration() {
        return dateHeureExpiration;
    }

    public void setDateHeureExpiration(Timestamp dateHeureExpiration) {
        this.dateHeureExpiration = dateHeureExpiration;
    }

    public boolean isExpired() {
        return dateHeureExpiration != null && 
               dateHeureExpiration.before(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", dateHeureExpiration=" + dateHeureExpiration +
                ", expired=" + isExpired() +
                '}';
    }
}
