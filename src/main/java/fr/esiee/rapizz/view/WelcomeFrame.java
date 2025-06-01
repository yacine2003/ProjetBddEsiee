package fr.esiee.rapizz.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Page d'accueil de l'application RaPizz
 */
public class WelcomeFrame extends JFrame {
    
    /**
     * Constructeur de la page d'accueil
     */
    public WelcomeFrame() {
        initComponents();
    }
    
    /**
     * Initialise les composants de la page d'accueil
     */
    private void initComponents() {
        // Configuration de la fenêtre
        setTitle("RaPizz - Bienvenue");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Panneau principal avec dégradé
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Dégradé de couleur
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 94, 77),  // Rouge-orange
                    0, getHeight(), new Color(255, 154, 0)  // Orange
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Panneau central pour le contenu
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        // Titre principal - Mieux centré
        JLabel titleLabel = new JLabel("🍕 RaPizz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Gestion de Pizzeria");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Description
        JLabel descriptionLabel = new JLabel("Application complète de gestion pour votre pizzeria");
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        descriptionLabel.setForeground(new Color(255, 255, 255, 220));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Espacement
        Component verticalGlue1 = Box.createVerticalGlue();
        Component verticalStrut1 = Box.createVerticalStrut(40);
        Component verticalStrut2 = Box.createVerticalStrut(15);
        Component verticalStrut3 = Box.createVerticalStrut(60);
        
        // Bouton d'accès à l'application - Design amélioré
        JButton enterButton = new JButton("🚀 ACCÉDER À L'APPLICATION") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 25, 25);
                
                // Fond du bouton avec dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(52, 152, 219),  // Bleu moderne
                    0, getHeight(), new Color(41, 128, 185)  // Bleu plus foncé
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);
                
                // Bordure
                g2d.setColor(new Color(30, 100, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        enterButton.setFont(new Font("Arial", Font.BOLD, 22));
        enterButton.setPreferredSize(new Dimension(450, 90));
        enterButton.setMaximumSize(new Dimension(450, 90));
        enterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterButton.setForeground(Color.WHITE);
        enterButton.setContentAreaFilled(false);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover sur le bouton - Plus sophistiqué
        enterButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                enterButton.setFont(new Font("Arial", Font.BOLD, 23));
                enterButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                enterButton.setFont(new Font("Arial", Font.BOLD, 22));
                enterButton.repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent evt) {
                enterButton.setLocation(enterButton.getX() + 2, enterButton.getY() + 2);
            }
            
            @Override
            public void mouseReleased(MouseEvent evt) {
                enterButton.setLocation(enterButton.getX() - 2, enterButton.getY() - 2);
            }
        });
        
        // Action du bouton
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fermer la page d'accueil
                dispose();
                
                // Ouvrir l'application principale
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                });
            }
        });
        
        // Informations en bas
        JLabel infoLabel = new JLabel("Projet de Base de Données - ESIEE Paris");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(255, 255, 255, 150));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Ajout des composants au panneau central
        centerPanel.add(verticalGlue1);
        centerPanel.add(titleLabel);
        centerPanel.add(verticalStrut1);
        centerPanel.add(subtitleLabel);
        centerPanel.add(verticalStrut2);
        centerPanel.add(descriptionLabel);
        centerPanel.add(verticalStrut3);
        centerPanel.add(enterButton);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(infoLabel);
        
        // Ajout au panneau principal
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Définir le panneau principal comme contenu de la fenêtre
        setContentPane(mainPanel);
    }
} 