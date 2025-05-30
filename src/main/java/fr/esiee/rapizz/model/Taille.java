package fr.esiee.rapizz.model;

/**
 * Classe représentant une taille de pizza
 */
public class Taille {
    
    private int idTaille;
    private String libelle;
    private double coefficientPrix;
    
    /**
     * Constructeur par défaut
     */
    public Taille() {
    }
    
    /**
     * Constructeur avec tous les champs sauf l'id
     * @param libelle Libellé de la taille (ex: "Petite", "Moyenne", "Grande")
     * @param coefficientPrix Coefficient multiplicateur du prix de base
     */
    public Taille(String libelle, double coefficientPrix) {
        this.libelle = libelle;
        this.coefficientPrix = coefficientPrix;
    }
    
    /**
     * Constructeur complet
     * @param idTaille Identifiant de la taille
     * @param libelle Libellé de la taille (ex: "Petite", "Moyenne", "Grande")
     * @param coefficientPrix Coefficient multiplicateur du prix de base
     */
    public Taille(int idTaille, String libelle, double coefficientPrix) {
        this.idTaille = idTaille;
        this.libelle = libelle;
        this.coefficientPrix = coefficientPrix;
    }
    
    /**
     * Calcule le prix de la pizza pour cette taille
     * @param prixBase Prix de base de la pizza
     * @return Prix calculé pour cette taille
     */
    public double calculerPrix(double prixBase) {
        return prixBase * coefficientPrix;
    }
    
    // Getters et Setters
    
    public int getIdTaille() {
        return idTaille;
    }
    
    public void setIdTaille(int idTaille) {
        this.idTaille = idTaille;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public double getCoefficientPrix() {
        return coefficientPrix;
    }
    
    public void setCoefficientPrix(double coefficientPrix) {
        this.coefficientPrix = coefficientPrix;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
} 