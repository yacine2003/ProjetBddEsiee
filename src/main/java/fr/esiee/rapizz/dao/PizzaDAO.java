package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Pizza;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les pizzas dans la base de données
 */
public class PizzaDAO {
    
    /**
     * Insère une nouvelle pizza dans la base de données
     * @param pizza Pizza à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Pizza pizza) {
        String sql = "INSERT INTO Pizza (nom, description, est_personnalisable) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pizza.getNom());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setBoolean(3, pizza.isEstPersonnalisable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pizza.setIdPizza(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la pizza : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour une pizza dans la base de données
     * @param pizza Pizza à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Pizza pizza) {
        String sql = "UPDATE Pizza SET nom = ?, description = ?, est_personnalisable = ? WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pizza.getNom());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setBoolean(3, pizza.isEstPersonnalisable());
            pstmt.setInt(4, pizza.getIdPizza());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la pizza : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime une pizza de la base de données
     * @param idPizza Identifiant de la pizza à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idPizza) {
        String sql = "DELETE FROM Pizza WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la pizza : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère une pizza par son identifiant
     * @param idPizza Identifiant de la pizza
     * @return La pizza ou null si non trouvée
     */
    public Pizza trouverParId(int idPizza) {
        String sql = "SELECT * FROM Pizza WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairePizzaDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la pizza : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les pizzas de la base de données
     * @return Liste des pizzas
     */
    public List<Pizza> trouverTous() {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT * FROM Pizza ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pizzas.add(extrairePizzaDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des pizzas : " + e.getMessage());
        }
        
        return pizzas;
    }
    
    /**
     * Récupère toutes les pizzas personnalisables
     * @return Liste des pizzas personnalisables
     */
    public List<Pizza> trouverPizzasPersonnalisables() {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT * FROM Pizza WHERE est_personnalisable = TRUE ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pizzas.add(extrairePizzaDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des pizzas personnalisables : " + e.getMessage());
        }
        
        return pizzas;
    }
    
    /**
     * Recherche des pizzas par nom
     * @param nom Nom à rechercher
     * @return Liste des pizzas correspondantes
     */
    public List<Pizza> rechercherParNom(String nom) {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT * FROM Pizza WHERE nom LIKE ? ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pizzas.add(extrairePizzaDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des pizzas par nom : " + e.getMessage());
        }
        
        return pizzas;
    }
    
    /**
     * Calcule le prix d'une pizza pour une taille donnée
     * @param idPizza Identifiant de la pizza
     * @param idTaille Identifiant de la taille
     * @return Prix de la pizza pour la taille spécifiée
     */
    public double calculerPrix(int idPizza, int idTaille) {
        double prix = 0.0;
        String sql = "SELECT prix FROM PrixPizza WHERE id_pizza = ? AND id_taille = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idTaille);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prix = rs.getDouble("prix");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du prix de la pizza : " + e.getMessage());
        }
        
        return prix;
    }
    
    /**
     * Définit le prix d'une pizza pour une taille donnée
     * @param idPizza Identifiant de la pizza
     * @param idTaille Identifiant de la taille
     * @param prix Prix à définir
     * @return true si la définition a réussi
     */
    public boolean definirPrix(int idPizza, int idTaille, double prix) {
        String sql = "INSERT INTO PrixPizza (id_pizza, id_taille, prix) VALUES (?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE prix = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idTaille);
            pstmt.setDouble(3, prix);
            pstmt.setDouble(4, prix);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la définition du prix de la pizza : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Trouve la pizza la plus vendue
     * @return La pizza la plus vendue ou null si aucune vente
     */
    public Pizza trouverPizzaLaPlusVendue() {
        String sql = "SELECT p.*, SUM(dc.quantite) as total_ventes " +
                     "FROM Pizza p " +
                     "JOIN DetailCommande dc ON p.id_pizza = dc.id_pizza " +
                     "GROUP BY p.id_pizza " +
                     "ORDER BY total_ventes DESC " +
                     "LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extrairePizzaDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la pizza la plus vendue : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extrait une pizza d'un ResultSet
     * @param rs ResultSet contenant les données de la pizza
     * @return Pizza extraite
     * @throws SQLException En cas d'erreur SQL
     */
    private Pizza extrairePizzaDuResultSet(ResultSet rs) throws SQLException {
        Pizza pizza = new Pizza();
        pizza.setIdPizza(rs.getInt("id_pizza"));
        pizza.setNom(rs.getString("nom"));
        pizza.setDescription(rs.getString("description"));
        pizza.setEstPersonnalisable(rs.getBoolean("est_personnalisable"));
        return pizza;
    }
} 