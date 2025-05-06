package fr.esiee.rapizz.model;

/**
 * Classe représentant une association entre une pizza et un ingrédient, avec une quantité
 */
public class Recette {
    
    private Pizza pizza;
    private Ingredient ingredient;
    private int quantite;
    
    /**
     * Constructeur par défaut
     */
    public Recette() {
    }
    
    /**
     * Constructeur avec tous les champs
     * @param pizza La pizza associée
     * @param ingredient L'ingrédient associé
     * @param quantite La quantité de l'ingrédient
     */
    public Recette(Pizza pizza, Ingredient ingredient, int quantite) {
        this.pizza = pizza;
        this.ingredient = ingredient;
        this.quantite = quantite;
    }
    
    /**
     * Vérifie si l'ingrédient est disponible en quantité suffisante
     * @param multiplicateur Multiplicateur lié à la taille ou au nombre de pizzas
     * @return true si l'ingrédient est disponible en quantité suffisante
     */
    public boolean ingredientDisponible(int multiplicateur) {
        return ingredient.estDisponible(quantite * multiplicateur);
    }
    
    /**
     * Prélève la quantité nécessaire d'ingrédient du stock
     * @param multiplicateur Multiplicateur lié à la taille ou au nombre de pizzas
     * @return true si le prélèvement a réussi
     */
    public boolean preleverIngredient(int multiplicateur) {
        return ingredient.prelever(quantite * multiplicateur);
    }
    
    // Getters et Setters
    
    public Pizza getPizza() {
        return pizza;
    }
    
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
    
    public Ingredient getIngredient() {
        return ingredient;
    }
    
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    @Override
    public String toString() {
        return ingredient.getNom() + " x" + quantite;
    }
} 