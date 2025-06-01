package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.DetailCommande;
import fr.esiee.rapizz.model.Commande;
import fr.esiee.rapizz.model.Pizza;
import fr.esiee.rapizz.model.Taille;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les détails de commande dans la base de données
 */
public class DetailCommandeDAO {
    
    private PizzaDAO pizzaDAO;
    private TailleDAO tailleDAO;
    
    public DetailCommandeDAO() {
        this.pizzaDAO = new PizzaDAO();
        this.tailleDAO = new TailleDAO();
    }
    
    /**
     * Insère un nouveau détail de commande dans la base de données
     * @param detailCommande Détail de commande à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(DetailCommande detailCommande) {
        String sql = "INSERT INTO DetailCommande (id_commande, id_pizza, id_taille, quantite, prix_unitaire) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, detailCommande.getCommande().getIdCommande());
            pstmt.setInt(2, detailCommande.getPizza().getIdPizza());
            pstmt.setInt(3, detailCommande.getTaille().getIdTaille());
            pstmt.setInt(4, detailCommande.getQuantite());
            pstmt.setDouble(5, detailCommande.getPrixUnitaire());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du détail de commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un détail de commande dans la base de données
     * @param detailCommande Détail de commande à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(DetailCommande detailCommande) {
        String sql = "UPDATE DetailCommande SET quantite = ?, prix_unitaire = ? WHERE id_commande = ? AND id_pizza = ? AND id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, detailCommande.getQuantite());
            pstmt.setDouble(2, detailCommande.getPrixUnitaire());
            pstmt.setInt(3, detailCommande.getCommande().getIdCommande());
            pstmt.setInt(4, detailCommande.getPizza().getIdPizza());
            pstmt.setInt(5, detailCommande.getTaille().getIdTaille());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du détail de commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un détail de commande de la base de données
     * @param idCommande Identifiant de la commande
     * @param idPizza Identifiant de la pizza
     * @param idTaille Identifiant de la taille
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idCommande, int idPizza, int idTaille) {
        String sql = "DELETE FROM DetailCommande WHERE id_commande = ? AND id_pizza = ? AND id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idPizza);
            pstmt.setInt(3, idTaille);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du détail de commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime tous les détails d'une commande
     * @param idCommande Identifiant de la commande
     * @return true si la suppression a réussi
     */
    public boolean supprimerParCommande(int idCommande) {
        String sql = "DELETE FROM DetailCommande WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des détails de la commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un détail de commande spécifique
     * @param idCommande Identifiant de la commande
     * @param idPizza Identifiant de la pizza
     * @param idTaille Identifiant de la taille
     * @return Le détail de commande ou null si non trouvé
     */
    public DetailCommande trouver(int idCommande, int idPizza, int idTaille) {
        String sql = "SELECT * FROM DetailCommande WHERE id_commande = ? AND id_pizza = ? AND id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idPizza);
            pstmt.setInt(3, idTaille);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireDetailCommandeDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du détail de commande : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les détails d'une commande avec jointures pour éviter les problèmes de ResultSet
     * @param idCommande Identifiant de la commande
     * @return Liste des détails de la commande
     */
    public List<DetailCommande> trouverParCommande(int idCommande) {
        List<DetailCommande> details = new ArrayList<>();
        String sql = "SELECT dc.*, p.nom as pizza_nom, p.prix_base, t.libelle as taille_libelle, t.coefficient_prix " +
                    "FROM DetailCommande dc " +
                    "JOIN Pizza p ON dc.id_pizza = p.id_pizza " +
                    "JOIN Taille t ON dc.id_taille = t.id_taille " +
                    "WHERE dc.id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailCommande detail = new DetailCommande();
                    
                    // Créer une commande simple avec juste l'ID
                    Commande commande = new Commande();
                    commande.setIdCommande(rs.getInt("id_commande"));
                    detail.setCommande(commande);
                    
                    // Créer la pizza directement depuis le ResultSet
                    Pizza pizza = new Pizza();
                    pizza.setIdPizza(rs.getInt("id_pizza"));
                    pizza.setNom(rs.getString("pizza_nom"));
                    pizza.setPrixBase(rs.getDouble("prix_base"));
                    detail.setPizza(pizza);
                    
                    // Créer la taille directement depuis le ResultSet
                    Taille taille = new Taille();
                    taille.setIdTaille(rs.getInt("id_taille"));
                    taille.setLibelle(rs.getString("taille_libelle"));
                    taille.setCoefficientPrix(rs.getDouble("coefficient_prix"));
                    detail.setTaille(taille);
                    
                    detail.setQuantite(rs.getInt("quantite"));
                    detail.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des détails de la commande : " + e.getMessage());
        }
        
        return details;
    }
    
    /**
     * Récupère tous les détails qui concernent une pizza spécifique
     * @param idPizza Identifiant de la pizza
     * @return Liste des détails de commande pour cette pizza
     */
    public List<DetailCommande> trouverParPizza(int idPizza) {
        List<DetailCommande> details = new ArrayList<>();
        String sql = "SELECT * FROM DetailCommande WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    details.add(extraireDetailCommandeDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des détails de commande par pizza : " + e.getMessage());
        }
        
        return details;
    }
    
    /**
     * Calcule le nombre total de pizzas vendues
     * @return Nombre total de pizzas vendues
     */
    public int calculerNombreTotalPizzasVendues() {
        int total = 0;
        String sql = "SELECT SUM(quantite) as total FROM DetailCommande";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du nombre total de pizzas vendues : " + e.getMessage());
        }
        
        return total;
    }
    
    /**
     * Calcule le nombre de pizzas vendues pour une pizza spécifique
     * @param idPizza Identifiant de la pizza
     * @return Nombre de pizzas vendues
     */
    public int calculerNombrePizzasVendues(int idPizza) {
        int total = 0;
        String sql = "SELECT SUM(quantite) as total FROM DetailCommande WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du nombre de pizzas vendues : " + e.getMessage());
        }
        
        return total;
    }
    
    /**
     * Extrait un détail de commande d'un ResultSet
     * @param rs ResultSet contenant les données du détail de commande
     * @return Détail de commande extrait
     * @throws SQLException En cas d'erreur SQL
     */
    private DetailCommande extraireDetailCommandeDuResultSet(ResultSet rs) throws SQLException {
        DetailCommande detail = new DetailCommande();
        
        // Créer une commande simple avec juste l'ID pour éviter la récursion
        int idCommande = rs.getInt("id_commande");
        Commande commande = new Commande();
        commande.setIdCommande(idCommande);
        detail.setCommande(commande);
        
        // Récupérer la pizza associée au détail
        int idPizza = rs.getInt("id_pizza");
        Pizza pizza = pizzaDAO.trouverParId(idPizza);
        detail.setPizza(pizza);
        
        // Récupérer la taille associée au détail
        int idTaille = rs.getInt("id_taille");
        Taille taille = tailleDAO.trouverParId(idTaille);
        detail.setTaille(taille);
        
        detail.setQuantite(rs.getInt("quantite"));
        detail.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        
        return detail;
    }
} 