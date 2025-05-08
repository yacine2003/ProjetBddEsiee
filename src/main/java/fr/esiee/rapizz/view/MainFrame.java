package fr.esiee.rapizz.view;

import fr.esiee.rapizz.util.DatabaseConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import fr.esiee.rapizz.dao.*;
import fr.esiee.rapizz.model.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
/**
 * Fenêtre principale de l'application RaPizz
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    
    /**
     * Constructeur de la fenêtre principale
     */
    public MainFrame() {
        // Configuration de la fenêtre
        setTitle("RaPizz - Gestion de pizzeria");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Ajout d'un listener pour fermer la connexion à la base de données à la fermeture de l'application
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConfig.closeConnection();
            }
        });
        
        // Initialisation des composants
        initComponents();
    }
    
    /**
     * Initialise les composants de l'interface
     */
    private void initComponents() {
        // Création du panneau principal avec un BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        
        // Création du panneau d'onglets
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Ajout des onglets
        tabbedPane.addTab("Clients", createClientPanel());
        tabbedPane.addTab("Pizzas", createPizzaPanel());
        tabbedPane.addTab("Commandes", createCommandePanel());
        tabbedPane.addTab("Livraisons", createLivraisonPanel());
        tabbedPane.addTab("Statistiques", createStatistiquesPanel());
        
        // Création de la barre de menu
        createMenuBar();
    }
    
    /**
     * Crée le panneau de gestion des clients
     * @return Le panneau de clients
     */
    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // À implémenter : liste des clients, formulaire d'ajout/modification, etc.
        //JLabel label = new JLabel("Gestion des clients");
        //label.setHorizontalAlignment(JLabel.CENTER);
        //panel.add(label, BorderLayout.CENTER);
        JTextField champRecherche = new JTextField(20);
        JButton boutonRecherche = new JButton("Rechercher");
        JButton boutonAjouter = new JButton("Ajouter");
        
        panel.add(champRecherche, BorderLayout.CENTER);
        panel.add(boutonRecherche, BorderLayout.EAST);
        panel.add(boutonAjouter, BorderLayout.SOUTH);

        JPanel panneauRecherche = new JPanel();
        panneauRecherche.add(new JLabel("🔍 Rechercher :"));
        panneauRecherche.add(champRecherche);
        panneauRecherche.add(boutonRecherche);
        panneauRecherche.add(boutonAjouter);

        panel.add(panneauRecherche, BorderLayout.NORTH);

        ClientDAO clientDAO = new ClientDAO();

        List<Client> clients = clientDAO.trouverTous();
        
        // Définir manuellement les noms des colonnes qui existent dans la base de données
        String[] colonnes = {"id_client", "nom", "prenom", "adresse", "telephone", "solde_compte", "nb_pizzas_achetees"};
        
        // Créer un tableau de données avec la taille correcte
        Object[][] donnees = new Object[clients.size()][colonnes.length];
        
        // Remplir le tableau avec les données de chaque client
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            donnees[i] = new Object[] { 
                client.getIdClient(), 
                client.getNom(), 
                client.getPrenom(), 
                client.getAdresse(),
                client.getTelephone(),
                client.getSoldeCompte(),
                client.getNbPizzasAchetees()
            };
        }

        DefaultTableModel modele = new DefaultTableModel(donnees, colonnes);
        JTable table = new JTable(modele);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton boutonModifier = new JButton("Modifier");
        JButton boutonSupprimer = new JButton("Supprimer");

        JPanel panneauActions = new JPanel();
        panneauActions.add(boutonModifier);
        panneauActions.add(boutonSupprimer);

        panel.add(panneauActions, BorderLayout.SOUTH);

        boutonModifier.addActionListener(e -> {
            // Vérifier qu'une ligne est sélectionnée
            int ligneSelectionnee = table.getSelectedRow();
            if (ligneSelectionnee == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à modifier", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Récupérer l'ID du client sélectionné
            int idClient = (int) table.getValueAt(ligneSelectionnee, 0);
            
            // Récupérer le client depuis la base de données
            Client client = clientDAO.trouverParId(idClient);
            
            if (client == null) {
                JOptionPane.showMessageDialog(this, "Client introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer une fenêtre de modification
            JDialog fenetreModification = new JDialog(this, "Modification d'un client", true);
            fenetreModification.setSize(400, 300);
            fenetreModification.setLocationRelativeTo(this);
            fenetreModification.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // Créer un panneau avec un GridLayout pour organiser les champs
            JPanel panneauModification = new JPanel(new GridLayout(6, 2, 5, 5));
            panneauModification.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Ajouter les champs pour chaque colonne existante
            panneauModification.add(new JLabel("Nom :"));
            JTextField champNom = new JTextField(client.getNom());
            panneauModification.add(champNom);
            
            panneauModification.add(new JLabel("Prénom :"));
            JTextField champPrenom = new JTextField(client.getPrenom());
            panneauModification.add(champPrenom);
            
            panneauModification.add(new JLabel("Adresse :"));
            JTextField champAdresse = new JTextField(client.getAdresse());
            panneauModification.add(champAdresse);
            
            panneauModification.add(new JLabel("Téléphone :"));
            JTextField champTelephone = new JTextField(client.getTelephone());
            panneauModification.add(champTelephone);
            
            panneauModification.add(new JLabel("Solde compte :"));
            JTextField champSolde = new JTextField(String.valueOf(client.getSoldeCompte()));
            panneauModification.add(champSolde);
            
            panneauModification.add(new JLabel("Nb. pizzas achetées :"));
            JTextField champNbPizzas = new JTextField(String.valueOf(client.getNbPizzasAchetees()));
            panneauModification.add(champNbPizzas);
            
            // Panneau pour les boutons
            JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton boutonEnregistrer = new JButton("Enregistrer");
            JButton boutonAnnuler = new JButton("Annuler");
            
            panneauBoutons.add(boutonEnregistrer);
            panneauBoutons.add(boutonAnnuler);
            
            // Action du bouton Enregistrer
            boutonEnregistrer.addActionListener(event -> {
                try {
                    // Mettre à jour les informations du client
                    client.setNom(champNom.getText());
                    client.setPrenom(champPrenom.getText());
                    client.setAdresse(champAdresse.getText());
                    client.setTelephone(champTelephone.getText());
                    client.setSoldeCompte(Double.parseDouble(champSolde.getText().replace(",", ".")));
                    client.setNbPizzasAchetees(Integer.parseInt(champNbPizzas.getText()));
                    
                    // Enregistrer les modifications
                    if (clientDAO.mettreAJour(client)) {
                        JOptionPane.showMessageDialog(fenetreModification, "Client modifié avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Mettre à jour le tableau
                        table.setValueAt(client.getNom(), ligneSelectionnee, 1);
                        table.setValueAt(client.getPrenom(), ligneSelectionnee, 2);
                        table.setValueAt(client.getAdresse(), ligneSelectionnee, 3);
                        table.setValueAt(client.getTelephone(), ligneSelectionnee, 4);
                        table.setValueAt(client.getSoldeCompte(), ligneSelectionnee, 5);
                        table.setValueAt(client.getNbPizzasAchetees(), ligneSelectionnee, 6);
                        
                        fenetreModification.dispose();
                    } else {
                        JOptionPane.showMessageDialog(fenetreModification, "Erreur lors de la modification du client", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(fenetreModification, "Veuillez entrer des valeurs numériques valides pour le solde et le nombre de pizzas", "Erreur de format", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Action du bouton Annuler
            boutonAnnuler.addActionListener(event -> fenetreModification.dispose());
            
            // Ajouter les panneaux à la fenêtre
            fenetreModification.setLayout(new BorderLayout());
            fenetreModification.add(panneauModification, BorderLayout.CENTER);
            fenetreModification.add(panneauBoutons, BorderLayout.SOUTH);
            
            // Afficher la fenêtre
            fenetreModification.setVisible(true);
        });

        boutonSupprimer.addActionListener(e -> {
            // Vérifier qu'une ligne est sélectionnée
            int ligneSelectionnee = table.getSelectedRow();
            if (ligneSelectionnee == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Récupérer l'ID du client sélectionné
            int idClient = (int) table.getValueAt(ligneSelectionnee, 0);
            
            // Demander confirmation
            int reponse = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer ce client ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (reponse == JOptionPane.YES_OPTION) {
                // Supprimer le client
                if (clientDAO.supprimer(idClient)) {
                    // Supprimer la ligne du tableau
                    ((DefaultTableModel) table.getModel()).removeRow(ligneSelectionnee);
                    JOptionPane.showMessageDialog(this, "Client supprimé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        boutonAjouter.addActionListener(e -> {
            // Créer une fenêtre d'ajout
            JDialog fenetreAjout = new JDialog(this, "Ajouter un client", true);
            fenetreAjout.setSize(400, 300);
            fenetreAjout.setLocationRelativeTo(this);
            fenetreAjout.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // Créer un panneau avec un GridLayout pour organiser les champs
            JPanel panneauAjout = new JPanel(new GridLayout(6, 2, 5, 5));
            panneauAjout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Ajouter les champs pour chaque colonne existante
            panneauAjout.add(new JLabel("Nom :"));
            JTextField champNom = new JTextField();
            panneauAjout.add(champNom);
            
            panneauAjout.add(new JLabel("Prénom :"));
            JTextField champPrenom = new JTextField();
            panneauAjout.add(champPrenom);
            
            panneauAjout.add(new JLabel("Adresse :"));
            JTextField champAdresse = new JTextField();
            panneauAjout.add(champAdresse);
            
            panneauAjout.add(new JLabel("Téléphone :"));
            JTextField champTelephone = new JTextField();
            panneauAjout.add(champTelephone);
            
            panneauAjout.add(new JLabel("Solde compte :"));
            JTextField champSolde = new JTextField("0.0");
            panneauAjout.add(champSolde);
            
            panneauAjout.add(new JLabel("Nb. pizzas achetées :"));
            JTextField champNbPizzas = new JTextField("0");
            panneauAjout.add(champNbPizzas);
            
            // Panneau pour les boutons
            JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton boutonEnregistrer = new JButton("Enregistrer");
            JButton boutonAnnuler = new JButton("Annuler");
            
            panneauBoutons.add(boutonEnregistrer);
            panneauBoutons.add(boutonAnnuler);
            
            // Action du bouton Enregistrer
            boutonEnregistrer.addActionListener(event -> {
                try {
                    // Vérifier les champs obligatoires
                    if (champNom.getText().trim().isEmpty() || 
                        champPrenom.getText().trim().isEmpty() || 
                        champAdresse.getText().trim().isEmpty() || 
                        champTelephone.getText().trim().isEmpty()) {
                        
                        JOptionPane.showMessageDialog(fenetreAjout, 
                            "Veuillez remplir tous les champs obligatoires", 
                            "Champs incomplets", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Créer un nouveau client
                    Client client = new Client();
                    client.setNom(champNom.getText().trim());
                    client.setPrenom(champPrenom.getText().trim());
                    client.setAdresse(champAdresse.getText().trim());
                    client.setTelephone(champTelephone.getText().trim());
                    client.setSoldeCompte(Double.parseDouble(champSolde.getText().replace(",", ".")));
                    client.setNbPizzasAchetees(Integer.parseInt(champNbPizzas.getText()));
                    
                    // Enregistrer le client
                    if (clientDAO.inserer(client)) {
                        JOptionPane.showMessageDialog(fenetreAjout, 
                            "Client ajouté avec succès !", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Ajouter le client à la table
                        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                        tableModel.addRow(new Object[]{
                            client.getIdClient(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getAdresse(),
                            client.getTelephone(),
                            client.getSoldeCompte(),
                            client.getNbPizzasAchetees()
                        });
                        
                        fenetreAjout.dispose();
                    } else {
                        JOptionPane.showMessageDialog(fenetreAjout, 
                            "Erreur lors de l'ajout du client", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(fenetreAjout, 
                        "Veuillez entrer des valeurs numériques valides pour le solde et le nombre de pizzas", 
                        "Erreur de format", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Action du bouton Annuler
            boutonAnnuler.addActionListener(event -> fenetreAjout.dispose());
            
            // Ajouter les panneaux à la fenêtre
            fenetreAjout.setLayout(new BorderLayout());
            fenetreAjout.add(panneauAjout, BorderLayout.CENTER);
            fenetreAjout.add(panneauBoutons, BorderLayout.SOUTH);
            
            // Afficher la fenêtre
            fenetreAjout.setVisible(true);
        });

        boutonRecherche.addActionListener(e -> {
            String termeRecherche = champRecherche.getText().trim();
            
            if (termeRecherche.isEmpty()) {
                // Si le champ de recherche est vide, afficher tous les clients
                List<Client> tousLesClients = clientDAO.trouverTous();
                mettreAJourTableau(table, tousLesClients);
                return;
            }
            
            // Rechercher les clients par nom ou prénom
            List<Client> clientsTrouves = clientDAO.rechercherParNomOuPrenom(termeRecherche);
            
            if (clientsTrouves.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Aucun client trouvé pour la recherche : " + termeRecherche,
                    "Recherche sans résultat", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            
            // Mettre à jour le tableau avec les résultats
            mettreAJourTableau(table, clientsTrouves);
        });
        
        // Ajouter aussi un événement sur la touche Entrée dans le champ de recherche
        champRecherche.addActionListener(e -> {
            boutonRecherche.doClick(); // Déclenche le clic sur le bouton recherche
        });

        return panel;
    }
    
    /**
     * Crée le panneau de gestion des pizzas
     * @return Le panneau de pizzas
     */
    private JPanel createPizzaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panneau de recherche en haut
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton addButton = new JButton("Ajouter une pizza");
        
        searchPanel.add(new JLabel("🔍 Rechercher :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panneau principal avec scroll pour les pizzas
        JPanel pizzasPanel = new JPanel();
        pizzasPanel.setLayout(new BoxLayout(pizzasPanel, BoxLayout.Y_AXIS));
        
        // Récupération des données
        PizzaDAO pizzaDAO = new PizzaDAO();
        TailleDAO tailleDAO = new TailleDAO();
        
        List<Pizza> pizzas = pizzaDAO.trouverTous();
        List<Taille> tailles = tailleDAO.trouverTous();
        
        // Création d'un panneau pour chaque pizza
        for (Pizza pizza : pizzas) {
            // Panneau pour une pizza
            JPanel pizzaPanel = new JPanel(new BorderLayout());
            pizzaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)
            ));
            
            // Trouver l'image correspondante basée sur le nom de la pizza
            String imageName = trouverNomImagePizza(pizza.getNom());
            ImageIcon imageIcon = chargerImagePizza(imageName);
            
            // Panneau d'image à gauche
            JLabel imageLabel = new JLabel();
            if (imageIcon != null) {
                imageLabel.setIcon(imageIcon);
            } else {
                imageLabel.setText("Image non disponible");
            }
            imageLabel.setPreferredSize(new Dimension(200, 150));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(imageLabel, BorderLayout.CENTER);
            imagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
            
            // Panneau d'informations à droite
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            
            // Titre de la pizza
            JLabel titleLabel = new JLabel(pizza.getNom());
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Ingrédients
            List<Ingredient> ingredients = pizza.getIngredients();
            StringBuilder ingredientsStr = new StringBuilder("<html><b>Ingrédients :</b> ");
            
            if (ingredients != null && !ingredients.isEmpty()) {
                for (int i = 0; i < ingredients.size(); i++) {
                    ingredientsStr.append(ingredients.get(i).getNom());
                    if (i < ingredients.size() - 1) {
                        ingredientsStr.append(", ");
                    }
                }
            } else {
                ingredientsStr.append("Aucun ingrédient renseigné");
            }
            ingredientsStr.append("</html>");
            
            JLabel ingredientsLabel = new JLabel(ingredientsStr.toString());
            ingredientsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Prix selon les tailles
            JPanel prixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            prixPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            prixPanel.add(new JLabel("<html><b>Prix de base :</b> " + String.format("%.2f€", pizza.getPrixBase()) + "</html>"));
            
            // Prix selon les tailles si elles existent
            if (!tailles.isEmpty()) {
                JPanel taillesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                taillesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                taillesPanel.add(new JLabel("<html><b>Prix par taille :</b></html>"));
                
                for (Taille taille : tailles) {
                    double prix = pizza.calculerPrix(taille);
                    taillesPanel.add(new JLabel(String.format("%s: %.2f€", taille.getLibelle(), prix)));
                }
                
                infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                infoPanel.add(taillesPanel);
            }
            
            // Boutons d'action
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JButton editButton = new JButton("Modifier");
            JButton deleteButton = new JButton("Supprimer");
            
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            
            // Ajouter tous les composants au panneau d'info
            infoPanel.add(titleLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(ingredientsLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(prixPanel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(buttonPanel);
            
            // Ajout des deux panneaux au panneau de la pizza
            pizzaPanel.add(imagePanel, BorderLayout.WEST);
            pizzaPanel.add(infoPanel, BorderLayout.CENTER);
            
            // Ajouter le panneau de la pizza au panneau général
            pizzasPanel.add(pizzaPanel);
            pizzasPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            // Gestion des événements pour les boutons
            final Pizza currentPizza = pizza;
            
            // Action du bouton Modifier
            editButton.addActionListener(e -> {
                // Implémentation de la modification de pizza
                JOptionPane.showMessageDialog(this, "Modification de la pizza " + currentPizza.getNom() + " à implémenter");
            });
            
            // Action du bouton Supprimer
            deleteButton.addActionListener(e -> {
                int reponse = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer la pizza " + currentPizza.getNom() + " ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (reponse == JOptionPane.YES_OPTION) {
                    boolean supprime = pizzaDAO.supprimer(currentPizza.getIdPizza());
                    if (supprime) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Pizza supprimée avec succès",
                            "Suppression réussie",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Rafraîchir le panneau
                        refreshPizzaPanel();
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Erreur lors de la suppression de la pizza",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
        }
        
        // Panneau avec défilement
        JScrollPane scrollPane = new JScrollPane(pizzasPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action du bouton de recherche
        searchButton.addActionListener(e -> {
            String termeRecherche = searchField.getText().trim();
            
            if (termeRecherche.isEmpty()) {
                // Si le champ de recherche est vide, afficher toutes les pizzas
                refreshPizzaPanel();
                return;
            }
            
            // Rechercher les pizzas par nom
            List<Pizza> pizzasTrouvees = pizzaDAO.rechercherParNom(termeRecherche);
            
            if (pizzasTrouvees.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Aucune pizza trouvée pour la recherche : " + termeRecherche,
                    "Recherche sans résultat",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            
            // Mettre à jour l'affichage avec les résultats
            refreshPizzaPanel(pizzasTrouvees);
        });
        
        // Recherche à la touche Entrée
        searchField.addActionListener(e -> searchButton.doClick());
        
        // Action du bouton Ajouter
        addButton.addActionListener(e -> {
            // Créer une fenêtre d'ajout
            JDialog fenetreAjout = new JDialog(this, "Ajouter une pizza", true);
            fenetreAjout.setSize(400, 300);
            fenetreAjout.setLocationRelativeTo(this);
            fenetreAjout.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // Créer un panneau avec un GridLayout pour organiser les champs
            JPanel panneauAjout = new JPanel(new GridLayout(6, 2, 5, 5));
            panneauAjout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Ajouter les champs pour chaque colonne existante
            panneauAjout.add(new JLabel("Nom :"));
            JTextField champNom = new JTextField();
            panneauAjout.add(champNom);
            
            panneauAjout.add(new JLabel("Prénom :"));
            JTextField champPrenom = new JTextField();
            panneauAjout.add(champPrenom);
            
            panneauAjout.add(new JLabel("Adresse :"));
            JTextField champAdresse = new JTextField();
            panneauAjout.add(champAdresse);
            
            panneauAjout.add(new JLabel("Téléphone :"));
            JTextField champTelephone = new JTextField();
            panneauAjout.add(champTelephone);
            
            panneauAjout.add(new JLabel("Solde compte :"));
            JTextField champSolde = new JTextField("0.0");
            panneauAjout.add(champSolde);
            
            panneauAjout.add(new JLabel("Nb. pizzas achetées :"));
            JTextField champNbPizzas = new JTextField("0");
            panneauAjout.add(champNbPizzas);
            
            // Panneau pour les boutons
            JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton boutonEnregistrer = new JButton("Enregistrer");
            JButton boutonAnnuler = new JButton("Annuler");
            
            panneauBoutons.add(boutonEnregistrer);
            panneauBoutons.add(boutonAnnuler);
        });
        
        return panel;
    }
    
    /**
     * Rafraîchit le panneau des pizzas
     */
    private void refreshPizzaPanel() {
        // Recréer le panneau avec toutes les pizzas
        PizzaDAO pizzaDAO = new PizzaDAO();
        List<Pizza> pizzas = pizzaDAO.trouverTous();
        refreshPizzaPanel(pizzas);
    }
    
    /**
     * Rafraîchit le panneau des pizzas avec la liste fournie
     * @param pizzas Liste de pizzas à afficher
     */
    private void refreshPizzaPanel(List<Pizza> pizzas) {
        // Remplacer l'onglet des pizzas
        tabbedPane.setComponentAt(1, createPizzaPanel());
    }
    
    /**
     * Trouve le nom du fichier image correspondant à une pizza
     * @param nomPizza Nom de la pizza
     * @return Nom du fichier d'image
     */
    private String trouverNomImagePizza(String nomPizza) {
        // Correspondance entre noms de pizzas et noms de fichiers
        String nomSansAccent = nomPizza.toLowerCase()
            .replace("é", "e")
            .replace("è", "e")
            .replace("ê", "e")
            .replace("ë", "e")
            .replace("à", "a")
            .replace("â", "a")
            .replace("ô", "o")
            .replace("ù", "u")
            .replace("ü", "u")
            .replace("ç", "c")
            .replace(" ", "");
        
        // Mappages spécifiques pour certaines pizzas
        if (nomSansAccent.contains("margarita") || nomSansAccent.contains("margherita")) {
            return "margaritha";
        } else if (nomSansAccent.contains("calzone")) {
            return "calzone";
        } else if (nomSansAccent.contains("haw") || nomSansAccent.contains("hawaii")) {
            return "hawaienne";
        } else if (nomSansAccent.contains("vege") || nomSansAccent.contains("vegetarienne")) {
            return "vege";
        } else if (nomSansAccent.contains("quatrefromages") || nomSansAccent.contains("4fromages")) {
            return "quattreF";
        } else if (nomSansAccent.contains("peperoni") || nomSansAccent.contains("pepperoni")) {
            return "peperoni";
        } else if (nomSansAccent.contains("reine")) {
            return "reine";
        }
        
        // Par défaut, on retourne le nom sans accent
        return nomSansAccent;
    }
    
    /**
     * Charge une image de pizza à partir du dossier assets
     * @param nomImage Nom de l'image (sans extension)
     * @return ImageIcon redimensionnée ou null si l'image n'est pas trouvée
     */
    private ImageIcon chargerImagePizza(String nomImage) {
        try {
            // Essayer d'abord avec l'extension .jpg
            File imageFile = new File("assets/" + nomImage + ".jpg");
            if (!imageFile.exists()) {
                // Essayer avec l'extension .jpeg
                imageFile = new File("assets/" + nomImage + ".jpeg");
            }
            
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image image = originalIcon.getImage();
                
                // Redimensionner l'image à une taille raisonnable
                Image resizedImage = image.getScaledInstance(180, 140, Image.SCALE_SMOOTH);
                return new ImageIcon(resizedImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de pizza: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Crée le panneau de gestion des commandes
     * @return Le panneau de commandes
     */
    private JPanel createCommandePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // À implémenter : création de commandes, suivi des commandes, etc.
        JLabel label = new JLabel("Gestion des commandes");
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le panneau de gestion des livraisons
     * @return Le panneau de livraisons
     */
    private JPanel createLivraisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // À implémenter : suivi des livraisons, livreurs, véhicules, etc.
        JLabel label = new JLabel("Gestion des livraisons");
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le panneau des statistiques
     * @return Le panneau de statistiques
     */
    private JPanel createStatistiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // À implémenter : affichage des différentes statistiques demandées
        JLabel label = new JLabel("Statistiques");
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée la barre de menu
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        // Menu Fichier
        JMenu menuFichier = new JMenu("Fichier");
        menuBar.add(menuFichier);
        
        JMenuItem menuItemQuitter = new JMenuItem("Quitter");
        menuItemQuitter.addActionListener(e -> {
            DatabaseConfig.closeConnection();
            System.exit(0);
        });
        menuFichier.add(menuItemQuitter);
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        menuBar.add(menuAide);
        
        JMenuItem menuItemAPropos = new JMenuItem("À propos");
        menuItemAPropos.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "RaPizz - Application de gestion de pizzeria\n" +
                    "Projet BDD ESIEE Paris\n" +
                    "© 2023",
                    "À propos",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        menuAide.add(menuItemAPropos);
    }

    private void mettreAJourTableau(JTable table, List<Client> clients) {
        DefaultTableModel modele = (DefaultTableModel) table.getModel();
        modele.setRowCount(0);
        for (Client client : clients) {
            modele.addRow(new Object[]{
                client.getIdClient(),
                client.getNom(),
                client.getPrenom(),
                client.getAdresse(),
                client.getTelephone(),
                client.getSoldeCompte(),
                client.getNbPizzasAchetees()
            });
        }
    }
} 