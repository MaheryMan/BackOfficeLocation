package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import database.ConnexDB;
import model.Client;
import model.Hotel;
import model.Reservation;
import model.TypeEnergie;
import model.Voiture;

public class ReservationService {
    
    private final ClientService clientService = new ClientService();
    private final HotelService hotelService = new HotelService();
    private final VoitureService voitureService = new VoitureService();
    
    public void create(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_client, id_hotel, date_heure_arrivee, nombre_passager, id_voiture) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reservation.getClient().getId());
            stmt.setInt(2, reservation.getHotel().getId());
            stmt.setTimestamp(3, parseTimestamp(reservation.getDateHeureArrivee()));
            stmt.setInt(4, reservation.getNombrePassager());
            if (reservation.getVoiture() != null && reservation.getVoiture().getId() != null) {
                stmt.setInt(5, reservation.getVoiture().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
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
                int idVoiture = rs.getInt("id_voiture");
                if (!rs.wasNull()) {
                    Voiture v = voitureService.read(idVoiture);
                    reservation.setVoiture(v);
                }
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
                int idVoiture = rs.getInt("id_voiture");
                if (!rs.wasNull()) {
                    Voiture v = voitureService.read(idVoiture);
                    reservation.setVoiture(v);
                }
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
    
    public List<Reservation> getReservationsByVoiture(Voiture voiture) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id_voiture = ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, voiture.getId());
            ResultSet rs = stmt.executeQuery();
            
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
                reservation.setVoiture(voiture);
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public Voiture trouverVoiturePourPassengers(int nombrePassager) throws SQLException {
        return trouverVoiturePourPassengers(nombrePassager, null);
    }

    /**
     * Trouve la voiture ayant le plus petit espace disponible >= nombrePassager.
     * Si dateHeureArrivee est fournie, ne tient compte que des réservations à cette date.
     * Retourne en fallback la voiture avec le plus d'espace disponible si aucune ne satisfait la demande.
     */
    public Voiture trouverVoiturePourPassengers(int nombrePassager, String dateHeureArrivee) throws SQLException {
        boolean useDate = dateHeureArrivee != null && !dateHeureArrivee.isBlank();

        String sql = "SELECT v.id, v.numero, v.capacite, v.id_type_energie, te.libelle AS te_libelle, "
                + "(v.capacite - COALESCE(SUM(r.nombre_passager),0)) AS disponible "
                + "FROM voiture v "
                + "LEFT JOIN reservation r ON r.id_voiture = v.id "
                + "LEFT JOIN type_energie te ON te.id = v.id_type_energie ";

        if (useDate) {
            sql += "AND r.date_heure_arrivee = ? ";
        }

        sql += "GROUP BY v.id, v.numero, v.capacite, v.id_type_energie, te.libelle "
                + "HAVING v.capacite > ? AND (v.capacite - COALESCE(SUM(r.nombre_passager),0)) >= ? "
                + "ORDER BY v.capacite ASC, (CASE WHEN lower(te.libelle) = 'diesel' THEN 0 ELSE 1 END) ASC, RANDOM() "
                + "LIMIT 1";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            if (useDate) {
                stmt.setTimestamp(idx++, parseTimestamp(dateHeureArrivee));
            }
            stmt.setInt(idx++, nombrePassager); // v.capacite > nombrePassager
            stmt.setInt(idx, nombrePassager);   // disponible >= nombrePassager

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TypeEnergie te = null;
                int teId = rs.getInt("id_type_energie");
                if (!rs.wasNull()) {
                    te = new TypeEnergie(teId, rs.getString("te_libelle"));
                }
                Voiture v = new Voiture(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        te,
                        rs.getInt("capacite")
                );
                return v;
            }
        }

        // Fallback: choisir la voiture avec v.capacite > nombrePassager et la plus grande 'disponible'
        String sqlFallback = "SELECT v.id, v.numero, v.capacite, v.id_type_energie, te.libelle AS te_libelle, "
                + "(v.capacite - COALESCE(SUM(r.nombre_passager),0)) AS disponible "
                + "FROM voiture v "
                + "LEFT JOIN reservation r ON r.id_voiture = v.id "
                + "LEFT JOIN type_energie te ON te.id = v.id_type_energie ";

        if (useDate) {
            sqlFallback += "AND r.date_heure_arrivee = ? ";
        }

        sqlFallback += "GROUP BY v.id, v.numero, v.capacite, v.id_type_energie, te.libelle "
                + "HAVING v.capacite > ? "
                + "ORDER BY disponible DESC, (CASE WHEN lower(te.libelle) = 'diesel' THEN 0 ELSE 1 END) ASC, RANDOM() "
                + "LIMIT 1";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {

            int idx = 1;
            if (useDate) {
                stmt.setTimestamp(idx++, parseTimestamp(dateHeureArrivee));
            }
            stmt.setInt(idx, nombrePassager); // v.capacite > nombrePassager

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TypeEnergie te = null;
                int teId = rs.getInt("id_type_energie");
                if (!rs.wasNull()) {
                    te = new TypeEnergie(teId, rs.getString("te_libelle"));
                }
                Voiture v = new Voiture(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        te,
                        rs.getInt("capacite")
                );
                return v;
            }
        }

        return null;
    }
}
