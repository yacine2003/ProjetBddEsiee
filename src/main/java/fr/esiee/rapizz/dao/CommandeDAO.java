package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Commande;
import fr.esiee.rapizz.model.Client;
import fr.esiee.rapizz.model.DetailCommande;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les commandes dans la base de données
 */
public class CommandeDAO {
    
    private DetailCommandeDAO detailCommandeDAO;
    private ClientDAO clientDAO;
    
    public CommandeDAO() {
        this.detailCommandeDAO = new DetailCommandeDAO();
        this.clientDAO = new ClientDAO();
    }
    
    /**
     * Insère une nouvelle commande dans la base de données
     * @param commande Commande à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Commande commande) {
        String sql = "INSERT INTO Commande (date_commande, heure_commande, statut, id_client) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Formatage de la date et de l'heure selon le format attendu par la base
            Date sqlDate = Date.valueOf(commande.getDateCommande());
            Time sqlTime = Time.valueOf(commande.getHeureCommande());
            
            pstmt.setDate(1, sqlDate);
            pstmt.setTime(2, sqlTime);
            pstmt.setString(3, commande.getStatut().getLibelle());
            pstmt.setInt(4, commande.getClient().getIdClient());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        commande.setIdCommande(rs.getInt(1));
                        
                        // Insérer les détails de la commande
                        for (DetailCommande detail : commande.getDetailsCommande()) {
                            detail.setCommande(commande);
                            detailCommandeDAO.inserer(detail);
                        }
                        
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de la commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour une commande dans la base de données
     * @param commande Commande à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Commande commande) {
        String sql = "UPDATE Commande SET date_commande = ?, heure_commande = ?, statut = ?, id_client = ? WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            Date sqlDate = Date.valueOf(commande.getDateCommande());
            Time sqlTime = Time.valueOf(commande.getHeureCommande());
            
            pstmt.setDate(1, sqlDate);
            pstmt.setTime(2, sqlTime);
            pstmt.setString(3, commande.getStatut().getLibelle());
            pstmt.setInt(4, commande.getClient().getIdClient());
            pstmt.setInt(5, commande.getIdCommande());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime une commande de la base de données
     * @param idCommande Identifiant de la commande à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idCommande) {
        // D'abord supprimer les détails de commande associés
        detailCommandeDAO.supprimerParCommande(idCommande);
        
        String sql = "DELETE FROM Commande WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère une commande par son identifiant
     * @param idCommande Identifiant de la commande
     * @return La commande ou null si non trouvée
     */
    public Commande trouverParId(int idCommande) {
        String sql = "SELECT * FROM Commande WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = extraireCommandeDuResultSet(rs);
                    
                    // Charger les détails de la commande
                    commande.setDetailsCommande(detailCommandeDAO.trouverParCommande(commande.getIdCommande()));
                    
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la commande : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les commandes de la base de données
     * @return Liste des commandes
     */
    public List<Commande> trouverTous() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande ORDER BY date_commande DESC, heure_commande DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = extraireCommandeDuResultSet(rs);
                
                // Charger les détails de la commande
                commande.setDetailsCommande(detailCommandeDAO.trouverParCommande(commande.getIdCommande()));
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes : " + e.getMessage());
        }
        
