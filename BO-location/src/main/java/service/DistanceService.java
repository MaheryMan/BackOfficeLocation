package service;

import database.ConnexDB;
import model.Distance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DistanceService {
    private static final int AEROPORT_LIEU_ID = 1;

    public Distance save(Distance distance) throws SQLException {
        String sql = "INSERT INTO distance (from_id_lieu, to_id_lieu, distance) VALUES (?, ?, ?)";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, distance.getFromLieuId());
            stmt.setInt(2, distance.getToLieuId());
            stmt.setDouble(3, distance.getDistance());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    distance.setId(rs.getInt(1));
                }
            }
        }

        return distance;
    }

    public Distance read(int id) throws SQLException {
        String sql = "SELECT id, from_id_lieu, to_id_lieu, distance FROM distance WHERE id = ?";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Distance(
                            rs.getInt("id"),
                            rs.getInt("from_id_lieu"),
                            rs.getInt("to_id_lieu"),
                            rs.getDouble("distance")
                    );
                }
            }
        }

        return null;
    }

    public List<Distance> readAll() throws SQLException {
        String sql = "SELECT id, from_id_lieu, to_id_lieu, distance FROM distance ORDER BY id";
        List<Distance> distances = new ArrayList<>();

        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                distances.add(new Distance(
                        rs.getInt("id"),
                        rs.getInt("from_id_lieu"),
                        rs.getInt("to_id_lieu"),
                        rs.getDouble("distance")
                ));
            }
        }

        return distances;
    }

    public Double getDistanceEntreLieux(int lieuId1, int lieuId2) throws SQLException {
        String sql = """
                SELECT distance
                FROM distance
                WHERE (from_id_lieu = ? AND to_id_lieu = ?)
                   OR (from_id_lieu = ? AND to_id_lieu = ?)
                ORDER BY CASE
                    WHEN from_id_lieu = ? AND to_id_lieu = ? THEN 0
                    ELSE 1
                END
                LIMIT 1
                """;

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lieuId1);
            stmt.setInt(2, lieuId2);
            stmt.setInt(3, lieuId2);
            stmt.setInt(4, lieuId1);
            stmt.setInt(5, lieuId1);
            stmt.setInt(6, lieuId2);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("distance");
                }
            }
        }

        return null;
    }

    public Double getDistanceDepuisAeroport(int lieuId) throws SQLException {
        return getDistanceEntreLieux(AEROPORT_LIEU_ID, lieuId);
    }
}
