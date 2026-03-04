package database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexDB {
    
    // Configuration depuis les variables d'environnement ou valeurs par défaut pour dev local
    private static final String URL = System.getenv("DATABASE_URL") != null 
        ? System.getenv("DATABASE_URL") 
        : "jdbc:postgresql://localhost:5432/location_S5";
    private static final String USER = System.getenv("DATABASE_USER") != null 
        ? System.getenv("DATABASE_USER") 
        : "postgres";
    private static final String PASSWORD = System.getenv("DATABASE_PASSWORD") != null 
        ? System.getenv("DATABASE_PASSWORD") 
        : "postgres";
    
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("org.postgresql.Driver");

                String databaseUrl = System.getenv("DATABASE_URL");

                // CAS LOCAL (si DATABASE_URL n'existe pas)
                if (databaseUrl == null) {
                    String url = "jdbc:postgresql://localhost:5432/location_S5";
                    String user = "postgres";
                    String password = "max";
                    connection = DriverManager.getConnection(url, user, password);
                }
                // CAS RENDER/HEROKU (avec DATABASE_URL)
                else {
                    // Si l'URL commence déjà par jdbc:postgresql://, on l'utilise directement
                    if (databaseUrl.startsWith("jdbc:postgresql://")) {
                        // Format JDBC direct (Render style)
                        connection = DriverManager.getConnection(databaseUrl);
                    } else {
                        // Format URI (Heroku style : postgres://user:password@host:port/database)
                        URI dbUri = new URI(databaseUrl);
                        String user = dbUri.getUserInfo().split(":")[0];
                        String password = dbUri.getUserInfo().split(":")[1];
                        String url = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort()
                                + dbUri.getPath() + "?sslmode=require";
                        connection = DriverManager.getConnection(url, user, password);
                    }
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
