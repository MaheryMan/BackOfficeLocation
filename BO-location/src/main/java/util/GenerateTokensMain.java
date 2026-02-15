package util;

import model.Token;
import service.TokenService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class GenerateTokensMain {
    
    public static void main(String[] args) {
        TokenService tokenService = new TokenService();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Date d'expiration : 17 février 2026 à 12h00
        Timestamp expirationDate = Timestamp.valueOf("2026-02-17 12:00:00");
        
        System.out.println("=== Génération de tokens ===");
        System.out.println("Date d'expiration : " + dateFormat.format(expirationDate));
        System.out.println();
        
        try {
            // Génération de 2 tokens
            for (int i = 1; i <= 2; i++) {
                String tokenValue = TokenService.generateRandomToken();
                Token token = new Token(null, tokenValue, expirationDate);
                
                tokenService.create(token);
                
                System.out.println("Token " + i + " généré avec succès :");
                System.out.println("  ID: " + token.getId());
                System.out.println("  Token: " + token.getToken());
                System.out.println("  Expiration: " + dateFormat.format(token.getDateHeureExpiration()));
                System.out.println("  Expiré: " + token.isExpired());
                System.out.println();
            }
            
            System.out.println("=== Tous les tokens générés avec succès ! ===");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la génération des tokens: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
