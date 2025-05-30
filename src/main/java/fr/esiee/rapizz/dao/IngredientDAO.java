package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Ingredient;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les ingrédients dans la base de données
 */
public class IngredientDAO {
    
    /**
     * Insère un nouvel ingrédient dans la base de données
     * @param ingredient Ingrédient à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Ingredient ingredient) {
        String sql = "INSERT INTO Ingredient (nom, stock) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, ingredient.getNom());
            pstmt.setInt(2, ingredient.getStock());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ingredient.setIdIngredient(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'ingrédient : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un ingrédient dans la base de données
     * @param ingredient Ingrédient à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Ingredient ingredient) {
        String sql = "UPDATE Ingredient SET nom = ?, stock = ? WHERE id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ingredient.getNom());
            pstmt.setInt(2, ingredient.getStock());
            pstmt.setInt(3, ingredient.getIdIngredient());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'ingrédient : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un ingrédient de la base de données
     * @param idIngredient Identifiant de l'ingrédient à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idIngredient) {
        String sql = "DELETE FROM Ingredient WHERE id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idIngredient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ingrédient : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un ingrédient par son identifiant
     * @param idIngredient Identifiant de l'ingrédient
     * @return L'ingrédient ou null si non trouvé
     */
    public Ingredient trouverParId(int idIngredient) {
        String sql = "SELECT * FROM Ingredient WHERE id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idIngredient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireIngredientDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'ingrédient : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les ingrédients de la base de données
     * @return Liste des ingrédients
     */
    public List<Ingredient> trouverTous() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ingredients.add(extraireIngredientDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ingrédients : " + e.getMessage());
        }
        
        return ingredients;
    }
    
    /**
     * Recherche des ingrédients par nom
     * @param nom Nom à rechercher
     * @return Liste des ingrédients correspondants
     */
    public List<Ingredient> rechercherParNom(String nom) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient WHERE nom LIKE ? ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(extraireIngredientDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ingrédients par nom : " + e.getMessage());
        }
        
        return ingredients;
    }
    
    /**
     * Récupère les ingrédients dont le stock est inférieur à une quantité
     * @param quantite Quantité seuil
     * @return Liste des ingrédients avec stock faible
     */
    public List<Ingredient> trouverIngredientsFaibleStock(int quantite) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient WHERE stock < ? ORDER BY stock";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantite);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(extraireIngredientDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ingrédients à faible stock : " + e.getMessage());
        }
        
        return ingredients;
    }
    
    /**
     * Met à jour le stock d'un ingrédient
     * @param idIngredient Identifiant de l'ingrédient
     * @param nouveauStock Nouveau stock
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourStock(int idIngredient, int nouveauStock) {
        String sql = "UPDATE Ingredient SET stock = ? WHERE id_ingredient = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nouveauStock);
            pstmt.setInt(2, idIngredient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vérifie si un ingrédient est disponible en quantité suffisante
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Quantité nécessaire
     * @return true si le stock est suffisant
     */
    public boolean estDisponible(int idIngredient, int quantite) {
        Ingredient ingredient = trouverParId(idIngredient);
        return ingredient != null && ingredient.estDisponible(quantite);
    }
    
    /**
     * Prélève une quantité du stock d'un ingrédient
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Quantité à prélever
     * @return true si le prélèvement a réussi
     */
    public boolean preleverStock(int idIngredient, int quantite) {
        Ingredient ingredient = trouverParId(idIngredient);
        
        if (ingredient != null && ingredient.estDisponible(quantite)) {
            ingredient.prelever(quantite);
            return mettreAJour(ingredient);
        }
        
        return false;
    }
    
    /**
     * Ajoute une quantité au stock d'un ingrédient
     * @param idIngredient Identifiant de l'ingrédient
     * @param quantite Quantité à ajouter
     * @return true si l'approvisionnement a réussi
     */
    public boolean approvisionnerStock(int idIngredient, int quantite) {
        Ingredient ingredient = trouverParId(idIngredient);
        
        if (ingredient != null) {
            ingredient.approvisionner(quantite);
            return mettreAJour(ingredient);
        }
        
        return false;
    }
    
    /**
     * Extrait un ingrédient d'un ResultSet
     * @param rs ResultSet contenant les données de l'ingrédient
     * @return Ingrédient extrait
     * @throws SQLException En cas d'erreur SQL
     */
    private Ingredient extraireIngredientDuResultSet(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getInt("id_ingredient"));
        ingredient.setNom(rs.getString("nom"));
        ingredient.setStock(rs.getInt("stock"));
        return ingredient;
    }
} 