package fr.esiee.rapizz.model;

/**
 * Classe représentant un détail de commande (une ligne de commande)
 */
public class DetailCommande {
    
    private Commande commande;
    private Pizza pizza;
    private Taille taille;
    private int quantite;
    private double prixUnitaire;
    
    /**
     * Constructeur par défaut
     */
    public DetailCommande() {
        this.quantite = 1;
        // La taille par défaut sera définie lors de l'initialisation de la pizza
    }
    
    /**
     * Constructeur avec tous les champs
     * @param commande Commande associée
     * @param pizza Pizza commandée
     * @param taille Taille de la pizza
     * @param quantite Quantité commandée
     * @param prixUnitaire Prix unitaire
     */
    public DetailCommande(Commande commande, Pizza pizza, Taille taille, int quantite, double prixUnitaire) {
        this.commande = commande;
        this.pizza = pizza;
        this.taille = taille;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
    
    /**
     * Calcule le prix total de ce détail de commande (quantité * prix unitaire)
     * @return Prix total
     */
    public double calculerPrixTotal() {
        return quantite * prixUnitaire;
    }
    
    /**
     * Calcule le prix unitaire en fonction de la pizza et de la taille
     */
    public void calculerPrixUnitaire() {
        if (pizza != null && taille != null) {
            this.prixUnitaire = taille.calculerPrix(pizza.getPrixBase());
        }
    }
    
    /**
     * Calcule le sous-total pour cette ligne de commande
     * @return Le montant total (prix unitaire * quantité)
     */
    public double calculerSousTotal() {
        return prixUnitaire * quantite;
    }
    
    /**
     * Augmente la quantité commandée
     * @param quantite Quantité à ajouter
     */
    public void augmenterQuantite(int quantite) {
        this.quantite += quantite;
    }
    
    // Getters et Setters
    
    public Commande getCommande() {
        return commande;
    }
    
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    
    public Pizza getPizza() {
        return pizza;
    }
    
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
        calculerPrixUnitaire();
    }
    
    public Taille getTaille() {
        return taille;
    }
    
    public void setTaille(Taille taille) {
        this.taille = taille;
        calculerPrixUnitaire();
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public double getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    
    @Override
    public String toString() {
        return pizza.getNom() + " (taille " + taille.getLibelle() + ") x" + quantite;
    }
} 