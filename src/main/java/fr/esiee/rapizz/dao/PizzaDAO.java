package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Pizza;
import fr.esiee.rapizz.model.Ingredient;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe DAO pour gérer les pizzas dans la base de données
 */
public class PizzaDAO {
    
    private IngredientDAO ingredientDAO;
    
    /**
     * Constructeur
     */
    public PizzaDAO() {
        this.ingredientDAO = new IngredientDAO();
    }
    
    /**
     * Insère une nouvelle pizza dans la base de données
     * @param pizza Pizza à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Pizza pizza) {
        String sql = "INSERT INTO Pizza (nom, prix_base) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pizza.getNom());
            pstmt.setDouble(2, pizza.getPrixBase());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pizza.setIdPizza(rs.getInt(1));
                        
                        // Insérer les ingrédients si présents
                        List<Ingredient> ingredients = pizza.getIngredients();
                        if (ingredients != null && !ingredients.isEmpty()) {
                            for (Ingredient ingredient : ingredients) {
                                ajouterIngredient(pizza.getIdPizza(), ingredient.getIdIngredient(), 1);
                            }
                        }
                        
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
        String sql = "UPDATE Pizza SET nom = ?, prix_base = ? WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pizza.getNom());
            pstmt.setDouble(2, pizza.getPrixBase());
            pstmt.setInt(3, pizza.getIdPizza());
            
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
        Pizza pizza = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    pizza = extrairePizzaDuResultSet(rs);
                }
            }
            
            // Charger les ingrédients si la pizza a été trouvée
            if (pizza != null) {
                chargerIngredients(pizza);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la pizza : " + e.getMessage());
        }
        
        return pizza;
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
                Pizza pizza = extrairePizzaDuResultSet(rs);
                pizzas.add(pizza);
            }
            
            // Charger les ingrédients pour chaque pizza avec une nouvelle connexion
            for (Pizza pizza : pizzas) {
                chargerIngredients(pizza);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des pizzas : " + e.getMessage());
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
                    Pizza pizza = extrairePizzaDuResultSet(rs);
                    pizzas.add(pizza);
                }
            }
            
            // Charger les ingrédients pour chaque pizza
            for (Pizza pizza : pizzas) {
                chargerIngredients(pizza);
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
        
        // D'abord, récupérer le prix de base de la pizza
        String sqlPizza = "SELECT prix_base FROM Pizza WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlPizza)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double prixBase = rs.getDouble("prix_base");
                    
                    // Ensuite, récupérer le coefficient de la taille
                    String sqlTaille = "SELECT coefficient_prix FROM Taille WHERE id_taille = ?";
                    try (PreparedStatement pstmtTaille = conn.prepareStatement(sqlTaille)) {
                        pstmtTaille.setInt(1, idTaille);
                        
                        try (ResultSet rsTaille = pstmtTaille.executeQuery()) {
                            if (rsTaille.next()) {
                                double coefficient = rsTaille.getDouble("coefficient_prix");
                                prix = prixBase * coefficient;
                            } else {
                                // Si la taille n'est pas trouvée, retourner le prix de base
                                prix = prixBase;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du prix de la pizza : " + e.getMessage());
        }
        
        return prix;
    }
    
    /**
     * Définit le prix d'une pizza
     * @param idPizza Identifiant de la pizza
     * @param nouveauPrix Nouveau prix à définir
     * @return true si la définition a réussi
     */
    public boolean definirPrix(int idPizza, double nouveauPrix) {
        String sql = "UPDATE Pizza SET prix_base = ? WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, nouveauPrix);
            pstmt.setInt(2, idPizza);
            
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
                Pizza pizza = extrairePizzaDuResultSet(rs);
                // Charger les ingrédients avec une nouvelle connexion
                chargerIngredients(pizza);
                return pizza;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la pizza la plus vendue : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Charge la liste des ingrédients pour une pizza
     * @param pizza Pizza pour laquelle charger les ingrédients
     */
    public void chargerIngredients(Pizza pizza) {
        if (pizza == null) return;
        
        List<Integer> ingredientIds = new ArrayList<>();
        
        // D'abord, récupérer les IDs des ingrédients
        String sql = "SELECT id_ingredient FROM CompositionPizza WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pizza.getIdPizza());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredientIds.add(rs.getInt("id_ingredient"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des IDs des ingrédients : " + e.getMessage());
            return;
        }
        
        // Ensuite, récupérer chaque ingrédient individuellement
        for (Integer idIngredient : ingredientIds) {
            try {
                Ingredient ingredient = ingredientDAO.trouverParId(idIngredient);
                if (ingredient != null) {
                    pizza.ajouterIngredient(ingredient);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de l'ingrédient ID " + idIngredient + " : " + e.getMessage());
            }
        }
    }
    
    /**
     * Ajoute un ingrédient à une pizza
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Quantité de l'ingrédient
     * @return true si l'ajout a réussi
     */
    public boolean ajouterIngredient(int idPizza, int idIngredient, int quantite) {
        String sql = "INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantite = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idIngredient);
            pstmt.setInt(3, quantite);
            pstmt.setInt(4, quantite);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'ingrédient à la pizza : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un ingrédient d'une pizza
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @return true si la suppression a réussi
     */
    public boolean supprimerIngredient(int idPizza, int idIngredient) {
        String sql = "DELETE FROM CompositionPizza WHERE id_pizza = ? AND id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idIngredient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ingrédient de la pizza : " + e.getMessage());
            return false;
        }
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
        pizza.setPrixBase(rs.getDouble("prix_base"));
        return pizza;
    }
} 