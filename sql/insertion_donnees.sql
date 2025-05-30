-- ==========================================================
-- Script d’insertion des données – RaPizz (schéma avec table Taille)
-- ==========================================================

USE rapizz;

-- ----------------------------------------------------------
-- 1) Tailles (⚠️ doit précéder tout INSERT dans DetailCommande)
-- ----------------------------------------------------------
INSERT INTO Taille (id_taille, libelle, coefficient_prix) VALUES
(1, 'naine',   0.666),
(2, 'humaine', 1.000),
(3, 'ogresse', 1.333);

-- ----------------------------------------------------------
-- 2) Clients
-- ----------------------------------------------------------
INSERT INTO Client (nom, prenom, adresse, telephone, solde_compte, nb_pizzas_achetees) VALUES
('Dubois', 'Jean',   '12 rue de la Paix, Paris',           '0612345678',  50.00, 3),
('Martin', 'Sophie', '24 avenue Victor Hugo, Lyon',        '0723456789',  75.50, 8),
('Petit',  'Pierre', '8 rue des Lilas, Marseille',         '0634567890',  30.00, 2),
('Leroy',  'Emma',   '15 boulevard Voltaire, Lille',       '0745678901', 100.00,12),
('Moreau', 'Lucas',  '3 place de la République, Toulouse', '0656789012',  15.25, 1);

-- ----------------------------------------------------------
-- 3) Ingrédients
-- ----------------------------------------------------------
INSERT INTO Ingredient (nom, stock) VALUES
('Sauce tomate', 100),
('Mozzarella',   80),
('Jambon',       50),
('Champignons',  60),
('Olives',       70),
('Poivrons',     45),
('Pepperoni',    40),
('Ananas',       30),
('Oignons',      55),
('Bacon',        35),
('Thon',         30),
('Chèvre',       25),
('Gorgonzola',   20),
('Emmental',     30);

-- ----------------------------------------------------------
-- 4) Pizzas
-- ----------------------------------------------------------
INSERT INTO Pizza (nom, prix_base) VALUES
('Margherita',      8.00),
('Reine',          10.00),
('Pepperoni',       9.50),
('Quatre Fromages',11.00),
('Végétarienne',    9.00),
('Hawaïenne',      10.50),
('Calzone',        12.00);

-- ----------------------------------------------------------
-- 5) Composition des pizzas
-- ----------------------------------------------------------
-- Margherita
INSERT INTO CompositionPizza VALUES
(1, 1, 1), -- Sauce tomate
(1, 2, 1); -- Mozzarella

-- Reine
INSERT INTO CompositionPizza VALUES
(2, 1, 1),
(2, 2, 1),
(2, 3, 1),
(2, 4, 1);

-- Pepperoni
INSERT INTO CompositionPizza VALUES
(3, 1, 1),
(3, 2, 1),
(3, 7, 2);

-- Quatre Fromages
INSERT INTO CompositionPizza VALUES
(4, 1, 1),
(4, 2, 1),
(4,12, 1),
(4,13, 1),
(4,14, 1);

-- Végétarienne
INSERT INTO CompositionPizza VALUES
(5, 1, 1),
(5, 2, 1),
(5, 4, 1),
(5, 5, 1),
(5, 6, 1),
(5, 9, 1);

-- Hawaïenne
INSERT INTO CompositionPizza VALUES
(6, 1, 1),
(6, 2, 1),
(6, 3, 1),
(6, 8, 2);

-- Calzone
INSERT INTO CompositionPizza VALUES
(7, 1, 1),
(7, 2, 1),
(7, 3, 1),
(7, 4, 1);

-- ----------------------------------------------------------
-- 6) Livreurs
-- ----------------------------------------------------------
INSERT INTO Livreur (nom, prenom, telephone, nb_retards) VALUES
('Durand',  'Thomas',  '0601020304', 1),
('Garcia',  'Julie',   '0602030405', 0),
('Bernard', 'Nicolas', '0603040506', 3),
('Lopez',   'Marie',   '0604050607', 0);

-- ----------------------------------------------------------
-- 7) Véhicules
-- ----------------------------------------------------------
INSERT INTO Vehicule (type, immatriculation, statut) VALUES
('voiture',  'AB-123-CD', 'disponible'),
('scooter',  'DE-456-FG', 'disponible'),
('moto',     'GH-789-IJ', 'disponible'),
('voiture',  'KL-012-MN', 'disponible'),
('velo',     NULL,        'disponible');

-- ----------------------------------------------------------
-- 8) Commandes
-- ----------------------------------------------------------
INSERT INTO Commande (date_commande, heure_commande, statut, est_gratuite, id_client) VALUES
('2023-11-01','12:30:00','livree',      FALSE, 1),
('2023-11-01','19:15:00','livree',      FALSE, 2),
('2023-11-02','20:00:00','livree',       TRUE, 3),
('2023-11-03','13:45:00','livree',      FALSE, 4),
('2023-11-03','21:10:00','livree',       TRUE, 4),
('2023-11-04','18:30:00','en_livraison',FALSE, 5),
('2023-11-04','19:00:00','en_preparation',FALSE,1);

-- ----------------------------------------------------------
-- 9) Livraisons
-- ----------------------------------------------------------
INSERT INTO Livraison (heure_depart, heure_arrivee, est_en_retard, id_livreur, id_vehicule, id_commande) VALUES
('12:45:00','13:05:00',FALSE,1,1,1),
('19:25:00','19:40:00',FALSE,2,2,2),
('20:10:00','20:45:00', TRUE,3,3,3),
('13:50:00','14:10:00',FALSE,4,4,4),
('21:20:00','21:35:00',FALSE,1,1,5),
('18:45:00',   NULL,   FALSE,2,2,6);

-- ----------------------------------------------------------
-- 10) Détails de commandes (id_taille ↔ Taille)
-- ----------------------------------------------------------
-- rappel : 1=naine, 2=humaine, 3=ogresse
INSERT INTO DetailCommande (id_commande, id_pizza, id_taille, quantite, prix_unitaire) VALUES
(1, 1, 2, 1,  8.00),  -- Margherita humaine
(1, 3, 1, 1,  6.33),  -- Pepperoni naine (8 € × 0.666 arrondi)
(2, 4, 3, 1, 14.67),  -- Quatre Fromages ogresse
(2, 5, 2, 2,  9.00),  -- Végétarienne humaine
(3, 2, 2, 1, 10.00),  -- Reine humaine
(4, 7, 2, 1, 12.00),  -- Calzone humaine
(5, 6, 2, 1, 10.50),  -- Hawaïenne humaine
(6, 3, 3, 1, 12.67),  -- Pepperoni ogresse (9.50 € × 1.333 arrondi)
(7, 1, 2, 3,  8.00);  -- Margherita humaine (×3)