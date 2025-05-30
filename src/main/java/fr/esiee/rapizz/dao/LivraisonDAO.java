package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Livraison;
import fr.esiee.rapizz.model.Livreur;
import fr.esiee.rapizz.model.Vehicule;
import fr.esiee.rapizz.model.Commande;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les livraisons dans la base de données
 */
public class LivraisonDAO {
    
    private LivreurDAO livreurDAO;
    private VehiculeDAO vehiculeDAO;
    private CommandeDAO commandeDAO;
    
    public LivraisonDAO() {
        this.livreurDAO = new LivreurDAO();
        this.vehiculeDAO = new VehiculeDAO();
        this.commandeDAO = new CommandeDAO();
    }
    
    /**
     * Insère une nouvelle livraison dans la base de données
     * @param livraison Livraison à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Livraison livraison) {
        String sql = "INSERT INTO Livraison (heure_depart, heure_arrivee, est_en_retard, id_livreur, id_vehicule, id_commande) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Conversion des heures en format SQL
            Time heureDepart = livraison.getHeureDepart() != null ? Time.valueOf(livraison.getHeureDepart()) : null;
            Time heureArrivee = livraison.getHeureArrivee() != null ? Time.valueOf(livraison.getHeureArrivee()) : null;
            
            pstmt.setTime(1, heureDepart);
            pstmt.setTime(2, heureArrivee);
            pstmt.setBoolean(3, livraison.isEstEnRetard());
            pstmt.setInt(4, livraison.getLivreur().getIdLivreur());
            pstmt.setInt(5, livraison.getVehicule().getIdVehicule());
            pstmt.setInt(6, livraison.getCommande().getIdCommande());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        livraison.setIdLivraison(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la livraison : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour une livraison dans la base de données
     * @param livraison Livraison à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Livraison livraison) {
        String sql = "UPDATE Livraison SET heure_depart = ?, heure_arrivee = ?, est_en_retard = ?, id_livreur = ?, id_vehicule = ?, id_commande = ? WHERE id_livraison = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Conversion des heures en format SQL
            Time heureDepart = livraison.getHeureDepart() != null ? Time.valueOf(livraison.getHeureDepart()) : null;
            Time heureArrivee = livraison.getHeureArrivee() != null ? Time.valueOf(livraison.getHeureArrivee()) : null;
            
            pstmt.setTime(1, heureDepart);
            pstmt.setTime(2, heureArrivee);
            pstmt.setBoolean(3, livraison.isEstEnRetard());
            pstmt.setInt(4, livraison.getLivreur().getIdLivreur());
            pstmt.setInt(5, livraison.getVehicule().getIdVehicule());
            pstmt.setInt(6, livraison.getCommande().getIdCommande());
            pstmt.setInt(7, livraison.getIdLivraison());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la livraison : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime une livraison de la base de données
     * @param idLivraison Identifiant de la livraison à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idLivraison) {
        String sql = "DELETE FROM Livraison WHERE id_livraison = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLivraison);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la livraison : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère une livraison par son identifiant
     * @param idLivraison Identifiant de la livraison
     * @return La livraison ou null si non trouvée
     */
    public Livraison trouverParId(int idLivraison) {
        String sql = "SELECT * FROM Livraison WHERE id_livraison = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLivraison);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireLivraisonDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la livraison : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Trouve une livraison par son identifiant de commande
     * @param idCommande Identifiant de la commande
     * @return La livraison associée à la commande, ou null si non trouvée
     */
    public Livraison trouverParCommande(int idCommande) {
        String sql = "SELECT * FROM Livraison WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireLivraisonDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la livraison par commande : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les livraisons de la base de données
     * @return Liste des livraisons
     */
    public List<Livraison> trouverTous() {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT * FROM Livraison ORDER BY heure_depart DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livraisons.add(extraireLivraisonDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livraisons : " + e.getMessage());
        }
        
        return livraisons;
    }
    
    /**
     * Trouve les livraisons d'un livreur spécifique
     * @param idLivreur Identifiant du livreur
     * @return Liste des livraisons effectuées par ce livreur
     */
    public List<Livraison> trouverParLivreur(int idLivreur) {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT * FROM Livraison WHERE id_livreur = ? ORDER BY heure_depart DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idLivreur);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livraisons.add(extraireLivraisonDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livraisons par livreur : " + e.getMessage());
        }
        
        return livraisons;
    }
    
    /**
     * Trouve les livraisons effectuées avec un véhicule spécifique
     * @param idVehicule Identifiant du véhicule
     * @return Liste des livraisons effectuées avec ce véhicule
     */
    public List<Livraison> trouverParVehicule(int idVehicule) {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT * FROM Livraison WHERE id_vehicule = ? ORDER BY heure_depart DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVehicule);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    livraisons.add(extraireLivraisonDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livraisons par véhicule : " + e.getMessage());
        }
        
        return livraisons;
    }
    
