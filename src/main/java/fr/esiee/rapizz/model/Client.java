package fr.esiee.rapizz.model;

/**
 * Classe représentant un client de la pizzeria
 */
public class Client {
    
    private int idClient;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private double soldeCompte;
    private int nbPizzasAchetees;
    
    /**
     * Constructeur par défaut
     */
    public Client() {
    }
    
    /**
     * Constructeur avec tous les champs sauf l'id
     * @param nom Nom du client
     * @param prenom Prénom du client
     * @param adresse Adresse du client
     * @param telephone Numéro de téléphone du client
     * @param soldeCompte Solde du compte client
     * @param nbPizzasAchetees Nombre de pizzas achetées (pour la fidélité)
     */
    public Client(String nom, String prenom, String adresse, String telephone, double soldeCompte, int nbPizzasAchetees) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.soldeCompte = soldeCompte;
        this.nbPizzasAchetees = nbPizzasAchetees;
    }
    
    /**
     * Constructeur complet
     * @param idClient Identifiant du client
     * @param nom Nom du client
     * @param prenom Prénom du client
     * @param adresse Adresse du client
     * @param telephone Numéro de téléphone du client
     * @param soldeCompte Solde du compte client
     * @param nbPizzasAchetees Nombre de pizzas achetées (pour la fidélité)
     */
    public Client(int idClient, String nom, String prenom, String adresse, String telephone, double soldeCompte, int nbPizzasAchetees) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.soldeCompte = soldeCompte;
        this.nbPizzasAchetees = nbPizzasAchetees;
    }
    
    /**
     * Vérifie si le client a droit à une pizza gratuite (fidélité)
     * @return true si le client a droit à une pizza gratuite (10 pizzas achetées)
     */
    public boolean aDroitPizzaGratuiteFidelite() {
        return nbPizzasAchetees >= 10;
    }
    
    /**
     * Vérifie si le solde du client est suffisant pour une commande
     * @param montant Montant de la commande
     * @return true si le solde est suffisant
     */
    public boolean soldeEstSuffisant(double montant) {
        return soldeCompte >= montant;
    }
    
    /**
     * Débite le compte du client
     * @param montant Montant à débiter
     * @return true si l'opération a réussi
     */
    public boolean debiterCompte(double montant) {
        if (soldeEstSuffisant(montant)) {
            soldeCompte -= montant;
            return true;
        }
        return false;
    }
    
    /**
     * Crédite le compte du client
     * @param montant Montant à créditer
     */
    public void crediterCompte(double montant) {
        soldeCompte += montant;
    }
    
    /**
     * Incrémente le nombre de pizzas achetées
     * @param nombre Nombre de pizzas à ajouter
     */
    public void incrementerPizzasAchetees(int nombre) {
        nbPizzasAchetees += nombre;
    }
    
    /**
     * Réinitialise le compteur de pizzas achetées (après avoir bénéficié d'une gratuite)
     */
    public void reinitialiserCompteurPizzas() {
        nbPizzasAchetees = 0;
    }
    
    // Getters et Setters
    
    public int getIdClient() {
        return idClient;
    }
    
    public void setIdClient(int idClient) {
        this.idClient = idClient;
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
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public double getSoldeCompte() {
        return soldeCompte;
    }
    
    public void setSoldeCompte(double soldeCompte) {
        this.soldeCompte = soldeCompte;
    }
    
    public int getNbPizzasAchetees() {
        return nbPizzasAchetees;
    }
    
    public void setNbPizzasAchetees(int nbPizzasAchetees) {
        this.nbPizzasAchetees = nbPizzasAchetees;
    }
    
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
} 