package fr.esiee.rapizz.model;

/**
 * Classe représentant un livreur de pizzas
 */
public class Livreur {
    
    private int idLivreur;
    private String nom;
    private String prenom;
    private String telephone;
    private int nbRetards;
    
    /**
     * Constructeur par défaut
     */
    public Livreur() {
        this.nbRetards = 0;
    }
    
    /**
     * Constructeur sans id
     * @param nom Nom du livreur
     * @param prenom Prénom du livreur
     * @param telephone Numéro de téléphone du livreur
     */
    public Livreur(String nom, String prenom, String telephone) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }
    
    /**
     * Constructeur complet
     * @param idLivreur Identifiant du livreur
     * @param nom Nom du livreur
     * @param prenom Prénom du livreur
     * @param telephone Numéro de téléphone du livreur
     * @param nbRetards Nombre de retards
     */
    public Livreur(int idLivreur, String nom, String prenom, String telephone, int nbRetards) {
        this.idLivreur = idLivreur;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.nbRetards = nbRetards;
    }
    
    /**
     * Ajoute un retard au livreur
     */
    public void ajouterRetard() {
        this.nbRetards++;
    }
    
    /**
     * Retourne le nom complet du livreur
     * @return Le nom complet
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    // Getters et Setters
    
    public int getIdLivreur() {
        return idLivreur;
    }
    
    public void setIdLivreur(int idLivreur) {
        this.idLivreur = idLivreur;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public int getNbRetards() {
        return nbRetards;
    }
    
    public void setNbRetards(int nbRetards) {
        this.nbRetards = nbRetards;
    }
    
    @Override
    public String toString() {
        return getNomComplet();
    }
}