package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Livreur;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les livreurs dans la base de données
 */
public class LivreurDAO {
    
    /**
     * Insère un nouveau livreur dans la base de données
     * @param livreur Livreur à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Livreur livreur) {
        String sql = "INSERT INTO Livreur (nom, prenom, telephone, nb_retards) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, livreur.getNom());
            pstmt.setString(2, livreur.getPrenom());
            pstmt.setString(3, livreur.getTelephone());
            pstmt.setInt(4, livreur.getNbRetards());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        livreur.setIdLivreur(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du livreur : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un livreur dans la base de données
     * @param livreur Livreur à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Livreur livreur) {
        String sql = "UPDATE Livreur SET nom = ?, prenom = ?, telephone = ?, nb_retards = ? WHERE id_livreur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, livreur.getNom());
            pstmt.setString(2, livreur.getPrenom());
            pstmt.setString(3, livreur.getTelephone());
            pstmt.setInt(4, livreur.getNbRetards());
            pstmt.setInt(5, livreur.getIdLivreur());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du livreur : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un livreur de la base de données
     * @param idLivreur Identifiant du livreur à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idLivreur) {
        String sql = "DELETE FROM Livreur WHERE id_livreur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLivreur);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livreur : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un livreur par son identifiant
     * @param idLivreur Identifiant du livreur
     * @return Le livreur ou null si non trouvé
     */
    public Livreur trouverParId(int idLivreur) {
        String sql = "SELECT * FROM Livreur WHERE id_livreur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLivreur);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireLivreurDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du livreur : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les livreurs de la base de données
     * @return Liste des livreurs
     */
    public List<Livreur> trouverTous() {
        List<Livreur> livreurs = new ArrayList<>();
        String sql = "SELECT * FROM Livreur ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livreurs.add(extraireLivreurDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livreurs : " + e.getMessage());
        }
        
        return livreurs;
    }
    
    /**
     * Trouve le pire livreur (celui qui a le plus de retards)
     * @return Le pire livreur ou null si aucun
     */
    public Livreur trouverPireLivreur() {
        String sql = "SELECT * FROM Livreur ORDER BY nb_retards DESC LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extraireLivreurDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du pire livreur : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Met à jour le nombre de retards d'un livreur
     * @param idLivreur Identifiant du livreur
     * @param nbRetards Nouveau nombre de retards
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourNbRetards(int idLivreur, int nbRetards) {
        String sql = "UPDATE Livreur SET nb_retards = ? WHERE id_livreur = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nbRetards);
            pstmt.setInt(2, idLivreur);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du nombre de retards : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrait un livreur d'un ResultSet
     * @param rs ResultSet contenant les données du livreur
     * @return Livreur extrait
     * @throws SQLException En cas d'erreur SQL
     */
    private Livreur extraireLivreurDuResultSet(ResultSet rs) throws SQLException {
        Livreur livreur = new Livreur();
        livreur.setIdLivreur(rs.getInt("id_livreur"));
        livreur.setNom(rs.getString("nom"));
        livreur.setPrenom(rs.getString("prenom"));
        livreur.setTelephone(rs.getString("telephone"));
        livreur.setNbRetards(rs.getInt("nb_retards"));
        return livreur;
    }
} 