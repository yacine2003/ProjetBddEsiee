package fr.esiee.rapizz.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une commande de pizzas
 */
public class Commande {
    
    /**
     * Énumération des statuts possibles pour une commande
     */
    public enum Statut {
        EN_PREPARATION("en_preparation"),
        EN_LIVRAISON("en_livraison"),
        LIVREE("livree"),
        ANNULEE("annulee");
        
        private final String libelle;
        
        Statut(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
        
        @Override
        public String toString() {
            return libelle;
        }
    }
    
    private int idCommande;
    private LocalDate dateCommande;
    private LocalTime heureCommande;
    private Statut statut;
    private boolean estGratuite;
    private Client client;
    private List<DetailCommande> detailsCommande;
    
    /**
     * Constructeur par défaut
     */
    public Commande() {
        this.dateCommande = LocalDate.now();
        this.heureCommande = LocalTime.now();
        this.statut = Statut.EN_PREPARATION;
        this.estGratuite = false;
        this.detailsCommande = new ArrayList<>();
    }
    
    /**
     * Constructeur avec client
     * @param client Le client qui passe la commande
     */
    public Commande(Client client) {
        this();
        this.client = client;
    }
    
    /**
     * Constructeur complet
     * @param idCommande Identifiant de la commande
     * @param dateCommande Date de la commande
     * @param heureCommande Heure de la commande
     * @param statut Statut de la commande
     * @param estGratuite Indique si la commande est gratuite
     * @param client Client qui a passé la commande
     */
    public Commande(int idCommande, LocalDate dateCommande, LocalTime heureCommande, 
                    Statut statut, boolean estGratuite, Client client) {
        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.heureCommande = heureCommande;
        this.statut = statut;
        this.estGratuite = estGratuite;
        this.client = client;
        this.detailsCommande = new ArrayList<>();
    }
    
    /**
     * Ajoute un détail de commande
     * @param detail Détail à ajouter
     */
    public void ajouterDetail(DetailCommande detail) {
        detail.setCommande(this);
        detailsCommande.add(detail);
    }
    
    /**
     * Ajoute une pizza à la commande
     * @param pizza Pizza à ajouter
     * @param taille Taille de la pizza
     * @param quantite Quantité désirée
     */
    public void ajouterPizza(Pizza pizza, Taille taille, int quantite) {
        DetailCommande detail = new DetailCommande();
        detail.setCommande(this);
        detail.setPizza(pizza);
        detail.setTaille(taille);
        detail.setQuantite(quantite);
        detail.calculerPrixUnitaire();
        detailsCommande.add(detail);
    }
    
    /**
     * Calcule le montant total de la commande
     * @return Montant total
     */
    public double calculerTotal() {
        if (estGratuite) {
            return 0.0;
        }
        
        double total = 0.0;
        for (DetailCommande detail : detailsCommande) {
            total += detail.calculerSousTotal();
        }
        return total;
    }
    
    /**
     * Vérifie si la commande peut être passée (solde client suffisant)
     * @return true si la commande peut être passée
     */
    public boolean peutEtrePrepare() {
        if (estGratuite) {
            return true;
        }
        
        if (client == null) {
            return false;
        }
        
        return client.soldeEstSuffisant(calculerTotal());
    }
    
    /**
     * Valide la commande et débite le compte du client
     * @return true si la validation a réussi
     */
    public boolean valider() {
        if (estGratuite || client.debiterCompte(calculerTotal())) {
            // Si ce n'est pas une commande gratuite, on incrémente le nombre de pizzas achetées
            if (!estGratuite) {
                int totalPizzas = 0;
                for (DetailCommande detail : detailsCommande) {
                    totalPizzas += detail.getQuantite();
                }
                client.incrementerPizzasAchetees(totalPizzas);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Change le statut de la commande pour En Livraison
     */
    public void partirEnLivraison() {
        this.statut = Statut.EN_LIVRAISON;
    }
    
    /**
     * Change le statut de la commande pour Livrée
     */
    public void marquerCommeLivree() {
        this.statut = Statut.LIVREE;
    }
    
    /**
     * Annule la commande
     */
    public void annuler() {
        this.statut = Statut.ANNULEE;
    }
    
    // Getters et Setters
    
    public int getIdCommande() {
        return idCommande;
    }
    
    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }
    
    public LocalDate getDateCommande() {
        return dateCommande;
    }
    
    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }
    
    public LocalTime getHeureCommande() {
        return heureCommande;
    }
    
    public void setHeureCommande(LocalTime heureCommande) {
        this.heureCommande = heureCommande;
    }
    
    public Statut getStatut() {
        return statut;
    }
    
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    
    public boolean isEstGratuite() {
        return estGratuite;
    }
    
    public void setEstGratuite(boolean estGratuite) {
        this.estGratuite = estGratuite;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public List<DetailCommande> getDetailsCommande() {
        return detailsCommande;
    }
    
    public void setDetailsCommande(List<DetailCommande> detailsCommande) {
        this.detailsCommande = detailsCommande;
    }
    
    @Override
    public String toString() {
        return "Commande #" + idCommande + " (" + statut + ")";
    }
} 