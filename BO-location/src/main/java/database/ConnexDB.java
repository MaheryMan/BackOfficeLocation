package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexDB {
    
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("org.postgresql.Driver");

                String databaseUrl = System.getenv("DATABASE_URL");
                String databaseUser = System.getenv("DATABASE_USER");
                String databasePassword = System.getenv("DATABASE_PASSWORD");

                // CAS LOCAL (si DATABASE_URL n'existe pas)
                if (databaseUrl == null) {
                    String url = "jdbc:postgresql://localhost:5432/location_S5";
                    String user = "postgres";
                    String password = "max";
                    connection = DriverManager.getConnection(url, user, password);
                }
                // CAS RENDER (avec DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)
                else {
                    // Si l'URL ne commence pas déjà par jdbc:postgresql://, on l'ajoute
                    if (!databaseUrl.startsWith("jdbc:postgresql://")) {
                        databaseUrl = "jdbc:postgresql://" + databaseUrl;
                    }
                    
                    // Ajouter sslmode=require si pas déjà présent
                    if (!databaseUrl.contains("sslmode=")) {
                        databaseUrl += (databaseUrl.contains("?") ? "&" : "?") + "sslmode=require";
                    }
                    
                    connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
                }

                System.out.println("Connexion PostgreSQL établie !");
            }
        } catch (Exception e) {
            System.err.println("Erreur connexion DB : " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Impossible de se connecter à la base de données", e);
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
