package service;
import database.ConnexDB;
import model.Parametre;

public class ParametreService {
    public Parametre getParametre() {
        Parametre parametre = null;
        String sql = "SELECT * FROM parametre LIMIT 1";
        
        try (var conn = ConnexDB.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                parametre = new Parametre(rs.getInt("temps_attente"), rs.getDouble("vitesse_moyenne"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return parametre;
    } 
}
