package fr.esiee.rapizz.controller;

import fr.esiee.rapizz.dao.ClientDAO;
import fr.esiee.rapizz.model.Client;

import java.util.List;

/**
 * Contrôleur pour gérer les actions liées aux clients
 */
public class ClientController {
    
    private final ClientDAO clientDAO;
    
    /**
     * Constructeur
     */
    public ClientController() {
        this.clientDAO = new ClientDAO();
    }
    
    /**
     * Ajoute un nouveau client
     * @param client Client à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean ajouterClient(Client client) {
        return clientDAO.inserer(client);
    }
    
    /**
     * Modifie un client existant
     * @param client Client à modifier
     * @return true si la modification a réussi
     */
    public boolean modifierClient(Client client) {
        return clientDAO.mettreAJour(client);
    }
    
    /**
     * Supprime un client
     * @param idClient Identifiant du client à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimerClient(int idClient) {
        return clientDAO.supprimer(idClient);
    }
    
    /**
     * Récupère un client par son ID
     * @param idClient Identifiant du client
     * @return Client trouvé ou null
     */
    public Client rechercherClientParId(int idClient) {
        return clientDAO.trouverParId(idClient);
    }
    
    /**
     * Récupère un client par son email
     * @param email Email du client
     * @return Client trouvé ou null
     */
    public Client rechercherClientParEmail(String email) {
        return clientDAO.trouverParEmail(email);
    }
    
    /**
     * Recherche des clients par nom ou prénom
     * @param terme Terme de recherche
     * @return Liste des clients correspondants
     */
    public List<Client> rechercherClientParNomOuPrenom(String terme) {
        return clientDAO.rechercherParNomOuPrenom(terme);
    }
    
    /**
     * Récupère tous les clients
     * @return Liste de tous les clients
     */
    public List<Client> listerTousLesClients() {
        return clientDAO.trouverTous();
    }
    
    /**
     * Crédite le compte d'un client
     * @param idClient Identifiant du client
     * @param montant Montant à créditer
     * @return true si l'opération a réussi
     */
    public boolean crediterCompteClient(int idClient, double montant) {
        Client client = clientDAO.trouverParId(idClient);
        if (client != null) {
            client.crediterCompte(montant);
            return clientDAO.mettreAJour(client);
        }
        return false;
    }
} 