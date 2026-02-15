package service;

import database.ConnexDB;
import model.Voiture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoitureService {
    
    public void create(Voiture voiture) throws SQLException {
        String sql = "INSERT INTO voiture (numero, id_type_energie, capacite) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, voiture.getNumero());
            stmt.setInt(2, voiture.getIdTypeEnergie());
            stmt.setInt(3, voiture.getCapacite());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                voiture.setId(rs.getInt(1));
            }
        }
    }
    
    public Voiture read(int id) throws SQLException {
        String sql = "SELECT * FROM voiture WHERE id = ?";
        Voiture voiture = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getInt("id_type_energie"),
                    rs.getInt("capacite")
                );
            }
        }
        return voiture;
    }
    
    public List<Voiture> readAll() throws SQLException {
        String sql = "SELECT * FROM voiture";
        List<Voiture> voitures = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Voiture voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getInt("id_type_energie"),
                    rs.getInt("capacite")
                );
                voitures.add(voiture);
            }
        }
        return voitures;
    }
    
    public List<Voiture> readByTypeEnergie(int idTypeEnergie) throws SQLException {
        String sql = "SELECT * FROM voiture WHERE id_type_energie = ?";
        List<Voiture> voitures = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTypeEnergie);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Voiture voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    rs.getInt("id_type_energie"),
                    rs.getInt("capacite")
                );
                voitures.add(voiture);
            }
        }
        return voitures;
    }
    
    public void update(Voiture voiture) throws SQLException {
        String sql = "UPDATE voiture SET numero = ?, id_type_energie = ?, capacite = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, voiture.getNumero());
            stmt.setInt(2, voiture.getIdTypeEnergie());
            stmt.setInt(3, voiture.getCapacite());
            stmt.setInt(4, voiture.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM voiture WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
