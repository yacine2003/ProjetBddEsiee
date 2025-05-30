package fr.esiee.rapizz.dao;

import fr.esiee.rapizz.model.Vehicule;
import fr.esiee.rapizz.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO pour gérer les véhicules dans la base de données
 */
public class VehiculeDAO {
    
    /**
     * Insère un nouveau véhicule dans la base de données
     * @param vehicule Véhicule à insérer
     * @return true si l'insertion a réussi
     */
    public boolean inserer(Vehicule vehicule) {
        String sql = "INSERT INTO Vehicule (type, immatriculation, statut) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vehicule.getType().getLibelle());
            pstmt.setString(2, vehicule.getImmatriculation());
            pstmt.setString(3, vehicule.getStatut().getLibelle());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehicule.setIdVehicule(rs.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du véhicule : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Met à jour un véhicule dans la base de données
     * @param vehicule Véhicule à mettre à jour
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJour(Vehicule vehicule) {
        String sql = "UPDATE Vehicule SET type = ?, immatriculation = ?, statut = ? WHERE id_vehicule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicule.getType().getLibelle());
            pstmt.setString(2, vehicule.getImmatriculation());
            pstmt.setString(3, vehicule.getStatut().getLibelle());
            pstmt.setInt(4, vehicule.getIdVehicule());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du véhicule : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime un véhicule de la base de données
     * @param idVehicule Identifiant du véhicule à supprimer
     * @return true si la suppression a réussi
     */
    public boolean supprimer(int idVehicule) {
        String sql = "DELETE FROM Vehicule WHERE id_vehicule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVehicule);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du véhicule : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Récupère un véhicule par son identifiant
     * @param idVehicule Identifiant du véhicule
     * @return Le véhicule ou null si non trouvé
     */
    public Vehicule trouverParId(int idVehicule) {
        String sql = "SELECT * FROM Vehicule WHERE id_vehicule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idVehicule);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extraireVehiculeDuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du véhicule : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les véhicules de la base de données
     * @return Liste des véhicules
     */
    public List<Vehicule> trouverTous() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM Vehicule";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicules.add(extraireVehiculeDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des véhicules : " + e.getMessage());
        }
        
        return vehicules;
    }
    
    /**
     * Trouve tous les véhicules disponibles
     * @return Liste des véhicules disponibles
     */
    public List<Vehicule> trouverVehiculesDisponibles() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT * FROM Vehicule WHERE statut = 'disponible'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicules.add(extraireVehiculeDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des véhicules disponibles : " + e.getMessage());
        }
        
        return vehicules;
    }
    
    /**
     * Trouve les véhicules n'ayant jamais servi
     * @return Liste des véhicules jamais utilisés
     */
    public List<Vehicule> trouverVehiculesJamaisUtilises() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT v.* FROM Vehicule v LEFT JOIN Livraison l ON v.id_vehicule = l.id_vehicule WHERE l.id_vehicule IS NULL";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicules.add(extraireVehiculeDuResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des véhicules jamais utilisés : " + e.getMessage());
        }
        
        return vehicules;
    }
    
    /**
     * Met à jour le statut d'un véhicule
     * @param idVehicule Identifiant du véhicule
     * @param statut Nouveau statut
     * @return true si la mise à jour a réussi
     */
    public boolean mettreAJourStatut(int idVehicule, Vehicule.Statut statut) {
        String sql = "UPDATE Vehicule SET statut = ? WHERE id_vehicule = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut.getLibelle());
            pstmt.setInt(2, idVehicule);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut du véhicule : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrait un véhicule d'un ResultSet
     * @param rs ResultSet contenant les données du véhicule
     * @return Véhicule extrait
     * @throws SQLException En cas d'erreur SQL
     */
    private Vehicule extraireVehiculeDuResultSet(ResultSet rs) throws SQLException {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(rs.getInt("id_vehicule"));
        
        // Conversion du type (String vers enum)
        String typeStr = rs.getString("type");
        for (Vehicule.Type type : Vehicule.Type.values()) {
            if (type.getLibelle().equals(typeStr)) {
                vehicule.setType(type);
                break;
            }
        }
        
        vehicule.setImmatriculation(rs.getString("immatriculation"));
        
        // Conversion du statut (String vers enum)
        String statutStr = rs.getString("statut");
        for (Vehicule.Statut statut : Vehicule.Statut.values()) {
            if (statut.getLibelle().equals(statutStr)) {
                vehicule.setStatut(statut);
                break;
            }
        }
        
        return vehicule;
    }
} 