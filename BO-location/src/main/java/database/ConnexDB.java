package database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexDB {

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("org.postgresql.Driver");

                String databaseUrl = System.getenv("DATABASE_URL");

                // CAS LOCAL (si DATABASE_URL n'existe pas)
                if (databaseUrl == null) {
                    String url = "jdbc:postgresql://localhost:5432/location_S5";
                    String user = "postgres";
                    String password = "postgres";
                    connection = DriverManager.getConnection(url, user, password);
                }
                // CAS HEROKU
                else {
                    URI dbUri = new URI(databaseUrl);

                    String user = dbUri.getUserInfo().split(":")[0];
                    String password = dbUri.getUserInfo().split(":")[1];
                    String url = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort()
                            + dbUri.getPath() + "?sslmode=require";

                    connection = DriverManager.getConnection(url, user, password);
                }

                System.out.println("Connexion PostgreSQL établie !");
            }
        } catch (Exception e) {
            System.err.println("Erreur connexion DB : " + e.getMessage());
            e.printStackTrace();
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
