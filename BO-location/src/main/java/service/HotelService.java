package service;

import database.ConnexDB;
import model.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelService {
    

    public void create(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotel (nom, id_lieu) VALUES (?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, hotel.getNom());
            stmt.setInt(2, hotel.getIdLieu());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                hotel.setId(rs.getInt(1));
            }
        }
    }

    public Hotel read(int id) throws SQLException {
        String sql = "SELECT id, nom, id_lieu FROM hotel WHERE id = ?";
        Hotel hotel = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                hotel = new Hotel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("id_lieu")
                );
            }
        }
        return hotel;
    }

    public List<Hotel> readAll() throws SQLException {
        String sql = "SELECT id, nom, id_lieu FROM hotel";
        List<Hotel> hotels = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Hotel hotel = new Hotel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("id_lieu")
                );
                hotels.add(hotel);
            }
        }
        return hotels;
    }

    public void update(Hotel hotel) throws SQLException {
        String sql = "UPDATE hotel SET nom = ?, id_lieu = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hotel.getNom());
            stmt.setInt(2, hotel.getIdLieu());
            stmt.setInt(3, hotel.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM hotel WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
