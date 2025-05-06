package fr.esiee.rapizz.model;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Classe représentant une livraison de pizza
 */
public class Livraison {
    
    private int idLivraison;
    private LocalTime heureDepart;
    private LocalTime heureArrivee;
    private boolean estEnRetard;
    private Livreur livreur;
    private Vehicule vehicule;
    private Commande commande;
    
    /**
     * Constructeur par défaut
     */
    public Livraison() {
        this.heureDepart = LocalTime.now();
    }
    
    /**
     * Constructeur avec livreur, véhicule et commande
     * @param livreur Le livreur qui effectue la livraison
     * @param vehicule Le véhicule utilisé
     * @param commande La commande à livrer
     */
    public Livraison(Livreur livreur, Vehicule vehicule, Commande commande) {
        this();
        this.livreur = livreur;
        this.vehicule = vehicule;
        this.commande = commande;
        this.estEnRetard = false;
        
        // Marquer la commande comme en livraison
        if (commande != null) {
            commande.partirEnLivraison();
        }
        
        // Marquer le véhicule comme en livraison
        if (vehicule != null) {
            vehicule.partEnLivraison();
        }
    }
    
    /**
     * Constructeur complet
     * @param idLivraison Identifiant de la livraison
     * @param heureDepart Heure de départ
     * @param heureArrivee Heure d'arrivée (peut être null si pas encore arrivé)
     * @param estEnRetard Indique si la livraison est en retard
     * @param livreur Le livreur qui effectue la livraison
     * @param vehicule Le véhicule utilisé
     * @param commande La commande à livrer
     */
    public Livraison(int idLivraison, LocalTime heureDepart, LocalTime heureArrivee, boolean estEnRetard,
                    Livreur livreur, Vehicule vehicule, Commande commande) {
        this.idLivraison = idLivraison;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.estEnRetard = estEnRetard;
        this.livreur = livreur;
        this.vehicule = vehicule;
        this.commande = commande;
    }
    
    /**
     * Termine la livraison en définissant l'heure d'arrivée
     */
    public void terminerLivraison() {
        this.heureArrivee = LocalTime.now();
        this.verifierRetard();
        
        // Marquer la commande comme livrée
        if (commande != null) {
            commande.marquerCommeLivree();
        }
        
        // Rendre le véhicule disponible
        if (vehicule != null) {
            vehicule.retourLivraison();
        }
    }
    
    /**
     * Vérifie si la livraison est en retard (plus de 30 minutes)
     */
    public void verifierRetard() {
        if (heureDepart != null && heureArrivee != null) {
            Duration duree = Duration.between(heureDepart, heureArrivee);
            this.estEnRetard = duree.toMinutes() > 30;
            
            // Si en retard, incrémenter le compteur du livreur et rendre la commande gratuite
            if (this.estEnRetard) {
                if (livreur != null) {
                    livreur.ajouterRetard();
                }
                if (commande != null) {
                    commande.setEstGratuite(true);
                }
            }
        }
    }
    
    /**
     * Calcule la durée de la livraison en minutes
     * @return Durée en minutes ou -1 si la livraison n'est pas terminée
     */
    public long calculerDureeMinutes() {
        if (heureDepart != null && heureArrivee != null) {
            return Duration.between(heureDepart, heureArrivee).toMinutes();
        }
        return -1;
    }
    
    // Getters et Setters
    
    public int getIdLivraison() {
        return idLivraison;
    }
    
    public void setIdLivraison(int idLivraison) {
        this.idLivraison = idLivraison;
    }
    
    public LocalTime getHeureDepart() {
        return heureDepart;
    }
    
    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }
    
    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }
    
    public void setHeureArrivee(LocalTime heureArrivee) {
        this.heureArrivee = heureArrivee;
        verifierRetard();
    }
    
    public boolean isEstEnRetard() {
        return estEnRetard;
    }
    
    public void setEstEnRetard(boolean estEnRetard) {
        this.estEnRetard = estEnRetard;
    }
    
    public Livreur getLivreur() {
        return livreur;
    }
    
    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }
    
    public Vehicule getVehicule() {
        return vehicule;
    }
    
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    public Commande getCommande() {
        return commande;
    }
    
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    
    @Override
    public String toString() {
        return "Livraison #" + idLivraison + 
               (estEnRetard ? " (EN RETARD)" : "") + 
               " - Livreur: " + (livreur != null ? livreur.getNomComplet() : "Non assigné");
    }
} 