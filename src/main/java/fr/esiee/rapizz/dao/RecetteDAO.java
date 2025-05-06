package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Recette;
import fr.esiee.rapizz.model.Ingredient;
import fr.esiee.rapizz.model.Pizza;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe DAO pour gérer les recettes (associations pizza-ingrédients) dans la base de données
 */
public class RecetteDAO {
    
    private PizzaDAO pizzaDAO;
    private IngredientDAO ingredientDAO;
    
    public RecetteDAO() {
        this.pizzaDAO = new PizzaDAO();
        this.ingredientDAO = new IngredientDAO();
    }
    
    /**
     * Associe un ingrédient à une pizza avec une quantité donnée
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Quantité de l'ingrédient dans la recette
     * @return true si l'ajout a réussi
     */
    public boolean ajouterIngredient(int idPizza, int idIngredient, int quantite) {
        String sql = "INSERT INTO Recette (id_pizza, id_ingredient, quantite) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantite = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idIngredient);
            pstmt.setInt(3, quantite);
            pstmt.setInt(4, quantite);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'ingrédient à la recette : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un ingrédient d'une recette
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @return true si la suppression a réussi
     */
    public boolean supprimerIngredient(int idPizza, int idIngredient) {
        String sql = "DELETE FROM Recette WHERE id_pizza = ? AND id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idIngredient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ingrédient de la recette : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour la quantité d'un ingrédient dans une recette
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Nouvelle quantité
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourQuantite(int idPizza, int idIngredient, int quantite) {
        String sql = "UPDATE Recette SET quantite = ? WHERE id_pizza = ? AND id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantite);
            pstmt.setInt(2, idPizza);
            pstmt.setInt(3, idIngredient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la quantité dans la recette : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère la quantité d'un ingrédient pour une pizza
     * @param idPizza Identifiant de la pizza
     * @param idIngredient Identifiant de l'ingrédient
     * @return Quantité ou -1 si non trouvé
     */
    public int trouverQuantite(int idPizza, int idIngredient) {
        String sql = "SELECT quantite FROM Recette WHERE id_pizza = ? AND id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            pstmt.setInt(2, idIngredient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantite");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la quantité dans la recette : " + e.getMessage());
        }
        
        return -1; // L'ingrédient n'est pas dans la recette
    }
    
    /**
     * Récupère tous les ingrédients d'une pizza avec leurs quantités
     * @param idPizza Identifiant de la pizza
     * @return Map associant chaque ingrédient à sa quantité
     */
    public Map<Ingredient, Integer> trouverIngredientsPourPizza(int idPizza) {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        String sql = "SELECT r.id_ingredient, r.quantite FROM Recette r WHERE r.id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idIngredient = rs.getInt("id_ingredient");
                    int quantite = rs.getInt("quantite");
                    
                    Ingredient ingredient = ingredientDAO.trouverParId(idIngredient);
                    if (ingredient != null) {
                        ingredients.put(ingredient, quantite);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ingrédients pour la pizza : " + e.getMessage());
        }
        
        return ingredients;
    }
    
    /**
     * Récupère toutes les pizzas contenant un ingrédient spécifique
     * @param idIngredient Identifiant de l'ingrédient
     * @return Liste des pizzas
     */
    public List<Pizza> trouverPizzasParIngredient(int idIngredient) {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT r.id_pizza FROM Recette r WHERE r.id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idIngredient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idPizza = rs.getInt("id_pizza");
                    
                    Pizza pizza = pizzaDAO.trouverParId(idPizza);
                    if (pizza != null) {
                        pizzas.add(pizza);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des pizzas par ingrédient : " + e.getMessage());
        }
        
        return pizzas;
    }
    
    /**
     * Vérifie si tous les ingrédients d'une pizza sont disponibles en quantité suffisante
     * @param idPizza Identifiant de la pizza
     * @param multiplicateur Multiplicateur de quantité (pour tenir compte de la taille ou du nombre)
     * @return true si tous les ingrédients sont disponibles
     */
    public boolean verifierDisponibiliteIngredients(int idPizza, int multiplicateur) {
        Map<Ingredient, Integer> ingredients = trouverIngredientsPourPizza(idPizza);
        
        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int quantiteNecessaire = entry.getValue() * multiplicateur;
            
            if (!ingredient.estDisponible(quantiteNecessaire)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Prélève les ingrédients nécessaires pour une pizza du stock
     * @param idPizza Identifiant de la pizza
     * @param multiplicateur Multiplicateur de quantité (pour tenir compte de la taille ou du nombre)
     * @return true si tous les ingrédients ont été prélevés
     */
    public boolean preleverIngredients(int idPizza, int multiplicateur) {
        if (!verifierDisponibiliteIngredients(idPizza, multiplicateur)) {
            return false;
        }
        
        Map<Ingredient, Integer> ingredients = trouverIngredientsPourPizza(idPizza);
        boolean succes = true;
        
        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            int quantiteNecessaire = entry.getValue() * multiplicateur;
            
            if (!ingredient.prelever(quantiteNecessaire)) {
                succes = false;
                break;
            }
            
            // Mise à jour du stock dans la base
            ingredientDAO.mettreAJour(ingredient);
        }
        
        return succes;
    }
    
    /**
     * Supprime tous les ingrédients d'une pizza (recette complète)
     * @param idPizza Identifiant de la pizza
     * @return true si la suppression a réussi
     */
    public boolean supprimerRecette(int idPizza) {
        String sql = "DELETE FROM Recette WHERE id_pizza = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPizza);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la recette : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crée une recette complète (ajout de plusieurs ingrédients à la fois)
     * @param idPizza Identifiant de la pizza
     * @param ingredients Map associant chaque identifiant d'ingrédient à sa quantité
     * @return true si la création a réussi
     */
    public boolean creerRecette(int idPizza, Map<Integer, Integer> ingredients) {
        boolean succes = true;
        
        for (Map.Entry<Integer, Integer> entry : ingredients.entrySet()) {
            int idIngredient = entry.getKey();
            int quantite = entry.getValue();
            
            if (!ajouterIngredient(idPizza, idIngredient, quantite)) {
                succes = false;
            }
        }
        
        return succes;
    }
} 