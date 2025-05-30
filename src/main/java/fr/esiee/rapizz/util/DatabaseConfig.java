package fr.esiee.rapizz.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer la connexion à la base de données
 */
public class DatabaseConfig {
    
    // Paramètres de connexion
    private static final String DB_URL = "jdbc:mysql://localhost:3306/rapizz";
    private static final String DB_USER = "amine";
    private static final String DB_PASSWORD = "azerty"; // À remplir avec votre mot de passe MySQL
    
    private static Connection connection = null;
    
    /**
     * Retourne une connexion à la base de données
     * @return La connexion
     * @throws SQLException En cas d'erreur de connexion
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Chargement du driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Création de la connexion
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL non trouvé", e);
            }
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
} 