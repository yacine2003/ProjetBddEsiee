package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Client;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour la gestion des clients dans la base de données
 */
public class ClientDAO {
    
    /**
     * Insère un nouveau client dans la base de données
     * @param client Client à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Client client) {
        String sql = "INSERT INTO Client (nom, prenom, adresse, telephone, solde_compte, nb_pizzas_achetees) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresse());
            pstmt.setString(4, client.getTelephone());
            pstmt.setDouble(5, client.getSoldeCompte());
            pstmt.setInt(6, client.getNbPizzasAchetees());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        client.setIdClient(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du client : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un client dans la base de données
     * @param client Client à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Client client) {
        String sql = "UPDATE Client SET nom = ?, prenom = ?, adresse = ?, telephone = ?, solde_compte = ?, nb_pizzas_achetees = ? WHERE id_client = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresse());
            pstmt.setString(4, client.getTelephone());
            pstmt.setDouble(5, client.getSoldeCompte());
            pstmt.setInt(6, client.getNbPizzasAchetees());
            pstmt.setInt(7, client.getIdClient());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du client : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un client de la base de données
     * @param idClient Identifiant du client à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idClient) {
        String sql = "DELETE FROM Client WHERE id_client = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idClient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un client par son identifiant
     * @param idClient Identifiant du client
     * @return Le client ou null si non trouvé
     */
    public Client trouverParId(int idClient) {
        String sql = "SELECT * FROM Client WHERE id_client = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idClient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireClientDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du client : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les clients de la base de données
     * @return Liste des clients
     */
    public List<Client> trouverTous() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clients.add(extraireClientDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des clients : " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Recherche des clients par nom ou prénom
     * @param recherche Texte à rechercher
     * @return Liste des clients correspondants
     */
    public List<Client> rechercherParNom(String recherche) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + recherche + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(extraireClientDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des clients : " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Met à jour le solde du compte d'un client
     * @param idClient Identifiant du client
     * @param nouveauSolde Nouveau solde
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourSolde(int idClient, double nouveauSolde) {
        String sql = "UPDATE Client SET solde_compte = ? WHERE id_client = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, nouveauSolde);
            pstmt.setInt(2, idClient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du solde : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour le nombre de pizzas achetées par un client
     * @param idClient Identifiant du client
     * @param nbPizzas Nouveau nombre de pizzas achetées
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourNbPizzas(int idClient, int nbPizzas) {
        String sql = "UPDATE Client SET nb_pizzas_achetees = ? WHERE id_client = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nbPizzas);
            pstmt.setInt(2, idClient);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du nombre de pizzas : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrait un client d'un ResultSet
     * @param rs ResultSet contenant les données du client
     * @return Client extrait
     * @throws SQLException En cas d'erreur SQL
     */
    private Client extraireClientDuResultSet(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setIdClient(rs.getInt("id_client"));
        client.setNom(rs.getString("nom"));
        client.setPrenom(rs.getString("prenom"));
        client.setAdresse(rs.getString("adresse"));
        client.setTelephone(rs.getString("telephone"));
        client.setSoldeCompte(rs.getDouble("solde_compte"));
        client.setNbPizzasAchetees(rs.getInt("nb_pizzas_achetees"));
        return client;
    }
    
    /**
     * Trouve le meilleur client (celui qui a dépensé le plus)
     * @return Le meilleur client ou null si aucun
     */
    public Client trouverMeilleurClient() {
        String sql = "SELECT c.*, SUM(dc.prix_unitaire * dc.quantite) AS montant_total " +
                     "FROM Client c " +
                     "JOIN Commande com ON c.id_client = com.id_client " +
                     "JOIN DetailCommande dc ON com.id_commande = dc.id_commande " +
                     "WHERE com.est_gratuite = FALSE " +
                     "GROUP BY c.id_client, c.nom, c.prenom " +
                     "ORDER BY montant_total DESC " +
                     "LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return extraireClientDuResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du meilleur client : " + e.getMessage());
        }
        
        return null;
    }
} 