    /**
     * Trouve les livraisons en retard
     * @return Liste des livraisons en retard
     */
    public List<Livraison> trouverLivraisonsEnRetard() {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT * FROM Livraison WHERE est_en_retard = TRUE ORDER BY heure_depart DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livraisons.add(extraireLivraisonDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livraisons en retard : " + e.getMessage());
        }
        
        return livraisons;
    }
    
    /**
     * Marque une livraison comme terminée en ajoutant l'heure d'arrivée et éventuellement le statut de retard
     * @param idLivraison Identifiant de la livraison
     * @param heureArrivee Heure d'arrivée
     * @param estEnRetard Statut de retard
     * @return true si la mise à jour a réussi
     */
    public boolean terminerLivraison(int idLivraison, LocalTime heureArrivee, boolean estEnRetard) {
        String sql = "UPDATE Livraison SET heure_arrivee = ?, est_en_retard = ? WHERE id_livraison = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTime(1, Time.valueOf(heureArrivee));
            pstmt.setBoolean(2, estEnRetard);
            pstmt.setInt(3, idLivraison);
            
            boolean success = pstmt.executeUpdate() > 0;
            
            // Si la livraison est en retard, mettre à jour le nombre de retards du livreur
            if (success && estEnRetard) {
                Livraison livraison = trouverParId(idLivraison);
                if (livraison != null) {
                    Livreur livreur = livraison.getLivreur();
                    livreur.ajouterRetard();
                    livreurDAO.mettreAJour(livreur);
                }
            }
            
            return success;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la livraison terminée : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calcule le nombre total de livraisons en retard
     * @return Nombre de livraisons en retard
     */
    public int calculerNombreLivraisonsEnRetard() {
        int total = 0;
        String sql = "SELECT COUNT(*) as total FROM Livraison WHERE est_en_retard = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du nombre de livraisons en retard : " + e.getMessage());
        }
        
        return total;
    }
    
    /**
     * Extrait une livraison d'un ResultSet
     * @param rs ResultSet contenant les données de la livraison
     * @return Livraison extraite
     * @throws SQLException En cas d'erreur SQL
     */
    private Livraison extraireLivraisonDuResultSet(ResultSet rs) throws SQLException {
        Livraison livraison = new Livraison();
        livraison.setIdLivraison(rs.getInt("id_livraison"));
        
        // Conversion des heures
        Time heureDepart = rs.getTime("heure_depart");
        if (heureDepart != null) {
            livraison.setHeureDepart(heureDepart.toLocalTime());
        }
        
        Time heureArrivee = rs.getTime("heure_arrivee");
        if (heureArrivee != null) {
            livraison.setHeureArrivee(heureArrivee.toLocalTime());
        }
        
        livraison.setEstEnRetard(rs.getBoolean("est_en_retard"));
        
        // Récupérer le livreur associé à la livraison
        int idLivreur = rs.getInt("id_livreur");
        Livreur livreur = livreurDAO.trouverParId(idLivreur);
        livraison.setLivreur(livreur);
        
        // Récupérer le véhicule associé à la livraison
        int idVehicule = rs.getInt("id_vehicule");
        Vehicule vehicule = vehiculeDAO.trouverParId(idVehicule);
        livraison.setVehicule(vehicule);
        
        // Récupérer la commande associée à la livraison
        int idCommande = rs.getInt("id_commande");
        Commande commande = commandeDAO.trouverParId(idCommande);
        livraison.setCommande(commande);
        
        return livraison;
    }
} 