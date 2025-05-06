package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Client;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les clients dans la base de données
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
     * Récupère un client par son email
     * Cette méthode est désactivée car la colonne email n'existe pas dans la table Client
     * @param email Email du client
     * @return Le client ou null si non trouvé
     */
    public Client trouverParEmail(String email) {
        // Cette méthode ne peut pas fonctionner car la colonne email n'existe pas
        System.err.println("Attention: La méthode trouverParEmail ne peut pas fonctionner car la colonne email n'existe pas dans la table Client");
        return null;
        /*
        String sql = "SELECT * FROM Client WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireClientDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du client par email : " + e.getMessage());
        }
        
        return null;
        */
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
     * @param terme Terme de recherche (nom ou prénom)
     * @return Liste des clients correspondants
     */
    public List<Client> rechercherParNomOuPrenom(String terme) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String termeLike = "%" + terme + "%";
            pstmt.setString(1, termeLike);
            pstmt.setString(2, termeLike);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(extraireClientDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des clients par nom ou prénom : " + e.getMessage());
        }
        
        return clients;
    }
    
    /**
     * Recherche des clients par ville
     * Cette méthode est désactivée car la colonne ville n'existe pas dans la table Client
     * @param ville Ville de recherche
     * @return Liste des clients correspondants
     */
    public List<Client> rechercherParVille(String ville) {
        // Cette méthode ne peut pas fonctionner car la colonne ville n'existe pas
        System.err.println("Attention: La méthode rechercherParVille ne peut pas fonctionner car la colonne ville n'existe pas dans la table Client");
        return new ArrayList<>();
        /*
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client WHERE ville = ? ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ville);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(extraireClientDuResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des clients par ville : " + e.getMessage());
        }
        
        return clients;
        */
    }
    
    /**
     * Vérifie si l'email et le mot de passe correspondent à un client
     * Cette méthode est désactivée car les colonnes email et mot_de_passe n'existent pas dans la table Client
     * @param email Email du client
     * @param motDePasse Mot de passe du client
     * @return Le client si authentification réussie, null sinon
     */
    public Client authentifier(String email, String motDePasse) {
        // Cette méthode ne peut pas fonctionner car les colonnes email et mot_de_passe n'existent pas
        System.err.println("Attention: La méthode authentifier ne peut pas fonctionner car les colonnes email et mot_de_passe n'existent pas dans la table Client");
        return null;
        /*
        String sql = "SELECT * FROM Client WHERE email = ? AND mot_de_passe = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireClientDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification du client : " + e.getMessage());
        }
        
        return null;
        */
    }
    
    /**
     * Vérifie si un email est déjà utilisé
     * Cette méthode est désactivée car la colonne email n'existe pas dans la table Client
     * @param email Email à vérifier
     * @return true si l'email est déjà utilisé
     */
    public boolean emailExiste(String email) {
        // Cette méthode ne peut pas fonctionner car la colonne email n'existe pas
        System.err.println("Attention: La méthode emailExiste ne peut pas fonctionner car la colonne email n'existe pas dans la table Client");
        return false;
        /*
        String sql = "SELECT COUNT(*) as count FROM Client WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'email : " + e.getMessage());
        }
        
        return false;
        */
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
        
        // Ces colonnes existent dans le modèle mais pas forcément dans la table
        // Traitement des colonnes qui existent dans la base
        try {
            client.setSoldeCompte(rs.getDouble("solde_compte"));
        } catch (SQLException e) {
            client.setSoldeCompte(0.0);
        }
        
        try {
            client.setNbPizzasAchetees(rs.getInt("nb_pizzas_achetees"));
        } catch (SQLException e) {
            client.setNbPizzasAchetees(0);
        }
        
        // Ces colonnes ne sont pas dans la structure actuelle de la table
        // Initialisation avec des valeurs par défaut
        client.setVille("");
        client.setEmail("");
        client.setMotDePasse("");
        
        return client;
    }

    public String[] getColonnes() {
        String sql = "SHOW COLUMNS FROM Client;";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            List<String> colonnes = new ArrayList<>();
            while (rs.next()) {
                colonnes.add(rs.getString("Field"));
            }
            
            return colonnes.toArray(new String[0]);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des colonnes : " + e.getMessage());
        }
        
        return null;
    }
} 