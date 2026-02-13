package service;

import database.ConnexDB;
import model.TypeEnergie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeEnergieService {
    
    public void create(TypeEnergie typeEnergie) throws SQLException {
        String sql = "INSERT INTO type_energie (libelle) VALUES (?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, typeEnergie.getLibelle());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                typeEnergie.setId(rs.getInt(1));
            }
        }
    }
    
    public TypeEnergie read(int id) throws SQLException {
        String sql = "SELECT * FROM type_energie WHERE id = ?";
        TypeEnergie typeEnergie = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                typeEnergie = new TypeEnergie(
                    rs.getInt("id"),
                    rs.getString("libelle")
                );
            }
        }
        return typeEnergie;
    }
    
    public List<TypeEnergie> readAll() throws SQLException {
        String sql = "SELECT * FROM type_energie";
        List<TypeEnergie> typesEnergie = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TypeEnergie typeEnergie = new TypeEnergie(
                    rs.getInt("id"),
                    rs.getString("libelle")
                );
                typesEnergie.add(typeEnergie);
            }
        }
        return typesEnergie;
    }
    
    public void update(TypeEnergie typeEnergie) throws SQLException {
        String sql = "UPDATE type_energie SET libelle = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, typeEnergie.getLibelle());
            stmt.setInt(2, typeEnergie.getId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM type_energie WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