        return commandes;
    }
    
    /**
     * Trouve les commandes d'un client
     * @param idClient Identifiant du client
     * @return Liste des commandes du client
     */
    public List<Commande> trouverParClient(int idClient) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE id_client = ? ORDER BY date_commande DESC, heure_commande DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idClient);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = extraireCommandeDuResultSet(rs);
                    
                    // Charger les détails de la commande
                    commande.setDetailsCommande(detailCommandeDAO.trouverParCommande(commande.getIdCommande()));
                    
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des commandes du client : " + e.getMessage());
        }
        
        return commandes;
    }
    
    /**
     * Trouve les commandes d'une journée spécifique
     * @param date Date des commandes à récupérer
     * @return Liste des commandes pour cette date
     */
    public List<Commande> trouverParDate(LocalDate date) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE date_commande = ? ORDER BY heure_commande";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = extraireCommandeDuResultSet(rs);
                    
                    // Charger les détails de la commande
                    commande.setDetailsCommande(detailCommandeDAO.trouverParCommande(commande.getIdCommande()));
                    
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des commandes par date : " + e.getMessage());
        }
        
        return commandes;
    }
    
    /**
     * Met à jour le statut d'une commande
     * @param idCommande Identifiant de la commande
     * @param statut Nouveau statut
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourStatut(int idCommande, Commande.Statut statut) {
        String sql = "UPDATE Commande SET statut = ? WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut.getLibelle());
            pstmt.setInt(2, idCommande);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la commande : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calcule le montant total d'une commande
     * @param idCommande Identifiant de la commande
     * @return Montant total de la commande
     */
    public double calculerMontantTotal(int idCommande) {
        double total = 0.0;
        
        String sql = "SELECT SUM(prix_unitaire * quantite) AS total FROM DetailCommande WHERE id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du montant total de la commande : " + e.getMessage());
        }
        
        return total;
    }
    
    /**
     * Extrait une commande d'un ResultSet
     * @param rs ResultSet contenant les données de la commande
     * @return Commande extraite
     * @throws SQLException En cas d'erreur SQL
     */
    private Commande extraireCommandeDuResultSet(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setIdCommande(rs.getInt("id_commande"));
        
        // Conversion des dates et heures
        commande.setDateCommande(rs.getDate("date_commande").toLocalDate());
        commande.setHeureCommande(rs.getTime("heure_commande").toLocalTime());
        
        // Conversion du statut (String vers enum)
        String statutStr = rs.getString("statut");
        for (Commande.Statut statut : Commande.Statut.values()) {
            if (statut.getLibelle().equals(statutStr)) {
                commande.setStatut(statut);
                break;
            }
        }
        
        // Récupérer le client associé à la commande
        int idClient = rs.getInt("id_client");
        Client client = clientDAO.trouverParId(idClient);
        commande.setClient(client);
        
        return commande;
    }
    
    /**
     * Récupère toutes les commandes de la base de données sans charger les détails
     * @return Liste des commandes
     */
    public List<Commande> trouverTousSansDetails() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, cl.nom, cl.prenom, cl.solde_compte " +
                    "FROM Commande c " +
                    "JOIN Client cl ON c.id_client = cl.id_client " +
                    "ORDER BY c.date_commande DESC, c.heure_commande DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("id_commande"));
                
                // Conversion des dates et heures
                commande.setDateCommande(rs.getDate("date_commande").toLocalDate());
                commande.setHeureCommande(rs.getTime("heure_commande").toLocalTime());
                
                // Conversion du statut (String vers enum)
                String statutStr = rs.getString("statut");
                for (Commande.Statut statut : Commande.Statut.values()) {
                    if (statut.getLibelle().equals(statutStr)) {
                        commande.setStatut(statut);
                        break;
                    }
                }
                
                // Créer le client directement depuis le ResultSet
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setSoldeCompte(rs.getDouble("solde_compte"));
                commande.setClient(client);
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes : " + e.getMessage());
            e.printStackTrace();
        }
        
        return commandes;
    }
    
    /**
     * Récupère une commande par son identifiant sans charger les détails
     * @param idCommande Identifiant de la commande
     * @return La commande ou null si non trouvée
     */
    public Commande trouverParIdSansDetails(int idCommande) {
        String sql = "SELECT c.*, cl.nom, cl.prenom, cl.solde_compte " +
                    "FROM Commande c " +
                    "JOIN Client cl ON c.id_client = cl.id_client " +
                    "WHERE c.id_commande = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCommande);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = new Commande();
                    commande.setIdCommande(rs.getInt("id_commande"));
                    
                    // Conversion des dates et heures
                    commande.setDateCommande(rs.getDate("date_commande").toLocalDate());
                    commande.setHeureCommande(rs.getTime("heure_commande").toLocalTime());
                    
                    // Conversion du statut (String vers enum)
                    String statutStr = rs.getString("statut");
                    for (Commande.Statut statut : Commande.Statut.values()) {
                        if (statut.getLibelle().equals(statutStr)) {
                            commande.setStatut(statut);
                            break;
                        }
                    }
                    
                    // Créer le client directement depuis le ResultSet
                    Client client = new Client();
                    client.setIdClient(rs.getInt("id_client"));
                    client.setNom(rs.getString("nom"));
                    client.setPrenom(rs.getString("prenom"));
                    client.setSoldeCompte(rs.getDouble("solde_compte"));
                    commande.setClient(client);
                    
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la commande : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
} 