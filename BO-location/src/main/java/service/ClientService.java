package service;

import database.ConnexDB;
import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    
    public void create(Client client) throws SQLException {
        String sql = "INSERT INTO client (nom, numero_passport, email, contact) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getNumeroPassport());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getContact());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                client.setId(rs.getInt(1));
            }
        }
    }
    

    public Client read(int id) throws SQLException {
        String sql = "SELECT * FROM client WHERE id = ?";
        Client client = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("numero_passport"),
                    rs.getString("email"),
                    rs.getString("contact")
                );
            }
        }
        return client;
    }
    

    public List<Client> readAll() throws SQLException {
        String sql = "SELECT * FROM client";
        List<Client> clients = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("numero_passport"),
                    rs.getString("email"),
                    rs.getString("contact")
                );
                clients.add(client);
            }
        }
        return clients;
    }
    

    public void update(Client client) throws SQLException {
        String sql = "UPDATE client SET nom = ?, numero_passport = ?, email = ?, contact = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getNumeroPassport());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getContact());
            stmt.setInt(5, client.getId());
            
            stmt.executeUpdate();
        }
    }
    
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
