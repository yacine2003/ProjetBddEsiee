package fr.esiee.rapizz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une pizza
 */
public class Pizza {
    
    private int idPizza;
    private String nom;
    private double prixBase;
    private List<Ingredient> ingredients;
    
    /**
     * Constructeur par défaut
     */
    public Pizza() {
        this.ingredients = new ArrayList<>();
    }
    
    /**
     * Constructeur sans id
     * @param nom Nom de la pizza
     * @param prixBase Prix de base
     */
    public Pizza(String nom, double prixBase) {
        this();
        this.nom = nom;
        this.prixBase = prixBase;
    }
    
    /**
     * Constructeur complet
     * @param idPizza Identifiant de la pizza
     * @param nom Nom de la pizza
     * @param prixBase Prix de base
     */
    public Pizza(int idPizza, String nom, double prixBase) {
        this(nom, prixBase);
        this.idPizza = idPizza;
    }
    
    /**
     * Ajoute un ingrédient à la pizza
     * @param ingredient Ingrédient à ajouter
     */
    public void ajouterIngredient(Ingredient ingredient) {
        if (!ingredients.contains(ingredient)) {
            ingredients.add(ingredient);
        }
    }
    
    /**
     * Retire un ingrédient de la pizza
     * @param ingredient Ingrédient à retirer
     * @return true si l'ingrédient a été retiré
     */
    public boolean retirerIngredient(Ingredient ingredient) {
        return ingredients.remove(ingredient);
    }
    
    /**
     * Vide la liste des ingrédients de la pizza
     */
    public void viderIngredients() {
        ingredients.clear();
    }
    
    /**
     * Calcule le prix d'une pizza selon sa taille
     * @param taille Taille de la pizza
     * @return Prix calculé
     */
    public double calculerPrix(Taille taille) {
        return Math.round(prixBase * taille.getCoefficientPrix() * 100) / 100.0;
    }
    
    // Getters et Setters
    
    public int getIdPizza() {
        return idPizza;
    }
    
    public void setIdPizza(int idPizza) {
        this.idPizza = idPizza;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public double getPrixBase() {
        return prixBase;
    }
    
    public void setPrixBase(double prixBase) {
        this.prixBase = prixBase;
    }
    
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    
    @Override
    public String toString() {
        return nom;
    }
} 