package fr.esiee.rapizz.model;

/**
 * Classe représentant un ingrédient de pizza
 */
public class Ingredient {
    
    private int idIngredient;
    private String nom;
    private int stock;
    
    /**
     * Constructeur par défaut
     */
    public Ingredient() {
    }
    
    /**
     * Constructeur sans id
     * @param nom Nom de l'ingrédient
     * @param stock Quantité en stock
     */
    public Ingredient(String nom, int stock) {
        this.nom = nom;
        this.stock = stock;
    }
    
    /**
     * Constructeur complet
     * @param idIngredient Identifiant de l'ingrédient
     * @param nom Nom de l'ingrédient
     * @param stock Quantité en stock
     */
    public Ingredient(int idIngredient, String nom, int stock) {
        this.idIngredient = idIngredient;
        this.nom = nom;
        this.stock = stock;
    }
    
    /**
     * Vérifie si l'ingrédient est disponible en quantité suffisante
     * @param quantite Quantité nécessaire
     * @return true si le stock est suffisant
     */
    public boolean estDisponible(int quantite) {
        return stock >= quantite;
    }
    
    /**
     * Prélève une quantité du stock
     * @param quantite Quantité à prélever
     * @return true si le prélèvement a réussi
     */
    public boolean prelever(int quantite) {
        if (estDisponible(quantite)) {
            stock -= quantite;
            return true;
        }
        return false;
    }
    
    /**
     * Ajoute une quantité au stock
     * @param quantite Quantité à ajouter
     */
    public void approvisionner(int quantite) {
        stock += quantite;
    }
    
    // Getters et Setters
    
    public int getIdIngredient() {
        return idIngredient;
    }
    
    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    @Override
    public String toString() {
        return nom;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Ingredient that = (Ingredient) obj;
        
        return idIngredient == that.idIngredient;
    }
    
    @Override
    public int hashCode() {
        return idIngredient;
    }
} 