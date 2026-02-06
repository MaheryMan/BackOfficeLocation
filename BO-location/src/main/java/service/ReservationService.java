package service;

import database.ConnexDB;
import model.Client;
import model.Hotel;
import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    
    private final ClientService clientService = new ClientService();
    private final HotelService hotelService = new HotelService();
    
    public void create(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservation.getClient().getId());
            stmt.setInt(2, reservation.getHotel().getId());
            stmt.setTimestamp(3, parseTimestamp(reservation.getDateHeureArrivee()));
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
                Client client = clientService.read(rs.getInt("id_client"));
                Hotel hotel = hotelService.read(rs.getInt("id_hotel"));
                
                reservation = new Reservation(
                    rs.getInt("id"),
                    client,
                    hotel,
                    timestampToString(rs.getTimestamp("date_heure_arrivee")),
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
                Client client = clientService.read(rs.getInt("id_client"));
                Hotel hotel = hotelService.read(rs.getInt("id_hotel"));
                
                Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    client,
                    hotel,
                    timestampToString(rs.getTimestamp("date_heure_arrivee")),
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
            
            stmt.setInt(1, reservation.getClient().getId());
            stmt.setInt(2, reservation.getHotel().getId());
            stmt.setTimestamp(3, parseTimestamp(reservation.getDateHeureArrivee()));
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

    private Timestamp parseTimestamp(String dateTimeValue) {
        if (dateTimeValue == null || dateTimeValue.isBlank()) {
            return null;
        }
        String normalized = dateTimeValue.replace("T", " ");
        if (normalized.length() == 16) {
            normalized = normalized + ":00";
        }
        return Timestamp.valueOf(normalized);
    }

    private String timestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toString();
    }
}
