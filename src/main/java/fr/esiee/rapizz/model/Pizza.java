package fr.esiee.rapizz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une pizza
 */
public class Pizza {
    
    private int idPizza;
    private String nom;
    private String description;
    private double prixBase;
    private boolean estPersonnalisable;
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
     * @param description Description de la pizza
     * @param prixBase Prix de base (taille humaine)
     * @param estPersonnalisable Indique si la pizza peut être personnalisée
     */
    public Pizza(String nom, String description, double prixBase, boolean estPersonnalisable) {
        this();
        this.nom = nom;
        this.description = description;
        this.prixBase = prixBase;
        this.estPersonnalisable = estPersonnalisable;
    }
    
    /**
     * Constructeur complet
     * @param idPizza Identifiant de la pizza
     * @param nom Nom de la pizza
     * @param description Description de la pizza
     * @param prixBase Prix de base (taille humaine)
     * @param estPersonnalisable Indique si la pizza peut être personnalisée
     */
    public Pizza(int idPizza, String nom, String description, double prixBase, boolean estPersonnalisable) {
        this(nom, description, prixBase, estPersonnalisable);
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrixBase() {
        return prixBase;
    }
    
    public void setPrixBase(double prixBase) {
        this.prixBase = prixBase;
    }
    
    public boolean isEstPersonnalisable() {
        return estPersonnalisable;
    }
    
    public void setEstPersonnalisable(boolean estPersonnalisable) {
        this.estPersonnalisable = estPersonnalisable;
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