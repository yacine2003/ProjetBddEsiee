-- Script d'insertion des données pour la base de données RaPizz
-- Projet BDD ESIEE

-- Sélection de la base de données
USE rapizz;

-- Insertion des clients
INSERT INTO Client (nom, prenom, adresse, telephone, solde_compte, nb_pizzas_achetees) VALUES
('Dubois', 'Jean', '12 rue de la Paix, Paris', '0612345678', 50.00, 3),
('Martin', 'Sophie', '24 avenue Victor Hugo, Lyon', '0723456789', 75.50, 8),
('Petit', 'Pierre', '8 rue des Lilas, Marseille', '0634567890', 30.00, 2),
('Leroy', 'Emma', '15 boulevard Voltaire, Lille', '0745678901', 100.00, 12),
('Moreau', 'Lucas', '3 place de la République, Toulouse', '0656789012', 15.25, 1);

-- Insertion des ingrédients
INSERT INTO Ingredient (nom, stock) VALUES
('Sauce tomate', 100),
('Mozzarella', 80),
('Jambon', 50),
('Champignons', 60),
('Olives', 70),
('Poivrons', 45),
('Pepperoni', 40),
('Ananas', 30),
('Oignons', 55),
('Bacon', 35),
('Thon', 30),
('Chèvre', 25),
('Gorgonzola', 20),
('Emmental', 30);

-- Insertion des pizzas
INSERT INTO Pizza (nom, prix_base) VALUES
('Margherita', 8.00),
('Reine', 10.00),
('Pepperoni', 9.50),
('Quatre Fromages', 11.00),
('Végétarienne', 9.00),
('Hawaïenne', 10.50),
('Calzone', 12.00);

-- Insertion des compositions de pizzas
-- Margherita
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(1, 1, 1), -- Sauce tomate
(1, 2, 1); -- Mozzarella

-- Reine
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(2, 1, 1), -- Sauce tomate
(2, 2, 1), -- Mozzarella
(2, 3, 1), -- Jambon
(2, 4, 1); -- Champignons

-- Pepperoni
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(3, 1, 1), -- Sauce tomate
(3, 2, 1), -- Mozzarella
(3, 7, 2); -- Pepperoni (double dose)

-- Quatre Fromages
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(4, 1, 1), -- Sauce tomate
(4, 2, 1), -- Mozzarella
(4, 12, 1), -- Chèvre
(4, 13, 1), -- Gorgonzola
(4, 14, 1); -- Emmental

-- Végétarienne
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(5, 1, 1), -- Sauce tomate
(5, 2, 1), -- Mozzarella
(5, 4, 1), -- Champignons
(5, 5, 1), -- Olives
(5, 6, 1), -- Poivrons
(5, 9, 1); -- Oignons

-- Hawaïenne
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(6, 1, 1), -- Sauce tomate
(6, 2, 1), -- Mozzarella
(6, 3, 1), -- Jambon
(6, 8, 2); -- Ananas (double dose)

-- Calzone
INSERT INTO CompositionPizza (id_pizza, id_ingredient, quantite) VALUES
(7, 1, 1), -- Sauce tomate
(7, 2, 1), -- Mozzarella
(7, 3, 1), -- Jambon
(7, 4, 1); -- Champignons

-- Insertion des livreurs
INSERT INTO Livreur (nom, prenom, telephone, nb_retards) VALUES
('Durand', 'Thomas', '0601020304', 1),
('Garcia', 'Julie', '0602030405', 0),
('Bernard', 'Nicolas', '0603040506', 3),
('Lopez', 'Marie', '0604050607', 0);

-- Insertion des véhicules
INSERT INTO Vehicule (type, immatriculation, statut) VALUES
('voiture', 'AB-123-CD', 'disponible'),
('scooter', 'DE-456-FG', 'disponible'),
('moto', 'GH-789-IJ', 'disponible'),
('voiture', 'KL-012-MN', 'disponible'),
('velo', NULL, 'disponible');

-- Insertion des commandes (fictives pour la démo)
INSERT INTO Commande (date_commande, heure_commande, statut, est_gratuite, id_client) VALUES
('2023-11-01', '12:30:00', 'livree', FALSE, 1),
('2023-11-01', '19:15:00', 'livree', FALSE, 2),
('2023-11-02', '20:00:00', 'livree', TRUE, 3),  -- Gratuite (retard)
('2023-11-03', '13:45:00', 'livree', FALSE, 4),
('2023-11-03', '21:10:00', 'livree', TRUE, 4),  -- Gratuite (fidélité)
('2023-11-04', '18:30:00', 'en_livraison', FALSE, 5),
('2023-11-04', '19:00:00', 'en_preparation', FALSE, 1);

-- Insertion des livraisons
INSERT INTO Livraison (heure_depart, heure_arrivee, est_en_retard, id_livreur, id_vehicule, id_commande) VALUES
('12:45:00', '13:05:00', FALSE, 1, 1, 1),
('19:25:00', '19:40:00', FALSE, 2, 2, 2),
('20:10:00', '20:45:00', TRUE, 3, 3, 3),   -- Retard
('13:50:00', '14:10:00', FALSE, 4, 4, 4),
('21:20:00', '21:35:00', FALSE, 1, 1, 5),
('18:45:00', NULL, FALSE, 2, 2, 6);        -- En cours

-- Insertion des détails de commandes
INSERT INTO DetailCommande (id_commande, id_pizza, taille, quantite, prix_unitaire) VALUES
(1, 1, 'humaine', 1, 8.00),
(1, 3, 'naine', 1, 6.33),     -- Prix réduit de 1/3 pour taille naine
(2, 4, 'ogresse', 1, 14.67),  -- Prix augmenté de 1/3 pour taille ogresse
(2, 5, 'humaine', 2, 9.00),
(3, 2, 'humaine', 1, 10.00),
(4, 7, 'humaine', 1, 12.00),
(5, 6, 'humaine', 1, 10.50),
(6, 3, 'ogresse', 1, 12.67),  -- Prix augmenté de 1/3 pour taille ogresse
(7, 1, 'humaine', 3, 8.00); 