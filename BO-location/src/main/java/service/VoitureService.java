package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import database.ConnexDB;
import model.TypeEnergie;
import model.Voiture;

public class VoitureService {

    public void create(Voiture voiture) throws SQLException {
        String sql = "INSERT INTO voiture (numero, id_type_energie, capacite) VALUES (?, ?, ?)";

        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, voiture.getNumero());
            if (voiture.getTypeEnergie() != null && voiture.getTypeEnergie().getId() != null) {
                stmt.setInt(2, voiture.getTypeEnergie().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, voiture.getCapacite());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                voiture.setId(rs.getInt(1));
            }
        }
    }

    public Voiture read(int id) throws SQLException {
        String sql = "SELECT v.*, te.libelle AS te_libelle FROM voiture v LEFT JOIN type_energie te ON te.id = v.id_type_energie WHERE v.id = ?";
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TypeEnergie te = null;
                int teId = rs.getInt("id_type_energie");
                if (!rs.wasNull()) {
                    te = new TypeEnergie(teId, rs.getString("te_libelle"));
                }
                return new Voiture(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        te,
                        rs.getInt("capacite"),
                        rs.getString("heure_disponibilite")
                );
            }
        }
        return null;
    }

    public List<TypeEnergie> readAllTypes() throws SQLException {
        String sql = "SELECT * FROM type_energie";
        List<TypeEnergie> types = new ArrayList<>();
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(new TypeEnergie(rs.getInt("id"), rs.getString("libelle")));
            }
        }
        return types;
    }

    public List<Voiture> readAll() throws SQLException {
        String sql = "SELECT v.*, te.libelle AS te_libelle FROM voiture v LEFT JOIN type_energie te ON te.id = v.id_type_energie";
        List<Voiture> voitures = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TypeEnergie te = null;
                int teId = rs.getInt("id_type_energie");
                if (!rs.wasNull()) {
                    te = new TypeEnergie(teId, rs.getString("te_libelle"));
                }
                
                Voiture voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("numero"),
                    te,
                    rs.getInt("capacite"),
                    rs.getString("heure_disponibilite")
                );
                voitures.add(voiture);
            }
        }
        return voitures;
    }

   
}
