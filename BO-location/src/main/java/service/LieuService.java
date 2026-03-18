package service;

import database.ConnexDB;
import model.Lieu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LieuService {

    public Lieu save(Lieu lieu) throws SQLException {
        String sql = "INSERT INTO lieu (libelle, code) VALUES (?, ?)";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, lieu.getLibelle());
            stmt.setString(2, lieu.getCode());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    lieu.setId(rs.getInt(1));
                }
            }
        }

        return lieu;
    }

    public Lieu read(int id) throws SQLException {
        String sql = "SELECT id, libelle, code FROM lieu WHERE id = ?";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lieu(
                            rs.getInt("id"),
                            rs.getString("libelle"),
                            rs.getString("code")
                    );
                }
            }
        }

        return null;
    }

    public List<Lieu> readAll() throws SQLException {
        String sql = "SELECT id, libelle, code FROM lieu ORDER BY id";
        List<Lieu> lieux = new ArrayList<>();

        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lieux.add(new Lieu(
                        rs.getInt("id"),
                        rs.getString("libelle"),
                        rs.getString("code")
                ));
            }
        }

        return lieux;
    }
}
