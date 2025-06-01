package fr.esiee.rapizz.view;

import fr.esiee.rapizz.util.DatabaseConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import fr.esiee.rapizz.dao.*;
import fr.esiee.rapizz.model.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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
        JButton resetButton = new JButton("Réinitialiser");
        
        searchPanel.add(new JLabel("🔍 Rechercher (nom ou ingrédient) :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.add(addButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panneau principal avec scroll pour les pizzas
        JPanel pizzasPanel = new JPanel();
        pizzasPanel.setLayout(new BoxLayout(pizzasPanel, BoxLayout.Y_AXIS));
        
        // Charger toutes les pizzas au démarrage
        chargerPizzasDansPanel(pizzasPanel, null);
        
        // Action du bouton de recherche
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            chargerPizzasDansPanel(pizzasPanel, query.isEmpty() ? null : query);
        });
        
        // Action du bouton de réinitialisation
        resetButton.addActionListener(e -> {
            searchField.setText("");
            chargerPizzasDansPanel(pizzasPanel, null);
        });
        
        // Recherche en temps réel lors de la saisie
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                rechercherEnTempsReel();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                rechercherEnTempsReel();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                rechercherEnTempsReel();
            }
            
            private void rechercherEnTempsReel() {
                String query = searchField.getText().trim();
                chargerPizzasDansPanel(pizzasPanel, query.isEmpty() ? null : query);
            }
        });
        
        // Recherche avec la touche Entrée
        searchField.addActionListener(e -> searchButton.doClick());
        
        // Ajouter le panneau des pizzas avec scroll
        JScrollPane scrollPane = new JScrollPane(pizzasPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action du bouton d'ajout
        addButton.addActionListener(e -> {
            // Création d'une fenêtre d'ajout de pizza
            JDialog addPizzaDialog = new JDialog(this, "Ajouter une nouvelle pizza", true);
            addPizzaDialog.setSize(600, 500);
            addPizzaDialog.setLocationRelativeTo(this);
            addPizzaDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            // Panneau principal avec BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Panneau de formulaire avec GridBagLayout pour plus de flexibilité
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Champ nom de la pizza
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Nom de la pizza :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField nameField = new JTextField(20);
            formPanel.add(nameField, gbc);
            
            // Champ prix de base
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Prix de base (€) :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField priceField = new JTextField(20);
            formPanel.add(priceField, gbc);
            
            // Sélection d'image
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Image :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            
            JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JTextField imagePathField = new JTextField(15);
            imagePathField.setEditable(false);
            JButton browseButton = new JButton("Parcourir...");
            imagePanel.add(imagePathField);
            imagePanel.add(Box.createHorizontalStrut(5));
            imagePanel.add(browseButton);
            formPanel.add(imagePanel, gbc);
            
            // Action du bouton Parcourir
            browseButton.addActionListener(imgEvent -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Sélectionner une image de pizza");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) return true;
                        String name = f.getName().toLowerCase();
                        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                               name.endsWith(".png") || name.endsWith(".gif");
                    }
                    
                    @Override
                    public String getDescription() {
                        return "Images (*.jpg, *.jpeg, *.png, *.gif)";
                    }
                });
                
                int result = fileChooser.showOpenDialog(addPizzaDialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                }
            });
            
            // Sélection des ingrédients
            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Ingrédients :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
            
            // Récupération de tous les ingrédients
            IngredientDAO ingredientDAO = new IngredientDAO();
            List<Ingredient> allIngredients = ingredientDAO.trouverTous();
            
            // Panneau avec JCheckBox pour sélectionner les ingrédients
            JPanel ingredientsPanel = new JPanel();
            ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
            ingredientsPanel.setBorder(BorderFactory.createTitledBorder("Sélectionnez les ingrédients"));
            
            // Map pour stocker les checkboxes par ingrédient
            Map<Ingredient, JCheckBox> ingredientCheckboxes = new HashMap<>();
            
            for (Ingredient ingredient : allIngredients) {
                JCheckBox checkBox = new JCheckBox(ingredient.getNom());
                checkBox.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                ingredientCheckboxes.put(ingredient, checkBox);
                ingredientsPanel.add(checkBox);
            }
            
            // Bouton pour ajouter un nouvel ingrédient
            JButton addIngredientButton = new JButton("+ Nouvel ingrédient");
            addIngredientButton.setPreferredSize(new Dimension(150, 30));
            ingredientsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            ingredientsPanel.add(addIngredientButton);
            
            // Panneau de défilement pour les ingrédients
            JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsPanel);
            ingredientsScrollPane.setPreferredSize(new Dimension(300, 200));
            ingredientsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            formPanel.add(ingredientsScrollPane, gbc);
            
            // Action du bouton pour ajouter un nouvel ingrédient
            addIngredientButton.addActionListener(event -> {
                JDialog addIngredientDialog = new JDialog(addPizzaDialog, "Ajouter un ingrédient", true);
                addIngredientDialog.setSize(350, 200);
                addIngredientDialog.setLocationRelativeTo(addPizzaDialog);
                
                JPanel ingPanel = new JPanel(new GridBagLayout());
                GridBagConstraints ingGbc = new GridBagConstraints();
                ingGbc.insets = new Insets(10, 10, 10, 10);
                ingGbc.anchor = GridBagConstraints.WEST;
                
                ingGbc.gridx = 0; ingGbc.gridy = 0;
                ingPanel.add(new JLabel("Nom de l'ingrédient :"), ingGbc);
                ingGbc.gridx = 1; ingGbc.fill = GridBagConstraints.HORIZONTAL; ingGbc.weightx = 1.0;
                JTextField ingNameField = new JTextField(15);
                ingPanel.add(ingNameField, ingGbc);
                
                ingGbc.gridx = 0; ingGbc.gridy = 1; ingGbc.fill = GridBagConstraints.NONE; ingGbc.weightx = 0;
                ingPanel.add(new JLabel("Stock initial :"), ingGbc);
                ingGbc.gridx = 1; ingGbc.fill = GridBagConstraints.HORIZONTAL; ingGbc.weightx = 1.0;
                JTextField stockField = new JTextField("100");
                ingPanel.add(stockField, ingGbc);
                
                JPanel ingButtonPanel = new JPanel(new FlowLayout());
                JButton ingSaveButton = new JButton("Ajouter");
                JButton ingCancelButton = new JButton("Annuler");
                ingButtonPanel.add(ingSaveButton);
                ingButtonPanel.add(ingCancelButton);
                
                JPanel ingMainPanel = new JPanel(new BorderLayout());
                ingMainPanel.add(ingPanel, BorderLayout.CENTER);
                ingMainPanel.add(ingButtonPanel, BorderLayout.SOUTH);
                
                addIngredientDialog.setContentPane(ingMainPanel);
                
                // Action du bouton de sauvegarde d'ingrédient
                ingSaveButton.addActionListener(ingEvent -> {
                    try {
                        String nomIngredient = ingNameField.getText().trim();
                        int stockInitial = Integer.parseInt(stockField.getText().trim());
                        
                        if (nomIngredient.isEmpty()) {
                            JOptionPane.showMessageDialog(addIngredientDialog, 
                                "Veuillez saisir un nom d'ingrédient", 
                                "Champ requis", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Créer et sauvegarder le nouvel ingrédient
                        Ingredient newIngredient = new Ingredient(nomIngredient, stockInitial);
                        if (ingredientDAO.inserer(newIngredient)) {
                            // Ajouter l'ingrédient à la liste et la checkbox
                            JCheckBox newCheckBox = new JCheckBox(newIngredient.getNom());
                            newCheckBox.setSelected(true); // Sélectionner par défaut
                            newCheckBox.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                            ingredientCheckboxes.put(newIngredient, newCheckBox);
                            
                            // Ajouter avant le bouton
                            ingredientsPanel.remove(addIngredientButton);
                            ingredientsPanel.remove(ingredientsPanel.getComponentCount() - 1); // Retirer l'espacement
                            ingredientsPanel.add(newCheckBox);
                            ingredientsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                            ingredientsPanel.add(addIngredientButton);
                            
                            // Rafraîchir l'affichage
                            ingredientsPanel.revalidate();
                            ingredientsPanel.repaint();
                            
                            addIngredientDialog.dispose();
                            
                            JOptionPane.showMessageDialog(addPizzaDialog, 
                                "Ingrédient '" + nomIngredient + "' ajouté avec succès !", 
                                "Succès", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(addIngredientDialog, 
                                "Erreur lors de l'ajout de l'ingrédient", 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(addIngredientDialog, 
                            "Veuillez saisir un nombre valide pour le stock", 
                            "Erreur de format", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                // Action du bouton d'annulation d'ingrédient
                ingCancelButton.addActionListener(ingEvent -> addIngredientDialog.dispose());
                
                addIngredientDialog.setVisible(true);
            });
            
            // Panneau pour les boutons principaux
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton saveButton = new JButton("Enregistrer la pizza");
            JButton cancelButton = new JButton("Annuler");
            
            saveButton.setPreferredSize(new Dimension(150, 35));
            cancelButton.setPreferredSize(new Dimension(100, 35));
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            // Ajout des panneaux à la fenêtre
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            addPizzaDialog.setContentPane(mainPanel);
            
            // Action du bouton d'enregistrement de pizza
            saveButton.addActionListener(event -> {
                try {
                    String nomPizza = nameField.getText().trim();
                    String prixText = priceField.getText().trim().replace(',', '.');
                    String imagePath = imagePathField.getText().trim();
                    
                    // Validation des champs
                    if (nomPizza.isEmpty()) {
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Veuillez saisir un nom pour la pizza", 
                            "Champ requis", 
                            JOptionPane.WARNING_MESSAGE);
                        nameField.requestFocus();
                        return;
                    }
                    
                    if (prixText.isEmpty()) {
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Veuillez saisir un prix pour la pizza", 
                            "Champ requis", 
                            JOptionPane.WARNING_MESSAGE);
                        priceField.requestFocus();
                        return;
                    }
                    
                    double prixBase;
                    try {
                        prixBase = Double.parseDouble(prixText);
                        if (prixBase <= 0) {
                            throw new NumberFormatException("Le prix doit être positif");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Veuillez saisir un prix valide (ex: 9.99)", 
                            "Erreur de format", 
                            JOptionPane.ERROR_MESSAGE);
                        priceField.requestFocus();
                        return;
                    }
                    
                    // Vérifier qu'au moins un ingrédient est sélectionné
                    boolean hasIngredients = false;
                    for (JCheckBox checkBox : ingredientCheckboxes.values()) {
                        if (checkBox.isSelected()) {
                            hasIngredients = true;
                            break;
                        }
                    }
                    
                    if (!hasIngredients) {
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Veuillez sélectionner au moins un ingrédient", 
                            "Ingrédients requis", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Créer la nouvelle pizza
                    Pizza nouvellePizza = new Pizza(nomPizza, prixBase);
                    
                    // Ajouter les ingrédients sélectionnés
                    for (Map.Entry<Ingredient, JCheckBox> entry : ingredientCheckboxes.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            nouvellePizza.ajouterIngredient(entry.getKey());
                        }
                    }
                    
                    // Sauvegarder la pizza dans la base de données
                    PizzaDAO pizzaDAO = new PizzaDAO();
                    boolean success = pizzaDAO.inserer(nouvellePizza);
                    
                    if (success) {
                        // Si une image a été sélectionnée, la copier dans le dossier assets
                        if (!imagePath.isEmpty()) {
                            try {
                                File sourceFile = new File(imagePath);
                                String fileName = sourceFile.getName();
                                String extension = fileName.substring(fileName.lastIndexOf('.'));
                                
                                // Déterminer le nom du fichier de destination basé sur le nom de la pizza
                                String destinationFileName = trouverNomImagePizza(nomPizza) + extension;
                                
                                // Créer le dossier assets s'il n'existe pas
                                File assetsDir = new File("assets");
                                if (!assetsDir.exists()) {
                                    assetsDir.mkdir();
                                }
                                
                                // Copier le fichier
                                File destinationFile = new File("assets/" + destinationFileName);
                                java.nio.file.Files.copy(
                                    sourceFile.toPath(),
                                    destinationFile.toPath(),
                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                                );
                                
                                System.out.println("Image copiée vers : " + destinationFile.getAbsolutePath());
                            } catch (Exception ex) {
                                System.err.println("Erreur lors de la copie de l'image : " + ex.getMessage());
                                // On continue même si l'image n'a pas pu être copiée
                            }
                        }
                        
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Pizza '" + nomPizza + "' ajoutée avec succès !", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir l'affichage des pizzas
                        chargerPizzasDansPanel(pizzasPanel, null);
                        
                        addPizzaDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(addPizzaDialog, 
                            "Erreur lors de l'ajout de la pizza", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addPizzaDialog, 
                        "Erreur inattendue : " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            // Action du bouton d'annulation
            cancelButton.addActionListener(event -> addPizzaDialog.dispose());
            
            addPizzaDialog.setVisible(true);
        });
        
        return panel;
    }
    
    /**
     * Charge les pizzas dans le panneau avec filtrage optionnel
     * @param pizzasPanel Le panneau où afficher les pizzas
     * @param query Le terme de recherche (null pour afficher toutes les pizzas)
     */
    private void chargerPizzasDansPanel(JPanel pizzasPanel, String query) {
        // Vider le panneau
        pizzasPanel.removeAll();
        
        // Récupération des données
        PizzaDAO pizzaDAO = new PizzaDAO();
        
        List<Pizza> pizzas;
        if (query == null || query.trim().isEmpty()) {
            pizzas = pizzaDAO.trouverTous();
        } else {
            pizzas = pizzaDAO.rechercherParNom(query);
        }
        
        // Si aucune pizza trouvée avec la recherche
        if (pizzas.isEmpty() && query != null && !query.trim().isEmpty()) {
            JLabel noResultLabel = new JLabel("Aucune pizza trouvée pour : " + query);
            noResultLabel.setHorizontalAlignment(JLabel.CENTER);
            noResultLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            pizzasPanel.add(noResultLabel);
        } else {
        // Création d'un panneau pour chaque pizza
        for (Pizza pizza : pizzas) {
                // Panneau principal pour une pizza avec une hauteur fixe
                JPanel singlePizzaPanel = new JPanel(new BorderLayout());
                singlePizzaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)
            ));
                singlePizzaPanel.setPreferredSize(new Dimension(800, 200));
                singlePizzaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
                
                // Panneau d'image à gauche avec taille fixe
                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.setPreferredSize(new Dimension(180, 160));
                imagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
                
                // Trouver et charger l'image
            String imageName = trouverNomImagePizza(pizza.getNom());
            ImageIcon imageIcon = chargerImagePizza(imageName);
            
            JLabel imageLabel = new JLabel();
            if (imageIcon != null) {
                imageLabel.setIcon(imageIcon);
            } else {
                    // Image par défaut si pas d'image trouvée
                    imageLabel.setText("<html><center>🍕<br/>Image<br/>non disponible</center></html>");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    imageLabel.setVerticalAlignment(JLabel.CENTER);
                    imageLabel.setOpaque(true);
                    imageLabel.setBackground(Color.LIGHT_GRAY);
                }
                imageLabel.setPreferredSize(new Dimension(160, 140));
            imagePanel.add(imageLabel, BorderLayout.CENTER);
            
                // Panneau d'informations à droite
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Titre de la pizza
                JLabel titleLabel = new JLabel(pizza.getNom());
                titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
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
                ingredientsLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
                
                // Prix de base
                JLabel prixLabel = new JLabel("<html><b>Prix de base :</b> " + String.format("%.2f€", pizza.getPrixBase()) + "</html>");
                prixLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                prixLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                
                // Espacement flexible pour pousser les boutons vers le bas
                Component verticalGlue = Box.createVerticalGlue();
                
                // Boutons d'action dans un panneau séparé
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
                buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JButton editButton = new JButton("Modifier");
            JButton deleteButton = new JButton("Supprimer");
            
                // Styliser les boutons
                editButton.setPreferredSize(new Dimension(80, 30));
                deleteButton.setPreferredSize(new Dimension(90, 30));
                
                buttonPanel.add(editButton);
                buttonPanel.add(Box.createHorizontalStrut(10)); // Espacement entre les boutons
                buttonPanel.add(deleteButton);
                
                // Ajouter tous les composants au panneau d'info dans l'ordre
                infoPanel.add(titleLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            infoPanel.add(ingredientsLabel);
                infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                infoPanel.add(prixLabel);
                infoPanel.add(verticalGlue); // Pousse les boutons vers le bas
                infoPanel.add(buttonPanel);
                
                // Assembler le panneau principal
                singlePizzaPanel.add(imagePanel, BorderLayout.WEST);
                singlePizzaPanel.add(infoPanel, BorderLayout.CENTER);
                
                // Ajouter le panneau de la pizza au panneau général
                pizzasPanel.add(singlePizzaPanel);
            pizzasPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
                // Gérer les événements des boutons
            final Pizza currentPizza = pizza;
            
                // Action du bouton Modifier
            editButton.addActionListener(e -> {
                // Création d'une fenêtre de modification de pizza
                    JDialog editPizzaDialog = new JDialog(this, "Modifier la pizza : " + currentPizza.getNom(), true);
                    editPizzaDialog.setSize(600, 500);
                editPizzaDialog.setLocationRelativeTo(this);
                editPizzaDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                
                // Panneau principal avec BorderLayout
                JPanel mainPanel = new JPanel(new BorderLayout());
                    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    
                    // Panneau de formulaire avec GridBagLayout
                    JPanel formPanel = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.anchor = GridBagConstraints.WEST;
                    
                    // Champ nom de la pizza (prérempli)
                    gbc.gridx = 0; gbc.gridy = 0;
                    formPanel.add(new JLabel("Nom de la pizza :"), gbc);
                    gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
                    JTextField nameField = new JTextField(currentPizza.getNom(), 20);
                    formPanel.add(nameField, gbc);
                    
                    // Champ prix de base (prérempli)
                    gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
                    formPanel.add(new JLabel("Prix de base (€) :"), gbc);
                    gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
                    JTextField priceField = new JTextField(String.format("%.2f", currentPizza.getPrixBase()), 20);
                    formPanel.add(priceField, gbc);
                    
                    // Sélection d'image
                    gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
                    formPanel.add(new JLabel("Image :"), gbc);
                    gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
                    
                    JPanel editImagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                JTextField imagePathField = new JTextField(15);
                imagePathField.setEditable(false);
                    JButton browseButton = new JButton("Changer...");
                    JButton removeImageButton = new JButton("Supprimer image");
                    editImagePanel.add(imagePathField);
                    editImagePanel.add(Box.createHorizontalStrut(5));
                    editImagePanel.add(browseButton);
                    editImagePanel.add(Box.createHorizontalStrut(5));
                    editImagePanel.add(removeImageButton);
                    formPanel.add(editImagePanel, gbc);

                    // Afficher l'image actuelle si elle existe
                    String currentImageName = trouverNomImagePizza(currentPizza.getNom());
                    File currentImageFile = new File("assets/" + currentImageName + ".jpg");
                    if (!currentImageFile.exists()) {
                        currentImageFile = new File("assets/" + currentImageName + ".jpeg");
                    }
                    if (currentImageFile.exists()) {
                        imagePathField.setText("Image actuelle : " + currentImageFile.getName());
                    }
                
                // Action du bouton Parcourir
                browseButton.addActionListener(imgEvent -> {
                    JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Sélectionner une nouvelle image");
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if (f.isDirectory()) return true;
                            String name = f.getName().toLowerCase();
                            return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                                   name.endsWith(".png") || name.endsWith(".gif");
                        }
                        
                        @Override
                        public String getDescription() {
                            return "Images (*.jpg, *.jpeg, *.png, *.gif)";
                        }
                    });
                    
                    int result = fileChooser.showOpenDialog(editPizzaDialog);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        imagePathField.setText(selectedFile.getAbsolutePath());
                    }
                });
                
                    // Action du bouton Supprimer image
                    removeImageButton.addActionListener(imgEvent -> {
                        imagePathField.setText("");
                    });
                    
                    // Sélection des ingrédients
                    gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
                    formPanel.add(new JLabel("Ingrédients :"), gbc);
                    gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
                
                // Récupération de tous les ingrédients
                IngredientDAO ingredientDAO = new IngredientDAO();
                List<Ingredient> allIngredients = ingredientDAO.trouverTous();
                
                // Panneau avec JCheckBox pour sélectionner les ingrédients
                JPanel ingredientsPanel = new JPanel();
                ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
                    ingredientsPanel.setBorder(BorderFactory.createTitledBorder("Sélectionnez les ingrédients"));
                
                // Map pour stocker les checkboxes par ingrédient
                Map<Ingredient, JCheckBox> ingredientCheckboxes = new HashMap<>();
                
                    // Récupérer les ingrédients actuels de la pizza
                List<Ingredient> currentIngredients = currentPizza.getIngredients();
                
                for (Ingredient ingredient : allIngredients) {
                    JCheckBox checkBox = new JCheckBox(ingredient.getNom());
                        checkBox.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                    
                    // Présélectionner si l'ingrédient fait partie de la pizza
                    if (currentIngredients != null) {
                        for (Ingredient pizzaIngredient : currentIngredients) {
                            if (pizzaIngredient.getIdIngredient() == ingredient.getIdIngredient()) {
                                checkBox.setSelected(true);
                                break;
                            }
                        }
                    }
                    
                    ingredientCheckboxes.put(ingredient, checkBox);
                    ingredientsPanel.add(checkBox);
                }
                
                // Bouton pour ajouter un nouvel ingrédient
                    JButton addIngredientButton = new JButton("+ Nouvel ingrédient");
                    addIngredientButton.setPreferredSize(new Dimension(150, 30));
                    ingredientsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                ingredientsPanel.add(addIngredientButton);
                
                // Panneau de défilement pour les ingrédients
                JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsPanel);
                    ingredientsScrollPane.setPreferredSize(new Dimension(300, 200));
                    ingredientsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    formPanel.add(ingredientsScrollPane, gbc);
                
                // Action du bouton pour ajouter un nouvel ingrédient
                addIngredientButton.addActionListener(event -> {
                    JDialog addIngredientDialog = new JDialog(editPizzaDialog, "Ajouter un ingrédient", true);
                        addIngredientDialog.setSize(350, 200);
                    addIngredientDialog.setLocationRelativeTo(editPizzaDialog);
                    
                        JPanel ingPanel = new JPanel(new GridBagLayout());
                        GridBagConstraints ingGbc = new GridBagConstraints();
                        ingGbc.insets = new Insets(10, 10, 10, 10);
                        ingGbc.anchor = GridBagConstraints.WEST;
                        
                        ingGbc.gridx = 0; ingGbc.gridy = 0;
                        ingPanel.add(new JLabel("Nom de l'ingrédient :"), ingGbc);
                        ingGbc.gridx = 1; ingGbc.fill = GridBagConstraints.HORIZONTAL; ingGbc.weightx = 1.0;
                        JTextField ingNameField = new JTextField(15);
                        ingPanel.add(ingNameField, ingGbc);
                        
                        ingGbc.gridx = 0; ingGbc.gridy = 1; ingGbc.fill = GridBagConstraints.NONE; ingGbc.weightx = 0;
                        ingPanel.add(new JLabel("Stock initial :"), ingGbc);
                        ingGbc.gridx = 1; ingGbc.fill = GridBagConstraints.HORIZONTAL; ingGbc.weightx = 1.0;
                    JTextField stockField = new JTextField("100");
                        ingPanel.add(stockField, ingGbc);
                    
                        JPanel ingButtonPanel = new JPanel(new FlowLayout());
                    JButton ingSaveButton = new JButton("Ajouter");
                    JButton ingCancelButton = new JButton("Annuler");
                    ingButtonPanel.add(ingSaveButton);
                    ingButtonPanel.add(ingCancelButton);
                    
                    JPanel ingMainPanel = new JPanel(new BorderLayout());
                    ingMainPanel.add(ingPanel, BorderLayout.CENTER);
                    ingMainPanel.add(ingButtonPanel, BorderLayout.SOUTH);
                    
                    addIngredientDialog.setContentPane(ingMainPanel);
                    
                    // Action du bouton de sauvegarde d'ingrédient
                    ingSaveButton.addActionListener(ingEvent -> {
                        try {
                            String nomIngredient = ingNameField.getText().trim();
                            int stockInitial = Integer.parseInt(stockField.getText().trim());
                            
                            if (nomIngredient.isEmpty()) {
                                JOptionPane.showMessageDialog(addIngredientDialog, 
                                    "Veuillez saisir un nom d'ingrédient", 
                                    "Champ requis", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            
                            // Créer et sauvegarder le nouvel ingrédient
                            Ingredient newIngredient = new Ingredient(nomIngredient, stockInitial);
                            if (ingredientDAO.inserer(newIngredient)) {
                                // Ajouter l'ingrédient à la liste et la checkbox
                                JCheckBox newCheckBox = new JCheckBox(newIngredient.getNom());
                                newCheckBox.setSelected(true); // Sélectionner par défaut
                                    newCheckBox.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                                ingredientCheckboxes.put(newIngredient, newCheckBox);
                                
                                // Ajouter avant le bouton
                                ingredientsPanel.remove(addIngredientButton);
                                    ingredientsPanel.remove(ingredientsPanel.getComponentCount() - 1); // Retirer l'espacement
                                ingredientsPanel.add(newCheckBox);
                                    ingredientsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                                ingredientsPanel.add(addIngredientButton);
                                
                                // Rafraîchir l'affichage
                                ingredientsPanel.revalidate();
                                ingredientsPanel.repaint();
                                
                                addIngredientDialog.dispose();
                                    
                                JOptionPane.showMessageDialog(editPizzaDialog, 
                                    "Ingrédient '" + nomIngredient + "' ajouté avec succès !", 
                                    "Succès", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(addIngredientDialog, 
                                    "Erreur lors de l'ajout de l'ingrédient", 
                                    "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(addIngredientDialog, 
                                "Veuillez saisir un nombre valide pour le stock", 
                                "Erreur de format", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    
                    // Action du bouton d'annulation d'ingrédient
                    ingCancelButton.addActionListener(ingEvent -> addIngredientDialog.dispose());
                    
                    addIngredientDialog.setVisible(true);
                });
                
                    // Panneau pour les boutons principaux
                    JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                    JButton saveButton = new JButton("Enregistrer les modifications");
                    JButton cancelButton = new JButton("Annuler");
                    
                    saveButton.setPreferredSize(new Dimension(200, 35));
                    cancelButton.setPreferredSize(new Dimension(100, 35));
                    
                    editButtonPanel.add(saveButton);
                    editButtonPanel.add(cancelButton);
                    
                    // Ajout des panneaux à la fenêtre
                    mainPanel.add(formPanel, BorderLayout.CENTER);
                    mainPanel.add(editButtonPanel, BorderLayout.SOUTH);
                    
                    editPizzaDialog.setContentPane(mainPanel);
                    
                    // Action du bouton d'enregistrement des modifications
                saveButton.addActionListener(event -> {
                    try {
                        String nomPizza = nameField.getText().trim();
                        String prixText = priceField.getText().trim().replace(',', '.');
                        String imagePath = imagePathField.getText().trim();
                        
                        // Validation des champs
                        if (nomPizza.isEmpty()) {
                            JOptionPane.showMessageDialog(editPizzaDialog, 
                                "Veuillez saisir un nom pour la pizza", 
                                "Champ requis", 
                                JOptionPane.WARNING_MESSAGE);
                                nameField.requestFocus();
                            return;
                        }
                        
                        if (prixText.isEmpty()) {
                            JOptionPane.showMessageDialog(editPizzaDialog, 
                                "Veuillez saisir un prix pour la pizza", 
                                "Champ requis", 
                                JOptionPane.WARNING_MESSAGE);
                                priceField.requestFocus();
                            return;
                        }
                        
                            double prixBase;
                            try {
                                prixBase = Double.parseDouble(prixText);
                                if (prixBase <= 0) {
                                    throw new NumberFormatException("Le prix doit être positif");
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(editPizzaDialog, 
                                    "Veuillez saisir un prix valide (ex: 9.99)", 
                                    "Erreur de format", 
                                    JOptionPane.ERROR_MESSAGE);
                                priceField.requestFocus();
                                return;
                            }
                            
                            // Vérifier qu'au moins un ingrédient est sélectionné
                            boolean hasIngredients = false;
                            for (JCheckBox checkBox : ingredientCheckboxes.values()) {
                                if (checkBox.isSelected()) {
                                    hasIngredients = true;
                                    break;
                                }
                            }
                            
                            if (!hasIngredients) {
                                JOptionPane.showMessageDialog(editPizzaDialog, 
                                    "Veuillez sélectionner au moins un ingrédient", 
                                    "Ingrédients requis", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        
                        // Mettre à jour les propriétés de la pizza
                        currentPizza.setNom(nomPizza);
                        currentPizza.setPrixBase(prixBase);
                        
                        // Mettre à jour les ingrédients
                        currentPizza.viderIngredients();
                        for (Map.Entry<Ingredient, JCheckBox> entry : ingredientCheckboxes.entrySet()) {
                            if (entry.getValue().isSelected()) {
                                currentPizza.ajouterIngredient(entry.getKey());
                            }
                        }
                        
                        // Sauvegarder la pizza modifiée dans la base de données
                            PizzaDAO editPizzaDAO = new PizzaDAO();
                            boolean success = editPizzaDAO.mettreAJour(currentPizza);
                        
                        if (success) {
                                // Gestion de l'image
                                if (!imagePath.isEmpty() && !imagePath.startsWith("Image actuelle")) {
                                    // Nouvelle image sélectionnée
                                try {
                                    File sourceFile = new File(imagePath);
                                    String fileName = sourceFile.getName();
                                        String extension = fileName.substring(fileName.lastIndexOf('.'));
                                    
                                        // Déterminer le nom du fichier de destination
                                        String destinationFileName = trouverNomImagePizza(nomPizza) + extension;
                                    
                                    // Créer le dossier assets s'il n'existe pas
                                    File assetsDir = new File("assets");
                                    if (!assetsDir.exists()) {
                                        assetsDir.mkdir();
                                    }
                                    
                                        // Supprimer l'ancienne image si elle existe
                                        File oldImageJpg = new File("assets/" + trouverNomImagePizza(currentPizza.getNom()) + ".jpg");
                                        File oldImageJpeg = new File("assets/" + trouverNomImagePizza(currentPizza.getNom()) + ".jpeg");
                                        if (oldImageJpg.exists()) oldImageJpg.delete();
                                        if (oldImageJpeg.exists()) oldImageJpeg.delete();
                                        
                                        // Copier la nouvelle image
                                        File destinationFile = new File("assets/" + destinationFileName);
                                    java.nio.file.Files.copy(
                                        sourceFile.toPath(),
                                        destinationFile.toPath(),
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                                    );
                                    
                                        System.out.println("Image mise à jour : " + destinationFile.getAbsolutePath());
                                } catch (Exception ex) {
                                        System.err.println("Erreur lors de la mise à jour de l'image : " + ex.getMessage());
                                    }
                                } else if (imagePath.isEmpty()) {
                                    // Supprimer l'image si le champ est vide
                                    try {
                                        File oldImageJpg = new File("assets/" + trouverNomImagePizza(currentPizza.getNom()) + ".jpg");
                                        File oldImageJpeg = new File("assets/" + trouverNomImagePizza(currentPizza.getNom()) + ".jpeg");
                                        if (oldImageJpg.exists()) {
                                            oldImageJpg.delete();
                                            System.out.println("Ancienne image supprimée");
                                        }
                                        if (oldImageJpeg.exists()) {
                                            oldImageJpeg.delete();
                                            System.out.println("Ancienne image supprimée");
                                        }
                                    } catch (Exception ex) {
                                        System.err.println("Erreur lors de la suppression de l'image : " + ex.getMessage());
                                }
                            }
                            
                            JOptionPane.showMessageDialog(editPizzaDialog, 
                                    "Pizza '" + nomPizza + "' modifiée avec succès !", 
                                "Succès", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Rafraîchir l'affichage des pizzas
                                chargerPizzasDansPanel(pizzasPanel, query);
                            
                            editPizzaDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(editPizzaDialog, 
                                "Erreur lors de la modification de la pizza", 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                        } catch (Exception ex) {
                        JOptionPane.showMessageDialog(editPizzaDialog, 
                                "Erreur inattendue : " + ex.getMessage(), 
                                "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                    }
                });
                
                // Action du bouton d'annulation
                cancelButton.addActionListener(event -> editPizzaDialog.dispose());
                
                editPizzaDialog.setVisible(true);
            });
            
            // Bouton Supprimer
            deleteButton.addActionListener(e -> {
                // Demander confirmation avant de supprimer
                int confirmation = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer la pizza '" + currentPizza.getNom() + "' ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                
                if (confirmation == JOptionPane.YES_OPTION) {
                    PizzaDAO pizzaDAODelete = new PizzaDAO();
                    if (pizzaDAODelete.supprimer(currentPizza.getIdPizza())) {
                        JOptionPane.showMessageDialog(this,
                            "Pizza supprimée avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir l'affichage
                            chargerPizzasDansPanel(pizzasPanel, query);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression de la pizza",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        }
        
        // Rafraîchir l'affichage
        pizzasPanel.revalidate();
        pizzasPanel.repaint();
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
    private javax.swing.ImageIcon chargerImagePizza(String nomImage) {
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
        
        // Panneau de contrôles en haut
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton addButton = new JButton("Nouvelle commande");
        JButton refreshButton = new JButton("Actualiser");
        
        // ComboBox pour filtrer par statut
        JComboBox<String> statusFilter = new JComboBox<>();
        statusFilter.addItem("Tous les statuts");
        statusFilter.addItem("En préparation");
        statusFilter.addItem("En livraison");
        statusFilter.addItem("Livrée");
        statusFilter.addItem("Annulée");
        
        controlPanel.add(new JLabel("🔍 Rechercher client :"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("Statut :"));
        controlPanel.add(statusFilter);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(addButton);
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Tableau des commandes
        String[] columnNames = {
            "ID", "Date", "Heure", "Client", "Statut", "Total (€)", "Gratuite", "Actions"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                    @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Seule la colonne Actions est éditable
            }
        };
        
        JTable commandesTable = new JTable(tableModel);
        commandesTable.setRowHeight(50); // Augmenter encore plus la hauteur des lignes
        
        // Désactiver la sélection des lignes pour éviter la surbrillance bleue
        commandesTable.setRowSelectionAllowed(false);
        commandesTable.setColumnSelectionAllowed(false);
        commandesTable.setCellSelectionEnabled(false);
        
        commandesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        commandesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        commandesTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Heure
        commandesTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Client
        commandesTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Statut
        commandesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Total
        commandesTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Gratuite
        commandesTable.getColumnModel().getColumn(7).setPreferredWidth(280); // Actions - largeur beaucoup plus grande
        
        // Renderer personnalisé pour la colonne Actions
        commandesTable.getColumnModel().getColumn(7).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                
                JButton detailsButton = new JButton("Détails");
                JButton editButton = new JButton("Modifier");
                JButton deleteButton = new JButton("Supprimer");
                
                // Boutons beaucoup plus gros et lisibles
                detailsButton.setPreferredSize(new Dimension(80, 35));
                editButton.setPreferredSize(new Dimension(80, 35));
                deleteButton.setPreferredSize(new Dimension(90, 35));
                
                // Police plus grande et en gras
                Font buttonFont = new Font("Dialog", Font.BOLD, 12);
                detailsButton.setFont(buttonFont);
                editButton.setFont(buttonFont);
                deleteButton.setFont(buttonFont);
                
                // Couleurs pour différencier les boutons
                detailsButton.setBackground(new Color(173, 216, 230)); // Bleu clair
                editButton.setBackground(new Color(255, 255, 224)); // Jaune clair
                deleteButton.setBackground(new Color(255, 182, 193)); // Rose clair
                
                detailsButton.setForeground(Color.BLACK);
                editButton.setForeground(Color.BLACK);
                deleteButton.setForeground(Color.BLACK);
                
                // Rendre les boutons opaques
                detailsButton.setOpaque(true);
                editButton.setOpaque(true);
                deleteButton.setOpaque(true);
                
                buttonPanel.add(detailsButton);
                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);
                
                // Toujours garder le même fond, ignorer la sélection
                buttonPanel.setBackground(table.getBackground());
                
                return buttonPanel;
            }
        });
        
        // Editor personnalisé pour la colonne Actions
        commandesTable.getColumnModel().getColumn(7).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
            private JPanel buttonPanel;
            private int currentRow;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                
                JButton detailsButton = new JButton("Détails");
                JButton editButton = new JButton("Modifier");
                JButton deleteButton = new JButton("Supprimer");
                
                // Boutons beaucoup plus gros et lisibles
                detailsButton.setPreferredSize(new Dimension(80, 35));
                editButton.setPreferredSize(new Dimension(80, 35));
                deleteButton.setPreferredSize(new Dimension(90, 35));
                
                // Police plus grande et en gras
                Font buttonFont = new Font("Dialog", Font.BOLD, 12);
                detailsButton.setFont(buttonFont);
                editButton.setFont(buttonFont);
                deleteButton.setFont(buttonFont);
                
                // Couleurs pour différencier les boutons
                detailsButton.setBackground(new Color(173, 216, 230)); // Bleu clair
                editButton.setBackground(new Color(255, 255, 224)); // Jaune clair
                deleteButton.setBackground(new Color(255, 182, 193)); // Rose clair
                
                detailsButton.setForeground(Color.BLACK);
                editButton.setForeground(Color.BLACK);
                deleteButton.setForeground(Color.BLACK);
                
                // Rendre les boutons opaques pour que les couleurs s'affichent
                detailsButton.setOpaque(true);
                editButton.setOpaque(true);
                deleteButton.setOpaque(true);
                
                // Actions des boutons
                detailsButton.addActionListener(e -> {
                    stopCellEditing();
                    afficherDetailsCommande(currentRow, commandesTable);
                });
                
                editButton.addActionListener(e -> {
                    stopCellEditing();
                    modifierCommande(currentRow, commandesTable);
                });
                
                deleteButton.addActionListener(e -> {
                    stopCellEditing();
                    supprimerCommande(currentRow, commandesTable);
                });
                
                buttonPanel.add(detailsButton);
                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);
                
                return buttonPanel;
            }
            
            @Override
            public Object getCellEditorValue() {
                return "";
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(commandesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Charger les commandes au démarrage
        chargerCommandes(tableModel, null, "Tous les statuts");
        
        // Actions des boutons de contrôle
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            chargerCommandes(tableModel, searchTerm.isEmpty() ? null : searchTerm, selectedStatus);
        });
        
        statusFilter.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            chargerCommandes(tableModel, searchTerm.isEmpty() ? null : searchTerm, selectedStatus);
        });
        
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            chargerCommandes(tableModel, null, "Tous les statuts");
        });
        
        addButton.addActionListener(e -> {
            creerNouvelleCommande(tableModel);
        });
        
        // Recherche en temps réel
        searchField.addActionListener(e -> searchButton.doClick());
        
        return panel;
    }
    
    /**
     * Charge les commandes dans le tableau
     */
    private void chargerCommandes(DefaultTableModel tableModel, String searchTerm, String statusFilter) {
        try {
            tableModel.setRowCount(0); // Vider le tableau
            
            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.trouverTousSansDetails(); // Utiliser la méthode sans détails
            
            for (Commande commande : commandes) {
                // Filtrer par terme de recherche (nom du client)
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String nomComplet = (commande.getClient().getNom() + " " + commande.getClient().getPrenom()).toLowerCase();
                    if (!nomComplet.contains(searchTerm.toLowerCase())) {
                        continue;
                    }
                }
                
                // Filtrer par statut
                if (statusFilter != null && !statusFilter.equals("Tous les statuts")) {
                    String statutCommande = statutEnumVersString(commande.getStatut());
                    if (!statutCommande.equals(statusFilter)) {
                        continue;
                    }
                }
                
                // Calculer le total directement depuis la base de données
                double total = commandeDAO.calculerMontantTotal(commande.getIdCommande());
                
                Object[] row = {
                    commande.getIdCommande(),
                    commande.getDateCommande().toString(),
                    commande.getHeureCommande().toString(),
                    commande.getClient().getNom() + " " + commande.getClient().getPrenom(),
                    formatStatut(commande.getStatut()),
                    String.format("%.2f€", total),
                    commande.isEstGratuite() ? "Oui" : "Non",
                    "Actions"
                };
                
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des commandes : " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des commandes : " + e.getMessage(), 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
    }
    
    /**
     * Convertit une énumération de statut en chaîne pour la ComboBox
     */
    private String statutEnumVersString(Commande.Statut statut) {
        switch (statut) {
            case EN_PREPARATION: return "En préparation";
            case EN_LIVRAISON: return "En livraison";
            case LIVREE: return "Livrée";
            case ANNULEE: return "Annulée";
            default: return "En préparation";
        }
    }

    /**
     * Convertit une chaîne de statut en énumération
     */
    private Commande.Statut convertirStatutString(String statut) {
        switch (statut) {
            case "En préparation": return Commande.Statut.EN_PREPARATION;
            case "En livraison": return Commande.Statut.EN_LIVRAISON;
            case "Livrée": return Commande.Statut.LIVREE;
            case "Annulée": return Commande.Statut.ANNULEE;
            default: return Commande.Statut.EN_PREPARATION;
        }
    }
    
    /**
     * Formate le statut pour l'affichage
     */
    private String formatStatut(Commande.Statut statut) {
        switch (statut) {
            case EN_PREPARATION: return "🔄 En préparation";
            case EN_LIVRAISON: return "🚚 En livraison";
            case LIVREE: return "✅ Livrée";
            case ANNULEE: return "❌ Annulée";
            default: return statut.toString();
        }
    }
    
    /**
     * Affiche les détails d'une commande
     */
    private void afficherDetailsCommande(int row, JTable table) {
        try {
            int idCommande = (int) table.getValueAt(row, 0);
            CommandeDAO commandeDAO = new CommandeDAO();
            DetailCommandeDAO detailCommandeDAO = new DetailCommandeDAO();
            
            // Récupérer la commande sans les détails pour éviter la récursion
            Commande commande = commandeDAO.trouverParIdSansDetails(idCommande);
            
            if (commande == null) {
                JOptionPane.showMessageDialog(this, "Commande introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
            // Charger les détails séparément
            List<DetailCommande> details = detailCommandeDAO.trouverParCommande(idCommande);
            
            // Créer une fenêtre de détails
            JDialog detailsDialog = new JDialog(this, "Détails de la commande #" + idCommande, true);
            detailsDialog.setSize(600, 400);
            detailsDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Informations générales
            JPanel infoPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Commande #:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(commande.getIdCommande())), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Date:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(commande.getDateCommande().toString()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Heure:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(commande.getHeureCommande().toString()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            infoPanel.add(new JLabel("Client:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(commande.getClient().getNom() + " " + commande.getClient().getPrenom()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            infoPanel.add(new JLabel("Statut:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(formatStatut(commande.getStatut())), gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            infoPanel.add(new JLabel("Gratuite:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(commande.isEstGratuite() ? "Oui" : "Non"), gbc);
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            
            // Détails des pizzas commandées
            String[] detailColumns = {"Pizza", "Taille", "Quantité", "Prix unitaire", "Sous-total"};
            DefaultTableModel detailModel = new DefaultTableModel(detailColumns, 0);
            JTable detailTable = new JTable(detailModel);
            
            double total = 0.0;
            for (DetailCommande detail : details) {
                Object[] detailRow = {
                    detail.getPizza().getNom(),
                    detail.getTaille().getLibelle(),
                    detail.getQuantite(),
                    String.format("%.2f€", detail.getPrixUnitaire()),
                    String.format("%.2f€", detail.calculerSousTotal())
                };
                detailModel.addRow(detailRow);
                total += detail.calculerSousTotal();
            }
            
            JScrollPane detailScrollPane = new JScrollPane(detailTable);
            detailScrollPane.setBorder(BorderFactory.createTitledBorder("Pizzas commandées"));
            mainPanel.add(detailScrollPane, BorderLayout.CENTER);
            
            // Total
            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalPanel.add(new JLabel("Total: " + String.format("%.2f€", total)));
            mainPanel.add(totalPanel, BorderLayout.SOUTH);
            
            detailsDialog.setContentPane(mainPanel);
            detailsDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'affichage des détails : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Modifie une commande complètement (pizzas, quantités, statut)
     */
    private void modifierCommande(int row, JTable table) {
        try {
            int idCommande = (int) table.getValueAt(row, 0);
            CommandeDAO commandeDAO = new CommandeDAO();
            DetailCommandeDAO detailCommandeDAO = new DetailCommandeDAO();
            
            // Récupérer la commande sans les détails pour éviter la récursion
            Commande commande = commandeDAO.trouverParIdSansDetails(idCommande);
            
            if (commande == null) {
                JOptionPane.showMessageDialog(this, "Commande introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier si la commande peut être modifiée
            boolean peutModifierContenu = commande.getStatut() == Commande.Statut.EN_PREPARATION;
            
            // Créer la fenêtre de modification
            JDialog editDialog = new JDialog(this, "Modifier la commande #" + idCommande, true);
            editDialog.setSize(900, 700);
            editDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Informations générales en haut
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createTitledBorder("Informations de la commande"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Commande #:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(commande.getIdCommande())), gbc);
            
            gbc.gridx = 2; gbc.gridy = 0;
            infoPanel.add(new JLabel("Date:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(new JLabel(commande.getDateCommande().toString()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Client:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3;
            infoPanel.add(new JLabel(commande.getClient().getNom() + " " + commande.getClient().getPrenom()), gbc);
            gbc.gridwidth = 1;
            
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Statut:"), gbc);
            gbc.gridx = 1;
            
            // ComboBox pour le statut (toujours modifiable)
            String[] statusOptions = {"En préparation", "En livraison", "Livrée", "Annulée"};
            JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
            String currentStatus = statutEnumVersString(commande.getStatut());
            statusCombo.setSelectedItem(currentStatus);
            infoPanel.add(statusCombo, gbc);
            
            gbc.gridx = 2; gbc.gridy = 2;
            infoPanel.add(new JLabel("Gratuite:"), gbc);
            gbc.gridx = 3;
            JCheckBox gratuitCheckBox = new JCheckBox();
            gratuitCheckBox.setSelected(commande.isEstGratuite());
            gratuitCheckBox.setEnabled(peutModifierContenu); // Seulement si en préparation
            infoPanel.add(gratuitCheckBox, gbc);
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            
            // Section des pizzas (modifiable seulement si en préparation)
            JPanel pizzaPanel = new JPanel(new BorderLayout());
            pizzaPanel.setBorder(BorderFactory.createTitledBorder("Pizzas commandées"));
            
            // Tableau des pizzas actuelles
            String[] pizzaColumns = {"Pizza", "Taille", "Quantité", "Prix unitaire", "Sous-total", "Actions"};
            DefaultTableModel pizzaModel = new DefaultTableModel(pizzaColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return peutModifierContenu && (column == 2 || column == 5); // Quantité et Actions
                }
            };
            JTable pizzaTable = new JTable(pizzaModel);
            pizzaTable.setRowHeight(35);
            
            // Charger les détails actuels
            List<DetailCommande> details = detailCommandeDAO.trouverParCommande(idCommande);
            for (DetailCommande detail : details) {
                Object[] pizzaRow = {
                    detail.getPizza().getNom(),
                    detail.getTaille().getLibelle(),
                    detail.getQuantite(),
                    String.format("%.2f€", detail.getPrixUnitaire()),
                    String.format("%.2f€", detail.calculerSousTotal()),
                    peutModifierContenu ? "Supprimer" : "Non modifiable"
                };
                pizzaModel.addRow(pizzaRow);
            }
            
            // Renderer pour la colonne Actions
            pizzaTable.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                        boolean hasFocus, int row, int column) {
                    JButton button = new JButton(value.toString());
                    button.setEnabled(peutModifierContenu);
                    if (peutModifierContenu) {
                        button.setBackground(new Color(255, 182, 193)); // Rose clair
                    } else {
                        button.setBackground(Color.LIGHT_GRAY);
                    }
                    return button;
                }
            });
            
            // Editor pour la colonne Actions
            if (peutModifierContenu) {
                pizzaTable.getColumnModel().getColumn(5).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
                    private JButton button;
                    private int currentRow;
                    
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                        currentRow = row;
                        button = new JButton("Supprimer");
                        button.setBackground(new Color(255, 182, 193));
                        button.addActionListener(e -> {
                            stopCellEditing();
                            pizzaModel.removeRow(currentRow);
                            mettreAJourTotalModification(pizzaModel, editDialog);
                        });
                        return button;
                    }
                    
                    @Override
                    public Object getCellEditorValue() {
                        return "Supprimer";
                    }
                });
            }
            
            // Editor pour la colonne Quantité
            if (peutModifierContenu) {
                pizzaTable.getColumnModel().getColumn(2).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
                    @Override
                    public boolean stopCellEditing() {
                        try {
                            String value = (String) getCellEditorValue();
                            int quantite = Integer.parseInt(value);
                            if (quantite <= 0) {
                                throw new NumberFormatException("La quantité doit être positive");
                            }
                            
                            // Mettre à jour le sous-total
                            int selectedRow = pizzaTable.getSelectedRow();
                            if (selectedRow >= 0) {
                                String prixStr = (String) pizzaModel.getValueAt(selectedRow, 3);
                                double prix = Double.parseDouble(prixStr.replace("€", "").replace(",", "."));
                                double sousTotal = prix * quantite;
                                pizzaModel.setValueAt(String.format("%.2f€", sousTotal), selectedRow, 4);
                                mettreAJourTotalModification(pizzaModel, editDialog);
                            }
                            
                            return super.stopCellEditing();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(editDialog, 
                                "Veuillez entrer une quantité valide (nombre entier positif)", 
                                "Erreur de format", 
                                JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                });
            }
            
            JScrollPane pizzaScrollPane = new JScrollPane(pizzaTable);
            pizzaPanel.add(pizzaScrollPane, BorderLayout.CENTER);
            
            // Panneau d'ajout de pizza (seulement si en préparation)
            if (peutModifierContenu) {
                JPanel addPizzaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                addPizzaPanel.setBorder(BorderFactory.createTitledBorder("Ajouter une pizza"));
                
                PizzaDAO pizzaDAO = new PizzaDAO();
                List<Pizza> pizzas = pizzaDAO.trouverTous();
                JComboBox<Pizza> pizzaCombo = new JComboBox<>();
                for (Pizza pizza : pizzas) {
                    pizzaCombo.addItem(pizza);
                }
                
                TailleDAO tailleDAO = new TailleDAO();
                List<Taille> tailles = tailleDAO.trouverTous();
                JComboBox<Taille> tailleCombo = new JComboBox<>();
                for (Taille taille : tailles) {
                    tailleCombo.addItem(taille);
                }
                
                JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
                JButton addButton = new JButton("Ajouter");
                
                addPizzaPanel.add(new JLabel("Pizza:"));
                addPizzaPanel.add(pizzaCombo);
                addPizzaPanel.add(new JLabel("Taille:"));
                addPizzaPanel.add(tailleCombo);
                addPizzaPanel.add(new JLabel("Quantité:"));
                addPizzaPanel.add(quantiteSpinner);
                addPizzaPanel.add(addButton);
                
                // Action d'ajout de pizza
                addButton.addActionListener(e -> {
                    Pizza selectedPizza = (Pizza) pizzaCombo.getSelectedItem();
                    Taille selectedTaille = (Taille) tailleCombo.getSelectedItem();
                    int quantite = (Integer) quantiteSpinner.getValue();
                    
                    if (selectedPizza != null && selectedTaille != null) {
                        double prixUnitaire = selectedTaille.calculerPrix(selectedPizza.getPrixBase());
                        double sousTotal = prixUnitaire * quantite;
                        
                        Object[] newRow = {
                            selectedPizza.getNom(),
                            selectedTaille.getLibelle(),
                            quantite,
                            String.format("%.2f€", prixUnitaire),
                            String.format("%.2f€", sousTotal),
                            "Supprimer"
                        };
                        pizzaModel.addRow(newRow);
                        mettreAJourTotalModification(pizzaModel, editDialog);
                    }
                });
                
                pizzaPanel.add(addPizzaPanel, BorderLayout.SOUTH);
            } else {
                // Message informatif si la commande ne peut pas être modifiée
                JLabel infoLabel = new JLabel("<html><i>⚠️ Le contenu de cette commande ne peut plus être modifié car elle n'est plus en préparation.<br/>Seul le statut peut être changé.</i></html>");
                infoLabel.setHorizontalAlignment(JLabel.CENTER);
                infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                pizzaPanel.add(infoLabel, BorderLayout.SOUTH);
            }
            
            mainPanel.add(pizzaPanel, BorderLayout.CENTER);
            
            // Total et boutons
            JPanel bottomPanel = new JPanel(new BorderLayout());
            
            // Total
            JLabel totalLabel = new JLabel("Total: 0.00€");
            totalLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            totalLabel.setHorizontalAlignment(JLabel.RIGHT);
            bottomPanel.add(totalLabel, BorderLayout.NORTH);
            
            // Mettre à jour le total initial
            mettreAJourTotalModification(pizzaModel, editDialog);
            
            // Boutons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton saveButton = new JButton("Enregistrer les modifications");
            JButton cancelButton = new JButton("Annuler");
            
            saveButton.setPreferredSize(new Dimension(200, 35));
            cancelButton.setPreferredSize(new Dimension(100, 35));
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            // Action de sauvegarde
            saveButton.addActionListener(e -> {
                try {
                    // Arrêter l'édition en cours pour sauvegarder les changements
                    if (pizzaTable.isEditing()) {
                        pizzaTable.getCellEditor().stopCellEditing();
                    }
                    
                    // Mettre à jour le statut
                    String newStatus = (String) statusCombo.getSelectedItem();
                    Commande.Statut nouveauStatut = convertirStatutString(newStatus);
                    commande.setStatut(nouveauStatut);
                    commande.setEstGratuite(gratuitCheckBox.isSelected());
                    
                    // Si le contenu peut être modifié, mettre à jour les détails
                    if (peutModifierContenu) {
                        // Vérifier qu'il y a au moins une pizza
                        if (pizzaModel.getRowCount() == 0) {
                            JOptionPane.showMessageDialog(editDialog, 
                                "Une commande doit contenir au moins une pizza", 
                                "Commande vide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Supprimer tous les anciens détails
                        detailCommandeDAO.supprimerParCommande(idCommande);
                        
                        // Ajouter les nouveaux détails depuis le tableau
                        PizzaDAO pizzaDAO = new PizzaDAO();
                        TailleDAO tailleDAO = new TailleDAO();
                        List<Pizza> pizzas = pizzaDAO.trouverTous();
                        List<Taille> tailles = tailleDAO.trouverTous();
                        
                        double nouveauTotal = 0.0;
                        
                        for (int i = 0; i < pizzaModel.getRowCount(); i++) {
                            String nomPizza = (String) pizzaModel.getValueAt(i, 0);
                            String libelleTaille = (String) pizzaModel.getValueAt(i, 1);
                            
                            // Récupérer la quantité (peut être modifiée dans le tableau)
                            Object quantiteObj = pizzaModel.getValueAt(i, 2);
                            int quantite;
                            if (quantiteObj instanceof Integer) {
                                quantite = (Integer) quantiteObj;
                            } else {
                                try {
                                    quantite = Integer.parseInt(quantiteObj.toString());
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(editDialog, 
                                        "Quantité invalide à la ligne " + (i + 1) + ": " + quantiteObj, 
                                        "Erreur de format", 
                                        JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                            
                            if (quantite <= 0) {
                                JOptionPane.showMessageDialog(editDialog, 
                                    "La quantité doit être positive à la ligne " + (i + 1), 
                                    "Quantité invalide", 
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            String prixStr = (String) pizzaModel.getValueAt(i, 3);
                            double prixUnitaire = Double.parseDouble(prixStr.replace("€", "").replace(",", "."));
                            
                            // Retrouver la pizza et la taille
                            Pizza pizza = pizzas.stream()
                                .filter(p -> p.getNom().equals(nomPizza))
                                .findFirst().orElse(null);
                            Taille taille = tailles.stream()
                                .filter(t -> t.getLibelle().equals(libelleTaille))
                                .findFirst().orElse(null);
                            
                            if (pizza != null && taille != null) {
                                // Recalculer le prix unitaire au cas où la taille aurait changé
                                double prixCalcule = taille.calculerPrix(pizza.getPrixBase());
                                
                                DetailCommande detail = new DetailCommande();
                                detail.setCommande(commande);
                                detail.setPizza(pizza);
                                detail.setTaille(taille);
                                detail.setQuantite(quantite);
                                detail.setPrixUnitaire(prixCalcule);
                                
                                if (!detailCommandeDAO.inserer(detail)) {
                                    JOptionPane.showMessageDialog(editDialog, 
                                        "Erreur lors de l'ajout de la pizza: " + nomPizza, 
                                        "Erreur", 
                                        JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                nouveauTotal += prixCalcule * quantite;
                            } else {
                                JOptionPane.showMessageDialog(editDialog, 
                                    "Pizza ou taille introuvable: " + nomPizza + " - " + libelleTaille, 
                                    "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        
                        // Vérifier le solde si ce n'est pas gratuit
                        if (!commande.isEstGratuite() && nouveauTotal > commande.getClient().getSoldeCompte()) {
                            JOptionPane.showMessageDialog(editDialog, 
                                String.format("Le solde du client (%.2f€) est insuffisant pour cette commande (%.2f€)", 
                                    commande.getClient().getSoldeCompte(), nouveauTotal), 
                                "Solde insuffisant", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    
                    // Sauvegarder la commande
                    if (commandeDAO.mettreAJour(commande)) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Commande modifiée avec succès!", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir le tableau principal
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        chargerCommandes(model, null, "Tous les statuts");
                        
                        editDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Erreur lors de la modification de la commande", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Erreur inattendue : " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            // Action d'annulation
            cancelButton.addActionListener(e -> editDialog.dispose());
            
            editDialog.setContentPane(mainPanel);
            editDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la modification : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Met à jour le total dans la fenêtre de modification
     */
    private void mettreAJourTotalModification(DefaultTableModel model, JDialog dialog) {
        double total = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String sousTotal = (String) model.getValueAt(i, 4);
            sousTotal = sousTotal.replace("€", "").replace(",", ".");
            total += Double.parseDouble(sousTotal);
        }
        
        // Trouver le label du total et le mettre à jour
        Component[] components = ((JPanel) dialog.getContentPane()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().startsWith("Total:")) {
                            label.setText("Total: " + String.format("%.2f€", total));
                            return;
                        }
                    } else if (subComp instanceof JPanel) {
                        JPanel subPanel = (JPanel) subComp;
                        for (Component subSubComp : subPanel.getComponents()) {
                            if (subSubComp instanceof JLabel) {
                                JLabel label = (JLabel) subSubComp;
                                if (label.getText().startsWith("Total:")) {
                                    label.setText("Total: " + String.format("%.2f€", total));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Supprime une commande
     */
    private void supprimerCommande(int row, JTable table) {
        try {
            int idCommande = (int) table.getValueAt(row, 0);
            
            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer la commande #" + idCommande + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmation == JOptionPane.YES_OPTION) {
                CommandeDAO commandeDAO = new CommandeDAO();
                if (commandeDAO.supprimer(idCommande)) {
                    JOptionPane.showMessageDialog(this, 
                        "Commande supprimée avec succès!", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                    // Rafraîchir le tableau
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    chargerCommandes(model, null, "Tous les statuts");
                    } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression de la commande", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la suppression : " + e.getMessage(), 
                "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Crée une nouvelle commande
     */
    private void creerNouvelleCommande(DefaultTableModel tableModel) {
        // Créer une fenêtre de création de commande
        JDialog createDialog = new JDialog(this, "Nouvelle commande", true);
        createDialog.setSize(800, 600);
        createDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Sélection du client
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.setBorder(BorderFactory.createTitledBorder("Client"));
        
        ClientDAO clientDAO = new ClientDAO();
        List<Client> clients = clientDAO.trouverTous();
        JComboBox<Client> clientCombo = new JComboBox<>();
        for (Client client : clients) {
            clientCombo.addItem(client);
        }
        clientCombo.setRenderer(new javax.swing.ListCellRenderer<Client>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Client> list, Client value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel();
                if (value != null) {
                    label.setText(value.getNom() + " " + value.getPrenom() + " (Solde: " + 
                                String.format("%.2f€", value.getSoldeCompte()) + ")");
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true);
                }
                return label;
            }
        });
        
        JCheckBox gratuitCheckBox = new JCheckBox("Commande gratuite");
        
        clientPanel.add(new JLabel("Client:"));
        clientPanel.add(clientCombo);
        clientPanel.add(Box.createHorizontalStrut(20));
        clientPanel.add(gratuitCheckBox);
        
        mainPanel.add(clientPanel, BorderLayout.NORTH);
        
        // Sélection des pizzas
        JPanel pizzaPanel = new JPanel(new BorderLayout());
        pizzaPanel.setBorder(BorderFactory.createTitledBorder("Pizzas à commander"));
        
        // Tableau pour les pizzas sélectionnées
        String[] pizzaColumns = {"Pizza", "Taille", "Quantité", "Prix unitaire", "Sous-total", "Actions"};
        DefaultTableModel pizzaModel = new DefaultTableModel(pizzaColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 5; // Quantité et Actions
            }
        };
        JTable pizzaTable = new JTable(pizzaModel);
        
        // Panneau d'ajout de pizza
        JPanel addPizzaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        PizzaDAO pizzaDAO = new PizzaDAO();
        List<Pizza> pizzas = pizzaDAO.trouverTous();
        JComboBox<Pizza> pizzaCombo = new JComboBox<>();
        for (Pizza pizza : pizzas) {
            pizzaCombo.addItem(pizza);
        }
        
        TailleDAO tailleDAO = new TailleDAO();
        List<Taille> tailles = tailleDAO.trouverTous();
        JComboBox<Taille> tailleCombo = new JComboBox<>();
        for (Taille taille : tailles) {
            tailleCombo.addItem(taille);
        }
        
        JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JButton addPizzaButton = new JButton("Ajouter");
        
        addPizzaPanel.add(new JLabel("Pizza:"));
        addPizzaPanel.add(pizzaCombo);
        addPizzaPanel.add(new JLabel("Taille:"));
        addPizzaPanel.add(tailleCombo);
        addPizzaPanel.add(new JLabel("Quantité:"));
        addPizzaPanel.add(quantiteSpinner);
        addPizzaPanel.add(addPizzaButton);
        
        pizzaPanel.add(addPizzaPanel, BorderLayout.NORTH);
        pizzaPanel.add(new JScrollPane(pizzaTable), BorderLayout.CENTER);
        
        // Label du total
        JLabel totalLabel = new JLabel("Total: 0.00€");
        totalLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        pizzaPanel.add(totalPanel, BorderLayout.SOUTH);
        
        mainPanel.add(pizzaPanel, BorderLayout.CENTER);
        
        // Boutons de validation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Créer la commande");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Action d'ajout de pizza
        addPizzaButton.addActionListener(e -> {
            Pizza selectedPizza = (Pizza) pizzaCombo.getSelectedItem();
            Taille selectedTaille = (Taille) tailleCombo.getSelectedItem();
            int quantite = (Integer) quantiteSpinner.getValue();
            
            if (selectedPizza != null && selectedTaille != null) {
                double prixUnitaire = selectedTaille.calculerPrix(selectedPizza.getPrixBase());
                double sousTotal = prixUnitaire * quantite;
                
                Object[] row = {
                    selectedPizza.getNom(),
                    selectedTaille.getLibelle(),
                    quantite,
                    String.format("%.2f€", prixUnitaire),
                    String.format("%.2f€", sousTotal),
                    "Supprimer"
                };
                pizzaModel.addRow(row);
                
                // Mettre à jour le total
                mettreAJourTotal(pizzaModel, totalLabel);
            }
        });
        
        // Action de sauvegarde
        saveButton.addActionListener(e -> {
            try {
                Client selectedClient = (Client) clientCombo.getSelectedItem();
                boolean estGratuite = gratuitCheckBox.isSelected();
                
                if (selectedClient == null) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Veuillez sélectionner un client", 
                        "Champ requis", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (pizzaModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Veuillez ajouter au moins une pizza", 
                        "Commande vide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Créer la commande
                Commande nouvelleCommande = new Commande(selectedClient);
                nouvelleCommande.setEstGratuite(estGratuite);
                
                // Ajouter les détails
                for (int i = 0; i < pizzaModel.getRowCount(); i++) {
                    String nomPizza = (String) pizzaModel.getValueAt(i, 0);
                    String libelleTaille = (String) pizzaModel.getValueAt(i, 1);
                    int quantite = (Integer) pizzaModel.getValueAt(i, 2);
                    
                    // Retrouver la pizza et la taille
                    Pizza pizza = pizzas.stream()
                        .filter(p -> p.getNom().equals(nomPizza))
                        .findFirst().orElse(null);
                    Taille taille = tailles.stream()
                        .filter(t -> t.getLibelle().equals(libelleTaille))
                        .findFirst().orElse(null);
                    
                    if (pizza != null && taille != null) {
                        nouvelleCommande.ajouterPizza(pizza, taille, quantite);
                    }
                }
                
                // Vérifier le solde si ce n'est pas gratuit
                if (!estGratuite && !nouvelleCommande.peutEtrePrepare()) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Le solde du client est insuffisant pour cette commande", 
                        "Solde insuffisant", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Sauvegarder la commande
                CommandeDAO commandeDAO = new CommandeDAO();
                if (commandeDAO.inserer(nouvelleCommande)) {
                    // Valider la commande (débiter le compte si nécessaire)
                    if (nouvelleCommande.valider()) {
                        // Mettre à jour le client dans la base
                        clientDAO.mettreAJour(selectedClient);
                    }
                    
                    JOptionPane.showMessageDialog(createDialog, 
                        "Commande créée avec succès!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir le tableau principal
                    chargerCommandes(tableModel, null, "Tous les statuts");
                    
                    createDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Erreur lors de la création de la commande", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(createDialog, 
                    "Erreur inattendue : " + ex.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // Action d'annulation
        cancelButton.addActionListener(e -> createDialog.dispose());
        
        createDialog.setContentPane(mainPanel);
        createDialog.setVisible(true);
    }
    
    /**
     * Met à jour le total affiché
     */
    private void mettreAJourTotal(DefaultTableModel model, JLabel totalLabel) {
        double total = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String sousTotal = (String) model.getValueAt(i, 4);
            // Enlever le symbole € et remplacer les virgules par des points
            sousTotal = sousTotal.replace("€", "").replace(",", ".");
            total += Double.parseDouble(sousTotal);
        }
        totalLabel.setText("Total: " + String.format("%.2f€", total));
    }
    
    /**
     * Crée le panneau de gestion des livraisons
     * @return Le panneau de livraisons
     */
    private JPanel createLivraisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panneau de contrôles en haut
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton addButton = new JButton("Nouvelle livraison");
        JButton refreshButton = new JButton("Actualiser");
        
        // ComboBox pour filtrer par statut de livraison
        JComboBox<String> statusFilter = new JComboBox<>();
        statusFilter.addItem("Toutes les livraisons");
        statusFilter.addItem("En cours");
        statusFilter.addItem("Terminées");
        statusFilter.addItem("En retard");
        
        controlPanel.add(new JLabel("🔍 Rechercher livreur :"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("Statut :"));
        controlPanel.add(statusFilter);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(addButton);
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Tableau des livraisons
        String[] columnNames = {
            "ID", "Commande", "Livreur", "Véhicule", "Heure départ", "Heure arrivée", "Durée", "Statut", "Actions"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Seule la colonne Actions est éditable
            }
        };
        
        JTable livraisonsTable = new JTable(tableModel);
        livraisonsTable.setRowHeight(45);
        
        // Désactiver la sélection des lignes
        livraisonsTable.setRowSelectionAllowed(false);
        livraisonsTable.setColumnSelectionAllowed(false);
        livraisonsTable.setCellSelectionEnabled(false);
        
        // Ajuster les largeurs des colonnes
        livraisonsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        livraisonsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Commande
        livraisonsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Livreur
        livraisonsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Véhicule
        livraisonsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Heure départ
        livraisonsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Heure arrivée
        livraisonsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Durée
        livraisonsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Statut
        livraisonsTable.getColumnModel().getColumn(8).setPreferredWidth(320); // Actions - largeur augmentée
        
        // Renderer personnalisé pour la colonne Actions
        livraisonsTable.getColumnModel().getColumn(8).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2)); // Espacement réduit
                
                // Vérifier si la livraison est terminée
                String heureArrivee = (String) table.getValueAt(row, 5);
                boolean estTerminee = !heureArrivee.equals("-");
                
                JButton detailsButton = new JButton("Détails");
                JButton editButton = new JButton("Modifier");
                JButton finishButton = new JButton("Terminer");
                JButton deleteButton = new JButton("Supprimer");
                
                // Taille des boutons réduite
                detailsButton.setPreferredSize(new Dimension(65, 28));
                editButton.setPreferredSize(new Dimension(65, 28));
                finishButton.setPreferredSize(new Dimension(65, 28));
                deleteButton.setPreferredSize(new Dimension(75, 28));
                
                // Police
                Font buttonFont = new Font("Dialog", Font.BOLD, 9); // Police légèrement plus petite
                detailsButton.setFont(buttonFont);
                editButton.setFont(buttonFont);
                finishButton.setFont(buttonFont);
                deleteButton.setFont(buttonFont);
                
                // Couleurs
                detailsButton.setBackground(new Color(173, 216, 230)); // Bleu clair
                editButton.setBackground(new Color(255, 255, 224)); // Jaune clair
                finishButton.setBackground(new Color(144, 238, 144)); // Vert clair
                deleteButton.setBackground(new Color(255, 182, 193)); // Rose clair
                
                detailsButton.setOpaque(true);
                editButton.setOpaque(true);
                finishButton.setOpaque(true);
                deleteButton.setOpaque(true);
                
                buttonPanel.add(detailsButton);
                buttonPanel.add(editButton);
                
                // Le bouton "Terminer" n'est visible que si la livraison n'est pas terminée
                if (!estTerminee) {
                    buttonPanel.add(finishButton);
                } else {
                    finishButton.setEnabled(false);
                    finishButton.setBackground(Color.LIGHT_GRAY);
                }
                
                buttonPanel.add(deleteButton);
                
                buttonPanel.setBackground(table.getBackground());
                return buttonPanel;
            }
        });
        
        // Editor personnalisé pour la colonne Actions
        livraisonsTable.getColumnModel().getColumn(8).setCellEditor(new javax.swing.DefaultCellEditor(new JTextField()) {
            private JPanel buttonPanel;
            private int currentRow;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
                
                // Vérifier si la livraison est terminée
                String heureArrivee = (String) table.getValueAt(row, 5);
                boolean estTerminee = !heureArrivee.equals("-");
                
                JButton detailsButton = new JButton("Détails");
                JButton editButton = new JButton("Modifier");
                JButton finishButton = new JButton("Terminer");
                JButton deleteButton = new JButton("Supprimer");
                
                // Taille des boutons réduite
                detailsButton.setPreferredSize(new Dimension(65, 28));
                editButton.setPreferredSize(new Dimension(65, 28));
                finishButton.setPreferredSize(new Dimension(65, 28));
                deleteButton.setPreferredSize(new Dimension(75, 28));
                
                // Police
                Font buttonFont = new Font("Dialog", Font.BOLD, 9); // Police légèrement plus petite
                detailsButton.setFont(buttonFont);
                editButton.setFont(buttonFont);
                finishButton.setFont(buttonFont);
                deleteButton.setFont(buttonFont);
                
                // Couleurs
                detailsButton.setBackground(new Color(173, 216, 230));
                editButton.setBackground(new Color(255, 255, 224));
                finishButton.setBackground(new Color(144, 238, 144));
                deleteButton.setBackground(new Color(255, 182, 193));
                
                detailsButton.setOpaque(true);
                editButton.setOpaque(true);
                finishButton.setOpaque(true);
                deleteButton.setOpaque(true);
                
                // Actions des boutons
                detailsButton.addActionListener(e -> {
                    stopCellEditing();
                    afficherDetailsLivraison(currentRow, livraisonsTable);
                });
                
                editButton.addActionListener(e -> {
                    stopCellEditing();
                    modifierLivraison(currentRow, livraisonsTable);
                });
                
                finishButton.addActionListener(e -> {
                    stopCellEditing();
                    terminerLivraison(currentRow, livraisonsTable);
                });
                
                deleteButton.addActionListener(e -> {
                    stopCellEditing();
                    supprimerLivraison(currentRow, livraisonsTable);
                });
                
                buttonPanel.add(detailsButton);
                buttonPanel.add(editButton);
                
                // Le bouton "Terminer" n'est visible que si la livraison n'est pas terminée
                if (!estTerminee) {
                    buttonPanel.add(finishButton);
                }
                
                buttonPanel.add(deleteButton);
                
                return buttonPanel;
            }
            
            @Override
            public Object getCellEditorValue() {
                return "";
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(livraisonsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Charger les livraisons au démarrage
        chargerLivraisons(tableModel, null, "Toutes les livraisons");
        
        // Actions des boutons de contrôle
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            chargerLivraisons(tableModel, searchTerm.isEmpty() ? null : searchTerm, selectedStatus);
        });
        
        statusFilter.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            chargerLivraisons(tableModel, searchTerm.isEmpty() ? null : searchTerm, selectedStatus);
        });
        
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            chargerLivraisons(tableModel, null, "Toutes les livraisons");
        });
        
        addButton.addActionListener(e -> {
            creerNouvelleLivraison(tableModel);
        });
        
        // Recherche en temps réel
        searchField.addActionListener(e -> searchButton.doClick());
        
        return panel;
    }
    
    /**
     * Charge les livraisons dans le tableau
     */
    private void chargerLivraisons(DefaultTableModel tableModel, String searchTerm, String statusFilter) {
        try {
            tableModel.setRowCount(0); // Vider le tableau
            
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            List<Livraison> livraisons = livraisonDAO.trouverTousSansDetails();
            
            for (Livraison livraison : livraisons) {
                // Filtrer par terme de recherche (nom du livreur)
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String nomComplet = (livraison.getLivreur().getNom() + " " + livraison.getLivreur().getPrenom()).toLowerCase();
                    if (!nomComplet.contains(searchTerm.toLowerCase())) {
                        continue;
                    }
                }
                
                // Filtrer par statut
                if (statusFilter != null && !statusFilter.equals("Toutes les livraisons")) {
                    String statutLivraison = determinerStatutLivraison(livraison);
                    if (!statutLivraison.equals(statusFilter)) {
                        continue;
                    }
                }
                
                // Calculer la durée
                String duree = calculerDureeLivraison(livraison);
                
                // Déterminer l'heure d'arrivée
                String heureArrivee = livraison.getHeureArrivee() != null ? 
                    livraison.getHeureArrivee().toString() : "-";
                
                Object[] row = {
                    livraison.getIdLivraison(),
                    livraison.getCommande().getIdCommande(),
                    livraison.getLivreur().getNom() + " " + livraison.getLivreur().getPrenom(),
                    livraison.getVehicule().toString(),
                    livraison.getHeureDepart().toString(),
                    heureArrivee,
                    duree,
                    formatStatutLivraison(livraison),
                    "Actions"
                };
                
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des livraisons : " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des livraisons : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Détermine le statut d'une livraison basé sur ses propriétés
     */
    private String determinerStatutLivraison(Livraison livraison) {
        if (livraison.isEstEnRetard()) {
            return "En retard";
        } else if (livraison.getHeureArrivee() != null) {
            return "Terminées";
        } else {
            return "En cours";
        }
    }
    
    /**
     * Calcule la durée d'une livraison
     */
    private String calculerDureeLivraison(Livraison livraison) {
        long dureeMinutes = livraison.calculerDureeMinutes();
        if (dureeMinutes >= 0) {
            return dureeMinutes + " min";
        } else {
            return "-";
        }
    }
    
    /**
     * Formate le statut d'une livraison pour l'affichage
     */
    private String formatStatutLivraison(Livraison livraison) {
        if (livraison.isEstEnRetard()) {
            return "⚠️ En retard";
        } else if (livraison.getHeureArrivee() != null) {
            return "✅ Terminée";
        } else {
            return "🔄 En cours";
        }
    }
    
    /**
     * Affiche les détails d'une livraison
     */
    private void afficherDetailsLivraison(int row, JTable table) {
        try {
            int idLivraison = (int) table.getValueAt(row, 0);
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            
            Livraison livraison = livraisonDAO.trouverParId(idLivraison);
            
            if (livraison == null) {
                JOptionPane.showMessageDialog(this, "Livraison introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer une fenêtre de détails
            JDialog detailsDialog = new JDialog(this, "Détails de la livraison #" + idLivraison, true);
            detailsDialog.setSize(600, 400);
            detailsDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Informations générales
            JPanel infoPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Livraison #:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(livraison.getIdLivraison())), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Commande #:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(livraison.getCommande().getIdCommande())), gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Client:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(livraison.getCommande().getClient().getNom() + " " + 
                livraison.getCommande().getClient().getPrenom()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            infoPanel.add(new JLabel("Livreur:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(livraison.getLivreur().getNomComplet()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            infoPanel.add(new JLabel("Véhicule:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(livraison.getVehicule().toString()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            infoPanel.add(new JLabel("Heure départ:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(livraison.getHeureDepart().toString()), gbc);
            
            gbc.gridx = 0; gbc.gridy = 6;
            infoPanel.add(new JLabel("Heure arrivée:"), gbc);
            gbc.gridx = 1;
            String heureArrivee = livraison.getHeureArrivee() != null ? 
                livraison.getHeureArrivee().toString() : "Non terminée";
            infoPanel.add(new JLabel(heureArrivee), gbc);
            
            gbc.gridx = 0; gbc.gridy = 7;
            infoPanel.add(new JLabel("Durée:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(calculerDureeLivraison(livraison)), gbc);
            
            gbc.gridx = 0; gbc.gridy = 8;
            infoPanel.add(new JLabel("Statut:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(formatStatutLivraison(livraison)), gbc);
            
            mainPanel.add(infoPanel, BorderLayout.CENTER);
            
            // Bouton de fermeture
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton closeButton = new JButton("Fermer");
            closeButton.addActionListener(e -> detailsDialog.dispose());
            buttonPanel.add(closeButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            detailsDialog.setContentPane(mainPanel);
            detailsDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'affichage des détails : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Modifie une livraison
     */
    private void modifierLivraison(int row, JTable table) {
        try {
            int idLivraison = (int) table.getValueAt(row, 0);
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            
            Livraison livraison = livraisonDAO.trouverParId(idLivraison);
            
            if (livraison == null) {
                JOptionPane.showMessageDialog(this, "Livraison introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Créer la fenêtre de modification
            JDialog editDialog = new JDialog(this, "Modifier la livraison #" + idLivraison, true);
            editDialog.setSize(500, 400);
            editDialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Formulaire de modification
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Sélection du livreur
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Livreur:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            
            LivreurDAO livreurDAO = new LivreurDAO();
            List<Livreur> livreurs = livreurDAO.trouverTous();
            JComboBox<Livreur> livreurCombo = new JComboBox<>();
            for (Livreur livreur : livreurs) {
                livreurCombo.addItem(livreur);
            }
            livreurCombo.setSelectedItem(livraison.getLivreur());
            formPanel.add(livreurCombo, gbc);
            
            // Sélection du véhicule
            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Véhicule:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            List<Vehicule> vehiculesDisponibles = vehiculeDAO.trouverVehiculesDisponibles();
            // Ajouter le véhicule actuel s'il n'est pas dans la liste des disponibles
            if (!vehiculesDisponibles.contains(livraison.getVehicule())) {
                vehiculesDisponibles.add(livraison.getVehicule());
            }
            JComboBox<Vehicule> vehiculeCombo = new JComboBox<>();
            for (Vehicule vehicule : vehiculesDisponibles) {
                vehiculeCombo.addItem(vehicule);
            }
            vehiculeCombo.setSelectedItem(livraison.getVehicule());
            formPanel.add(vehiculeCombo, gbc);
            
            // Heure de départ
            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("Heure départ:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            JTextField heureDepart = new JTextField(livraison.getHeureDepart().toString());
            formPanel.add(heureDepart, gbc);
            
            // Checkbox pour marquer en retard
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JCheckBox retardCheckBox = new JCheckBox("Marquer comme en retard");
            retardCheckBox.setSelected(livraison.isEstEnRetard());
            formPanel.add(retardCheckBox, gbc);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // Boutons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Action de sauvegarde
            saveButton.addActionListener(e -> {
                try {
                    Livreur selectedLivreur = (Livreur) livreurCombo.getSelectedItem();
                    Vehicule selectedVehicule = (Vehicule) vehiculeCombo.getSelectedItem();
                    
                    livraison.setLivreur(selectedLivreur);
                    livraison.setVehicule(selectedVehicule);
                    livraison.setEstEnRetard(retardCheckBox.isSelected());
                    
                    // Essayer de parser l'heure
                    try {
                        livraison.setHeureDepart(java.time.LocalTime.parse(heureDepart.getText()));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Format d'heure invalide. Utilisez le format HH:MM", 
                            "Erreur de format", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (livraisonDAO.mettreAJour(livraison)) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Livraison modifiée avec succès!", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir le tableau principal
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        chargerLivraisons(model, null, "Toutes les livraisons");
                        
                        editDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Erreur lors de la modification de la livraison", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Erreur inattendue : " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            // Action d'annulation
            cancelButton.addActionListener(e -> editDialog.dispose());
            
            editDialog.setContentPane(mainPanel);
            editDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la modification : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Termine une livraison
     */
    private void terminerLivraison(int row, JTable table) {
        try {
            int idLivraison = (int) table.getValueAt(row, 0);
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            
            Livraison livraison = livraisonDAO.trouverParId(idLivraison);
            
            if (livraison == null) {
                JOptionPane.showMessageDialog(this, "Livraison introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (livraison.getHeureArrivee() != null) {
                JOptionPane.showMessageDialog(this, "Cette livraison est déjà terminée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir terminer la livraison #" + idLivraison + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirmation == JOptionPane.YES_OPTION) {
                // Terminer la livraison
                livraison.terminerLivraison();
                
                if (livraisonDAO.mettreAJour(livraison)) {
                    JOptionPane.showMessageDialog(this, 
                        "Livraison terminée avec succès!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir le tableau
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    chargerLivraisons(model, null, "Toutes les livraisons");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la finalisation de la livraison", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la finalisation : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Supprime une livraison
     */
    private void supprimerLivraison(int row, JTable table) {
        try {
            int idLivraison = (int) table.getValueAt(row, 0);
            
            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer la livraison #" + idLivraison + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmation == JOptionPane.YES_OPTION) {
                LivraisonDAO livraisonDAO = new LivraisonDAO();
                if (livraisonDAO.supprimer(idLivraison)) {
                    JOptionPane.showMessageDialog(this, 
                        "Livraison supprimée avec succès!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir le tableau
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    chargerLivraisons(model, null, "Toutes les livraisons");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression de la livraison", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la suppression : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Crée une nouvelle livraison
     */
    private void creerNouvelleLivraison(DefaultTableModel tableModel) {
        // Créer une fenêtre de création de livraison
        JDialog createDialog = new JDialog(this, "Nouvelle livraison", true);
        createDialog.setSize(600, 500);
        createDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Sélection de la commande (seulement celles en livraison)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Commande:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        CommandeDAO commandeDAO = new CommandeDAO();
        List<Commande> toutesCommandes = commandeDAO.trouverTousSansDetails(); // Utiliser la méthode sans détails
        List<Commande> commandesEnLivraison = new ArrayList<>();
        for (Commande commande : toutesCommandes) {
            if (commande.getStatut() == Commande.Statut.EN_LIVRAISON) {
                commandesEnLivraison.add(commande);
            }
        }
        
        // Vérifier s'il y a des commandes en livraison
        if (commandesEnLivraison.isEmpty()) {
            JOptionPane.showMessageDialog(createDialog, 
                "Aucune commande avec le statut 'En livraison' n'est disponible.\n" +
                "Veuillez d'abord créer une commande et la mettre en statut 'En livraison'.", 
                "Aucune commande disponible", 
                JOptionPane.INFORMATION_MESSAGE);
            createDialog.dispose();
            return;
        }
        
        JComboBox<Commande> commandeCombo = new JComboBox<>();
        for (Commande commande : commandesEnLivraison) {
            commandeCombo.addItem(commande);
        }
        // Renderer pour afficher les informations de la commande
        commandeCombo.setRenderer(new javax.swing.ListCellRenderer<Commande>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Commande> list, Commande value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel();
                if (value != null) {
                    label.setText("Commande #" + value.getIdCommande() + " - " + 
                                value.getClient().getNom() + " " + value.getClient().getPrenom());
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true);
                }
                return label;
            }
        });
        formPanel.add(commandeCombo, gbc);
        
        // Sélection du livreur
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Livreur:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        LivreurDAO livreurDAO = new LivreurDAO();
        List<Livreur> livreurs = livreurDAO.trouverTous();
        JComboBox<Livreur> livreurCombo = new JComboBox<>();
        for (Livreur livreur : livreurs) {
            livreurCombo.addItem(livreur);
        }
        formPanel.add(livreurCombo, gbc);
        
        // Sélection du véhicule (seulement les disponibles)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Véhicule:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        List<Vehicule> vehiculesDisponibles = vehiculeDAO.trouverVehiculesDisponibles();
        JComboBox<Vehicule> vehiculeCombo = new JComboBox<>();
        for (Vehicule vehicule : vehiculesDisponibles) {
            vehiculeCombo.addItem(vehicule);
        }
        formPanel.add(vehiculeCombo, gbc);
        
        // Heure de départ (par défaut maintenant)
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Heure départ:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField heureDepart = new JTextField(java.time.LocalTime.now().toString());
        formPanel.add(heureDepart, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Boutons de validation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Créer la livraison");
        JButton cancelButton = new JButton("Annuler");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Action de sauvegarde
        saveButton.addActionListener(e -> {
            try {
                Commande selectedCommande = (Commande) commandeCombo.getSelectedItem();
                Livreur selectedLivreur = (Livreur) livreurCombo.getSelectedItem();
                Vehicule selectedVehicule = (Vehicule) vehiculeCombo.getSelectedItem();
                
                if (selectedCommande == null || selectedLivreur == null || selectedVehicule == null) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Veuillez sélectionner tous les champs requis", 
                        "Champs requis", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Créer la livraison
                Livraison nouvelleLivraison = new Livraison(selectedLivreur, selectedVehicule, selectedCommande);
                
                // Définir l'heure de départ
                try {
                    nouvelleLivraison.setHeureDepart(java.time.LocalTime.parse(heureDepart.getText()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Format d'heure invalide. Utilisez le format HH:MM", 
                        "Erreur de format", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Sauvegarder la livraison
                LivraisonDAO livraisonDAO = new LivraisonDAO();
                if (livraisonDAO.inserer(nouvelleLivraison)) {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Livraison créée avec succès!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir le tableau principal
                    chargerLivraisons(tableModel, null, "Toutes les livraisons");
                    
                    createDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(createDialog, 
                        "Erreur lors de la création de la livraison", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(createDialog, 
                    "Erreur inattendue : " + ex.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        // Action d'annulation
        cancelButton.addActionListener(e -> createDialog.dispose());
        
        createDialog.setContentPane(mainPanel);
        createDialog.setVisible(true);
    }
    
    /**
     * Crée le panneau des statistiques
     * @return Le panneau de statistiques
     */
    private JPanel createStatistiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Titre principal
        JLabel titleLabel = new JLabel("📊 Tableau de Bord - Statistiques RaPizz");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panneau principal avec scroll
        JPanel mainStatsPanel = new JPanel();
        mainStatsPanel.setLayout(new BoxLayout(mainStatsPanel, BoxLayout.Y_AXIS));
        
        // Bouton de rafraîchissement
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("🔄 Actualiser les statistiques");
        refreshButton.setPreferredSize(new Dimension(200, 30));
        refreshPanel.add(refreshButton);
        mainStatsPanel.add(refreshPanel);
        
        // Section 1: Statistiques générales
        mainStatsPanel.add(creerSectionStatistiquesGenerales());
        mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Section 2: Statistiques des commandes
        mainStatsPanel.add(creerSectionStatistiquesCommandes());
        mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Section 3: Statistiques des livraisons
        mainStatsPanel.add(creerSectionStatistiquesLivraisons());
        mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Section 4: Top des pizzas
        mainStatsPanel.add(creerSectionTopPizzas());
        mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Section 5: Statistiques des clients
        mainStatsPanel.add(creerSectionStatistiquesClients());
        
        // Action du bouton de rafraîchissement
        refreshButton.addActionListener(e -> {
            // Recharger toutes les statistiques
            mainStatsPanel.removeAll();
            
            // Recréer le bouton de rafraîchissement
            JPanel newRefreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton newRefreshButton = new JButton("🔄 Actualiser les statistiques");
            newRefreshButton.setPreferredSize(new Dimension(200, 30));
            newRefreshPanel.add(newRefreshButton);
            mainStatsPanel.add(newRefreshPanel);
            
            // Recréer toutes les sections
            mainStatsPanel.add(creerSectionStatistiquesGenerales());
            mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainStatsPanel.add(creerSectionStatistiquesCommandes());
            mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainStatsPanel.add(creerSectionStatistiquesLivraisons());
            mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainStatsPanel.add(creerSectionTopPizzas());
            mainStatsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainStatsPanel.add(creerSectionStatistiquesClients());
            
            // Réassigner l'action au nouveau bouton
            newRefreshButton.addActionListener(refreshButton.getActionListeners()[0]);
            
            // Rafraîchir l'affichage
            mainStatsPanel.revalidate();
            mainStatsPanel.repaint();
            
            JOptionPane.showMessageDialog(panel, 
                "Statistiques actualisées avec succès!", 
                "Actualisation", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Ajouter le panneau principal dans un scroll pane
        JScrollPane scrollPane = new JScrollPane(mainStatsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée la section des statistiques générales
     */
    private JPanel creerSectionStatistiquesGenerales() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2), 
            "📈 Statistiques Générales", 
            0, 0, new Font("Dialog", Font.BOLD, 14), Color.BLUE));
        
        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            // Récupérer les données
            ClientDAO clientDAO = new ClientDAO();
            PizzaDAO pizzaDAO = new PizzaDAO();
            CommandeDAO commandeDAO = new CommandeDAO();
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            
            List<Client> clients = clientDAO.trouverTous();
            List<Pizza> pizzas = pizzaDAO.trouverTous();
            List<Commande> commandes = commandeDAO.trouverTousSansDetails();
            List<Livraison> livraisons = livraisonDAO.trouverTousSansDetails();
            
            // Créer les cartes de statistiques
            gridPanel.add(creerCarteStatistique("👥 Clients", String.valueOf(clients.size()), Color.CYAN));
            gridPanel.add(creerCarteStatistique("🍕 Pizzas", String.valueOf(pizzas.size()), Color.ORANGE));
            gridPanel.add(creerCarteStatistique("📋 Commandes", String.valueOf(commandes.size()), Color.GREEN));
            gridPanel.add(creerCarteStatistique("🚚 Livraisons", String.valueOf(livraisons.size()), Color.MAGENTA));
            
            // Calculer le chiffre d'affaires total
            double chiffreAffaires = 0.0;
            for (Commande commande : commandes) {
                if (!commande.isEstGratuite()) {
                    chiffreAffaires += commandeDAO.calculerMontantTotal(commande.getIdCommande());
                }
            }
            
            // Calculer le solde total des clients
            double soldeTotal = clients.stream().mapToDouble(Client::getSoldeCompte).sum();
            
            // Compter les commandes gratuites
            long commandesGratuites = commandes.stream().filter(Commande::isEstGratuite).count();
            
            // Compter les livraisons en retard
            long livraisonsEnRetard = livraisons.stream().filter(Livraison::isEstEnRetard).count();
            
            gridPanel.add(creerCarteStatistique("💰 CA Total", String.format("%.2f€", chiffreAffaires), Color.YELLOW));
            gridPanel.add(creerCarteStatistique("💳 Solde Clients", String.format("%.2f€", soldeTotal), Color.PINK));
            gridPanel.add(creerCarteStatistique("🎁 Cmd Gratuites", String.valueOf(commandesGratuites), Color.LIGHT_GRAY));
            gridPanel.add(creerCarteStatistique("⚠️ Retards", String.valueOf(livraisonsEnRetard), Color.RED));
            
        } catch (Exception e) {
            gridPanel.add(new JLabel("Erreur lors du chargement des statistiques: " + e.getMessage()));
        }
        
        section.add(gridPanel, BorderLayout.CENTER);
        return section;
    }
    
    /**
     * Crée la section des statistiques des commandes
     */
    private JPanel creerSectionStatistiquesCommandes() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GREEN, 2), 
            "📋 Statistiques des Commandes", 
            0, 0, new Font("Dialog", Font.BOLD, 14), Color.GREEN));
        
        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.trouverTousSansDetails();
            
            // Compter par statut
            long enPreparation = commandes.stream().filter(c -> c.getStatut() == Commande.Statut.EN_PREPARATION).count();
            long enLivraison = commandes.stream().filter(c -> c.getStatut() == Commande.Statut.EN_LIVRAISON).count();
            long livrees = commandes.stream().filter(c -> c.getStatut() == Commande.Statut.LIVREE).count();
            long annulees = commandes.stream().filter(c -> c.getStatut() == Commande.Statut.ANNULEE).count();
            
            JPanel gridPanel = new JPanel(new GridLayout(1, 4, 10, 10));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            gridPanel.add(creerCarteStatistique("🔄 En préparation", String.valueOf(enPreparation), Color.ORANGE));
            gridPanel.add(creerCarteStatistique("🚚 En livraison", String.valueOf(enLivraison), Color.BLUE));
            gridPanel.add(creerCarteStatistique("✅ Livrées", String.valueOf(livrees), Color.GREEN));
            gridPanel.add(creerCarteStatistique("❌ Annulées", String.valueOf(annulees), Color.RED));
            
            section.add(gridPanel, BorderLayout.CENTER);
            
        } catch (Exception e) {
            section.add(new JLabel("Erreur: " + e.getMessage()), BorderLayout.CENTER);
        }
        
        return section;
    }
    
    /**
     * Crée la section des statistiques des livraisons
     */
    private JPanel creerSectionStatistiquesLivraisons() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.MAGENTA, 2), 
            "🚚 Statistiques des Livraisons", 
            0, 0, new Font("Dialog", Font.BOLD, 14), Color.MAGENTA));
        
        try {
            LivraisonDAO livraisonDAO = new LivraisonDAO();
            List<Livraison> livraisons = livraisonDAO.trouverTousSansDetails();
            
            // Calculer les statistiques
            long enCours = livraisons.stream().filter(l -> l.getHeureArrivee() == null && !l.isEstEnRetard()).count();
            long terminees = livraisons.stream().filter(l -> l.getHeureArrivee() != null).count();
            long enRetard = livraisons.stream().filter(Livraison::isEstEnRetard).count();
            
            // Calculer la durée moyenne des livraisons terminées
            double dureeMoyenne = livraisons.stream()
                .filter(l -> l.getHeureArrivee() != null)
                .mapToLong(Livraison::calculerDureeMinutes)
                .filter(d -> d >= 0)
                .average()
                .orElse(0.0);
            
            JPanel gridPanel = new JPanel(new GridLayout(1, 4, 10, 10));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            gridPanel.add(creerCarteStatistique("🔄 En cours", String.valueOf(enCours), Color.BLUE));
            gridPanel.add(creerCarteStatistique("✅ Terminées", String.valueOf(terminees), Color.GREEN));
            gridPanel.add(creerCarteStatistique("⚠️ En retard", String.valueOf(enRetard), Color.RED));
            gridPanel.add(creerCarteStatistique("⏱️ Durée moy.", String.format("%.1f min", dureeMoyenne), Color.CYAN));
            
            section.add(gridPanel, BorderLayout.CENTER);
            
        } catch (Exception e) {
            section.add(new JLabel("Erreur: " + e.getMessage()), BorderLayout.CENTER);
        }
        
        return section;
    }
    
    /**
     * Crée la section du top des pizzas
     */
    private JPanel creerSectionTopPizzas() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.ORANGE, 2), 
            "🍕 Top 5 des Pizzas les Plus Commandées", 
            0, 0, new Font("Dialog", Font.BOLD, 14), Color.ORANGE));
        
        try {
            // Créer un tableau pour afficher le top des pizzas
            String[] columns = {"Rang", "Pizza", "Nombre de commandes", "Chiffre d'affaires"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            
            // Calculer les statistiques des pizzas
            Map<String, Integer> pizzaCommandes = new HashMap<>();
            Map<String, Double> pizzaCA = new HashMap<>();
            
            DetailCommandeDAO detailDAO = new DetailCommandeDAO();
            CommandeDAO commandeDAO = new CommandeDAO();
            List<Commande> commandes = commandeDAO.trouverTousSansDetails();
            
            for (Commande commande : commandes) {
                if (!commande.isEstGratuite()) { // Exclure les commandes gratuites du CA
                    List<DetailCommande> details = detailDAO.trouverParCommande(commande.getIdCommande());
                    for (DetailCommande detail : details) {
                        String nomPizza = detail.getPizza().getNom();
                        int quantite = detail.getQuantite();
                        double sousTotal = detail.calculerSousTotal();
                        
                        pizzaCommandes.put(nomPizza, pizzaCommandes.getOrDefault(nomPizza, 0) + quantite);
                        pizzaCA.put(nomPizza, pizzaCA.getOrDefault(nomPizza, 0.0) + sousTotal);
                    }
                }
            }
            
            // Trier par nombre de commandes et prendre le top 5
            pizzaCommandes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    String nomPizza = entry.getKey();
                    int nbCommandes = entry.getValue();
                    double ca = pizzaCA.getOrDefault(nomPizza, 0.0);
                    
                    model.addRow(new Object[]{
                        model.getRowCount() + 1,
                        nomPizza,
                        nbCommandes,
                        String.format("%.2f€", ca)
                    });
                });
            
            // Styliser le tableau
            table.setRowHeight(25);
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(200);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(550, 150));
            section.add(scrollPane, BorderLayout.CENTER);
            
        } catch (Exception e) {
            section.add(new JLabel("Erreur: " + e.getMessage()), BorderLayout.CENTER);
        }
        
        return section;
    }
    
    /**
     * Crée la section des statistiques des clients
     */
    private JPanel creerSectionStatistiquesClients() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.CYAN, 2), 
            "👥 Statistiques des Clients", 
            0, 0, new Font("Dialog", Font.BOLD, 14), Color.CYAN));
        
        try {
            ClientDAO clientDAO = new ClientDAO();
            List<Client> clients = clientDAO.trouverTous();
            
            // Calculer les statistiques
            double soldeMoyen = clients.stream().mapToDouble(Client::getSoldeCompte).average().orElse(0.0);
            double pizzasMoyennes = clients.stream().mapToDouble(Client::getNbPizzasAchetees).average().orElse(0.0);
            
            Client clientPlusRiche = clients.stream()
                .max((c1, c2) -> Double.compare(c1.getSoldeCompte(), c2.getSoldeCompte()))
                .orElse(null);
            
            Client clientPlusActif = clients.stream()
                .max((c1, c2) -> Integer.compare(c1.getNbPizzasAchetees(), c2.getNbPizzasAchetees()))
                .orElse(null);
            
            JPanel gridPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            gridPanel.add(creerCarteStatistique("💰 Solde moyen", String.format("%.2f€", soldeMoyen), Color.YELLOW));
            gridPanel.add(creerCarteStatistique("🍕 Pizzas moy.", String.format("%.1f", pizzasMoyennes), Color.ORANGE));
            
            if (clientPlusRiche != null) {
                gridPanel.add(creerCarteStatistique("🏆 Client le + riche", 
                    clientPlusRiche.getNom() + " " + clientPlusRiche.getPrenom() + 
                    " (" + String.format("%.2f€", clientPlusRiche.getSoldeCompte()) + ")", new Color(255, 215, 0))); // Couleur dorée
            } else {
                gridPanel.add(creerCarteStatistique("🏆 Client le + riche", "Aucun", Color.LIGHT_GRAY));
            }
            
            if (clientPlusActif != null) {
                gridPanel.add(creerCarteStatistique("🥇 Client le + actif", 
                    clientPlusActif.getNom() + " " + clientPlusActif.getPrenom() + 
                    " (" + clientPlusActif.getNbPizzasAchetees() + " pizzas)", Color.PINK));
            } else {
                gridPanel.add(creerCarteStatistique("🥇 Client le + actif", "Aucun", Color.LIGHT_GRAY));
            }
            
            section.add(gridPanel, BorderLayout.CENTER);
            
        } catch (Exception e) {
            section.add(new JLabel("Erreur: " + e.getMessage()), BorderLayout.CENTER);
        }
        
        return section;
    }
    
    /**
     * Crée une carte de statistique avec un titre, une valeur et une couleur
     */
    private JPanel creerCarteStatistique(String titre, String valeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        carte.setBackground(couleur.brighter().brighter());
        
        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        titreLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel valeurLabel = new JLabel(valeur);
        valeurLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        valeurLabel.setHorizontalAlignment(JLabel.CENTER);
        
        carte.add(titreLabel, BorderLayout.NORTH);
        carte.add(valeurLabel, BorderLayout.CENTER);
        
        return carte;
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
            modele.addRow(new Object[] { 
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