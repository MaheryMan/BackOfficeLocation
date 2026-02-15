package controller;

import annotation.Controller;
import annotation.Get;
import annotation.Param;
import annotation.RestAPI;
import model.Token;
import service.TokenService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TokenController {

    private final TokenService tokenService = new TokenService();
    
    // Durée de validité par défaut : 7 jours
    private static final long DEFAULT_VALIDITY_DAYS = 7;

    /**
     * API publique pour générer un nouveau token
     * GET /api/token?days=7
     */
    @Get("/api/token")
    @RestAPI
    public Map<String, Object> generateToken(@Param("days") Integer validityDays) throws SQLException {
        // Utiliser la durée spécifiée ou la durée par défaut
        long days = (validityDays != null && validityDays > 0) ? validityDays : DEFAULT_VALIDITY_DAYS;
        
        // Calculer la date d'expiration
        long expirationTime = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000);
        Timestamp expirationDate = new Timestamp(expirationTime);
        
        // Générer un nouveau token
        String tokenValue = TokenService.generateRandomToken();
        Token token = new Token(null, tokenValue, expirationDate);
        
        // Sauvegarder dans la base de données
        tokenService.create(token);
        
        // Retourner les informations du token
        Map<String, Object> response = new HashMap<>();
        response.put("token", token.getToken());
        response.put("expiration", token.getDateHeureExpiration().toString());
        response.put("validityDays", days);
        
        return response;
    }
}
