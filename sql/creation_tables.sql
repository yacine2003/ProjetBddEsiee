-- Script de création des tables – RaPizz (option B)
DROP DATABASE IF EXISTS rapizz;
CREATE DATABASE rapizz CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rapizz;

-- Suppression dans l’ordre des dépendances
DROP TABLE IF EXISTS DetailCommande;
DROP TABLE IF EXISTS CompositionPizza;
DROP TABLE IF EXISTS Livraison;
DROP TABLE IF EXISTS Commande;
DROP TABLE IF EXISTS Vehicule;
DROP TABLE IF EXISTS Livreur;
DROP TABLE IF EXISTS Pizza;
DROP TABLE IF EXISTS Ingredient;
DROP TABLE IF EXISTS Taille;
DROP TABLE IF EXISTS Client;

-- ---------- Tables de référence ----------
CREATE TABLE Client (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    solde_compte DECIMAL(10,2) DEFAULT 0.00,
    nb_pizzas_achetees INT DEFAULT 0
);

CREATE TABLE Ingredient (
    id_ingredient INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

CREATE TABLE Pizza (
    id_pizza INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL UNIQUE,
    prix_base DECIMAL(6,2) NOT NULL
);

CREATE TABLE Taille (
    id_taille INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(20) NOT NULL UNIQUE,
    coefficient_prix DECIMAL(5,3) NOT NULL   -- 0.666 · 1.000 · 1.333
);

CREATE TABLE Livreur (
    id_livreur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    nb_retards INT DEFAULT 0
);

CREATE TABLE Vehicule (
    id_vehicule INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('voiture','moto','scooter','velo') NOT NULL,
    immatriculation VARCHAR(10),
    statut ENUM('disponible','en_livraison','maintenance') DEFAULT 'disponible'
);

-- ---------- Tables métiers ----------
CREATE TABLE Commande (
    id_commande INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATE NOT NULL,
    heure_commande TIME NOT NULL,
    statut ENUM('en_preparation','en_livraison','livree','annulee') DEFAULT 'en_preparation',
    est_gratuite BOOLEAN DEFAULT FALSE,
    id_client INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES Client(id_client)
);

CREATE TABLE Livraison (
    id_livraison INT PRIMARY KEY AUTO_INCREMENT,
    heure_depart TIME NOT NULL,
    heure_arrivee TIME,
    est_en_retard BOOLEAN DEFAULT FALSE,
    id_livreur INT NOT NULL,
    id_vehicule INT NOT NULL,
    id_commande INT NOT NULL,
    FOREIGN KEY (id_livreur)  REFERENCES Livreur(id_livreur),
    FOREIGN KEY (id_vehicule) REFERENCES Vehicule(id_vehicule),
    FOREIGN KEY (id_commande) REFERENCES Commande(id_commande)
);

CREATE TABLE CompositionPizza (
    id_pizza INT NOT NULL,
    id_ingredient INT NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_pizza, id_ingredient),
    FOREIGN KEY (id_pizza)     REFERENCES Pizza(id_pizza)     ON DELETE CASCADE,
    FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient)
);

CREATE TABLE DetailCommande (
    id_commande INT NOT NULL,
    id_pizza    INT NOT NULL,
    id_taille   INT NOT NULL,          -- <- remplace l’ENUM
    quantite    INT NOT NULL DEFAULT 1,
    prix_unitaire DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (id_commande, id_pizza, id_taille),
    FOREIGN KEY (id_commande) REFERENCES Commande(id_commande) ON DELETE CASCADE,
    FOREIGN KEY (id_pizza)    REFERENCES Pizza(id_pizza),
    FOREIGN KEY (id_taille)   REFERENCES Taille(id_taille)
);

-- ---------- Index ----------
CREATE INDEX idx_client_nom               ON Client(nom, prenom);
CREATE INDEX idx_commande_client          ON Commande(id_client);
CREATE INDEX idx_livraison_commande       ON Livraison(id_commande);
CREATE INDEX idx_detailcommande_commande  ON DetailCommande(id_commande);
CREATE INDEX idx_detailcommande_pizza     ON DetailCommande(id_pizza);
CREATE INDEX idx_detailcommande_taille    ON DetailCommande(id_taille);