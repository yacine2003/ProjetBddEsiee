package fr.esiee.rapizz;

import fr.esiee.rapizz.util.DatabaseConfig;
import fr.esiee.rapizz.view.WelcomeFrame;

import javax.swing.*;

/**
 * Classe principale de l'application RaPizz
 */
public class RapizzApplication {
    
    /**
     * Point d'entrée de l'application
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Configuration du look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration du look and feel : " + e.getMessage());
        }
        
        // Test de la connexion à la base de données
        try {
            DatabaseConfig.getConnection();
            System.out.println("Connexion à la base de données établie avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur de connexion à la base de données : " + e.getMessage(),
                "Erreur de connexion", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Lancement de la page d'accueil dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
        });
    }
} 