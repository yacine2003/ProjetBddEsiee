package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Taille;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les tailles de pizza dans la base de données
 */
public class TailleDAO {
    
    /**
     * Insère une nouvelle taille dans la base de données
     * @param taille Taille à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Taille taille) {
        String sql = "INSERT INTO Taille (libelle, coefficient_prix) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, taille.getLibelle());
            pstmt.setDouble(2, taille.getCoefficientPrix());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        taille.setIdTaille(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la taille : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour une taille dans la base de données
     * @param taille Taille à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Taille taille) {
        String sql = "UPDATE Taille SET libelle = ?, coefficient_prix = ? WHERE id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, taille.getLibelle());
            pstmt.setDouble(2, taille.getCoefficientPrix());
            pstmt.setInt(3, taille.getIdTaille());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la taille : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime une taille de la base de données
     * @param idTaille Identifiant de la taille à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idTaille) {
        String sql = "DELETE FROM Taille WHERE id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTaille);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la taille : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère une taille par son identifiant
     * @param idTaille Identifiant de la taille
     * @return La taille ou null si non trouvée
     */
    public Taille trouverParId(int idTaille) {
        String sql = "SELECT * FROM Taille WHERE id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idTaille);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireTailleDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la taille : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les tailles de la base de données
     * @return Liste des tailles
     */
    public List<Taille> trouverTous() {
        List<Taille> tailles = new ArrayList<>();
        String sql = "SELECT * FROM Taille ORDER BY coefficient_prix";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tailles.add(extraireTailleDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des tailles : " + e.getMessage());
        }
        
        return tailles;
    }
    
    /**
     * Trouve une taille par son libellé
     * @param libelle Libellé de la taille
     * @return La taille ou null si non trouvée
     */
    public Taille trouverParLibelle(String libelle) {
        String sql = "SELECT * FROM Taille WHERE libelle = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libelle);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireTailleDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la taille par libellé : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Calcule le prix d'une pizza pour une taille donnée
     * @param prixBase Prix de base de la pizza
     * @param idTaille Identifiant de la taille
     * @return Prix calculé
     */
    public double calculerPrix(double prixBase, int idTaille) {
        Taille taille = trouverParId(idTaille);
        
        if (taille != null) {
            return prixBase * taille.getCoefficientPrix();
        }
        
        return prixBase; // Par défaut, retourne le prix de base si la taille n'est pas trouvée
    }
    
    /**
     * Extrait une taille d'un ResultSet
     * @param rs ResultSet contenant les données de la taille
     * @return Taille extraite
     * @throws SQLException En cas d'erreur SQL
     */
    private Taille extraireTailleDuResultSet(ResultSet rs) throws SQLException {
        Taille taille = new Taille();
        taille.setIdTaille(rs.getInt("id_taille"));
        taille.setLibelle(rs.getString("libelle"));
        taille.setCoefficientPrix(rs.getDouble("coefficient_prix"));
        return taille;
    }
} 