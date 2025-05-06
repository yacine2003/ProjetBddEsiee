package fr.esiee.rapizz.model;

/**
 * Classe représentant un véhicule utilisé pour les livraisons
 */
public class Vehicule {
    
    /**
     * Énumération des types de véhicules
     */
    public enum Type {
        VOITURE("voiture"),
        MOTO("moto"),
        SCOOTER("scooter"),
        VELO("velo");
        
        private final String libelle;
        
        Type(String libelle) {
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
    
    /**
     * Énumération des statuts possibles pour un véhicule
     */
    public enum Statut {
        DISPONIBLE("disponible"),
        EN_LIVRAISON("en_livraison"),
        MAINTENANCE("maintenance");
        
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
    
    private int idVehicule;
    private Type type;
    private String immatriculation;
    private Statut statut;
    
    /**
     * Constructeur par défaut
     */
    public Vehicule() {
        this.statut = Statut.DISPONIBLE;
    }
    
    /**
     * Constructeur sans id
     * @param type Type du véhicule
     * @param immatriculation Immatriculation (peut être null pour vélos)
     */
    public Vehicule(Type type, String immatriculation) {
        this();
        this.type = type;
        this.immatriculation = immatriculation;
    }
    
    /**
     * Constructeur complet
     * @param idVehicule Identifiant du véhicule
     * @param type Type du véhicule
     * @param immatriculation Immatriculation
     * @param statut Statut du véhicule
     */
    public Vehicule(int idVehicule, Type type, String immatriculation, Statut statut) {
        this.idVehicule = idVehicule;
        this.type = type;
        this.immatriculation = immatriculation;
        this.statut = statut;
    }
    
    /**
     * Indique si le véhicule est disponible pour une livraison
     * @return true si disponible
     */
    public boolean estDisponible() {
        return statut == Statut.DISPONIBLE;
    }
    
    /**
     * Change le statut du véhicule pour En Livraison
     * @return true si le changement a réussi
     */
    public boolean partEnLivraison() {
        if (estDisponible()) {
            statut = Statut.EN_LIVRAISON;
            return true;
        }
        return false;
    }
    
    /**
     * Change le statut du véhicule pour Disponible
     */
    public void retourLivraison() {
        statut = Statut.DISPONIBLE;
    }
    
    /**
     * Met le véhicule en maintenance
     */
    public void mettreEnMaintenance() {
        statut = Statut.MAINTENANCE;
    }
    
    // Getters et Setters
    
    public int getIdVehicule() {
        return idVehicule;
    }
    
    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getImmatriculation() {
        return immatriculation;
    }
    
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    
    public Statut getStatut() {
        return statut;
    }
    
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    
    @Override
    public String toString() {
        return type + (immatriculation != null ? " (" + immatriculation + ")" : "");
    }
} 