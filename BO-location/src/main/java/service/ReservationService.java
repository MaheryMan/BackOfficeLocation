package service;

import database.ConnexDB;
import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    
    public void create(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservation.getIdClient());
            stmt.setInt(2, reservation.getIdHotel());
            stmt.setTimestamp(3, reservation.getDateHeureArrivee());
            stmt.setInt(4, reservation.getNombrePassager());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                reservation.setId(rs.getInt(1));
            }
        }
    }

    public Reservation read(int id) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        Reservation reservation = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getInt("id_client"),
                    rs.getInt("id_hotel"),
                    rs.getTimestamp("date_heure_arrivee"),
                    rs.getInt("nombre_passager")
                );
            }
        }
        return reservation;
    }

    public List<Reservation> readAll() throws SQLException {
        String sql = "SELECT * FROM reservation";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getInt("id_client"),
                    rs.getInt("id_hotel"),
                    rs.getTimestamp("date_heure_arrivee"),
                    rs.getInt("nombre_passager")
                );
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET id_client = ?, id_hotel = ?, date_heure_arrivee = ?, nombre_passager = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservation.getIdClient());
            stmt.setInt(2, reservation.getIdHotel());
            stmt.setTimestamp(3, reservation.getDateHeureArrivee());
            stmt.setInt(4, reservation.getNombrePassager());
            stmt.setInt(5, reservation.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
