package service;

import database.ConnexDB;
import model.Token;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TokenService {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 64;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Génère un token aléatoire sécurisé
     */
    public static String generateRandomToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }
    
    /**
     * Crée un nouveau token dans la base de données
     */
    public void create(Token token) throws SQLException {
        String sql = "INSERT INTO token (token, date_heure_expiration) VALUES (?, ?)";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, token.getToken());
            stmt.setTimestamp(2, token.getDateHeureExpiration());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                token.setId(rs.getInt(1));
            }
        }
    }
    
    /**
     * Récupère un token par son ID
     */
    public Token read(int id) throws SQLException {
        String sql = "SELECT * FROM token WHERE id = ?";
        Token token = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                token = new Token(
                    rs.getInt("id"),
                    rs.getString("token"),
                    rs.getTimestamp("date_heure_expiration")
                );
            }
        }
        return token;
    }
    
    /**
     * Récupère un token par sa valeur
     */
    public Token findByToken(String tokenValue) throws SQLException {
        String sql = "SELECT * FROM token WHERE token = ?";
        Token token = null;
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tokenValue);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                token = new Token(
                    rs.getInt("id"),
                    rs.getString("token"),
                    rs.getTimestamp("date_heure_expiration")
                );
            }
        }
        return token;
    }
    
    /**
     * Récupère tous les tokens
     */
    public List<Token> readAll() throws SQLException {
        String sql = "SELECT * FROM token";
        List<Token> tokens = new ArrayList<>();
        
        try (Connection conn = ConnexDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Token token = new Token(
                    rs.getInt("id"),
                    rs.getString("token"),
                    rs.getTimestamp("date_heure_expiration")
                );
                tokens.add(token);
            }
        }
        return tokens;
    }
    
    /**
     * Valide un token (vérifie s'il existe et n'est pas expiré)
     */
    public boolean isValidToken(String tokenValue) throws SQLException {
        Token token = findByToken(tokenValue);
        return token != null && !token.isExpired();
    }
    
    /**
     * Met à jour un token
     */
    public void update(Token token) throws SQLException {
        String sql = "UPDATE token SET token = ?, date_heure_expiration = ? WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token.getToken());
            stmt.setTimestamp(2, token.getDateHeureExpiration());
            stmt.setInt(3, token.getId());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Supprime un token
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM token WHERE id = ?";
        
        try (Connection conn = ConnexDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
