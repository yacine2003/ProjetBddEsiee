-- Script des requêtes SQL pour la base de données RaPizz
-- Projet BDD ESIEE

-- Sélection de la base de données
USE rapizz;

-- 1) MENU : Extraction des données pour imprimer la carte
-- Nom des pizzas, prix et ingrédients qui la composent
SELECT p.nom AS nom_pizza, 
       p.prix_base AS prix_base,
       ROUND(p.prix_base * 0.67, 2) AS prix_naine,
       ROUND(p.prix_base * 1.33, 2) AS prix_ogresse,
       GROUP_CONCAT(i.nom SEPARATOR ', ') AS ingredients
FROM Pizza p
JOIN CompositionPizza cp ON p.id_pizza = cp.id_pizza
JOIN Ingredient i ON cp.id_ingredient = i.id_ingredient
GROUP BY p.id_pizza, p.nom, p.prix_base
ORDER BY p.nom;

-- 2) FICHE DE LIVRAISON : Extraction des données pour la fiche de livraison
-- Informations sur le livreur, véhicule, client, date commande, retard éventuel, pizza et prix
SELECT 
    l.id_livraison,
    CONCAT(lv.nom, ' ', lv.prenom) AS nom_livreur,
    v.type AS type_vehicule, 
    v.immatriculation,
    CONCAT(c.nom, ' ', c.prenom) AS nom_client,
    c.adresse AS adresse_client,
    c.telephone AS telephone_client,
    com.date_commande,
    com.heure_commande,
    TIMESTAMPDIFF(MINUTE, l.heure_depart, l.heure_arrivee) AS duree_livraison,
    IF(l.est_en_retard, 'OUI', 'NON') AS retard,
    GROUP_CONCAT(
        CONCAT(p.nom, ' (', dc.taille, ') ', 'x', dc.quantite) 
        SEPARATOR ', '
    ) AS pizzas,
    SUM(dc.prix_unitaire * dc.quantite) AS prix_total,
    IF(com.est_gratuite, 'OUI', 'NON') AS commande_gratuite
FROM Livraison l
JOIN Livreur lv ON l.id_livreur = lv.id_livreur
JOIN Vehicule v ON l.id_vehicule = v.id_vehicule
JOIN Commande com ON l.id_commande = com.id_commande
JOIN Client c ON com.id_client = c.id_client
JOIN DetailCommande dc ON com.id_commande = dc.id_commande
JOIN Pizza p ON dc.id_pizza = p.id_pizza
WHERE l.id_livraison = 3  -- Remplacer par l'ID de la livraison souhaitée
GROUP BY l.id_livraison, lv.nom, lv.prenom, v.type, v.immatriculation, 
         c.nom, c.prenom, c.adresse, c.telephone, com.date_commande, 
         com.heure_commande, duree_livraison, l.est_en_retard, com.est_gratuite;

-- 3) QUESTIONS DIVERSES

-- a) Quels sont les véhicules n'ayant jamais servi ?
SELECT v.id_vehicule, v.type, v.immatriculation
FROM Vehicule v
LEFT JOIN Livraison l ON v.id_vehicule = l.id_vehicule
WHERE l.id_vehicule IS NULL;

-- b) Calcul du nombre de commandes par client
SELECT 
    c.id_client,
    CONCAT(c.nom, ' ', c.prenom) AS client,
    COUNT(com.id_commande) AS nombre_commandes
FROM Client c
LEFT JOIN Commande com ON c.id_client = com.id_client
GROUP BY c.id_client, c.nom, c.prenom
ORDER BY nombre_commandes DESC;

-- c) Calcul de la moyenne des commandes
SELECT 
    AVG(commandes_par_client.nombre_commandes) AS moyenne_commandes
FROM (
    SELECT 
        c.id_client,
        COUNT(com.id_commande) AS nombre_commandes
    FROM Client c
    LEFT JOIN Commande com ON c.id_client = com.id_client
    GROUP BY c.id_client
) AS commandes_par_client;

-- d) Extraction des clients ayant commandé plus que la moyenne
WITH commandes_par_client AS (
    SELECT 
        c.id_client,
        CONCAT(c.nom, ' ', c.prenom) AS client,
        COUNT(com.id_commande) AS nombre_commandes
    FROM Client c
    LEFT JOIN Commande com ON c.id_client = com.id_client
    GROUP BY c.id_client, c.nom, c.prenom
),
moyenne AS (
    SELECT AVG(nombre_commandes) AS moyenne_commandes
    FROM commandes_par_client
)
SELECT 
    cpc.id_client,
    cpc.client,
    cpc.nombre_commandes,
    m.moyenne_commandes
FROM commandes_par_client cpc, moyenne m
WHERE cpc.nombre_commandes > m.moyenne_commandes
ORDER BY cpc.nombre_commandes DESC;

-- Requêtes supplémentaires pour les statistiques

-- Identification du meilleur client (celui qui a dépensé le plus)
SELECT 
    c.id_client,
    CONCAT(c.nom, ' ', c.prenom) AS client,
    SUM(dc.prix_unitaire * dc.quantite) AS montant_total_depense
FROM Client c
JOIN Commande com ON c.id_client = com.id_client
JOIN DetailCommande dc ON com.id_commande = dc.id_commande
WHERE com.est_gratuite = FALSE  -- Exclure les commandes gratuites
GROUP BY c.id_client, c.nom, c.prenom
ORDER BY montant_total_depense DESC
LIMIT 1;

-- Identification du plus mauvais livreur (nombre de retards)
SELECT 
    l.id_livreur,
    CONCAT(l.nom, ' ', l.prenom) AS livreur,
    l.nb_retards
FROM Livreur l
ORDER BY l.nb_retards DESC
LIMIT 1;

-- Identification de la pizza la plus demandée
SELECT 
    p.id_pizza,
    p.nom,
    SUM(dc.quantite) AS nombre_total_commande
FROM Pizza p
JOIN DetailCommande dc ON p.id_pizza = dc.id_pizza
GROUP BY p.id_pizza, p.nom
ORDER BY nombre_total_commande DESC
LIMIT 1;

-- Identification de la pizza la moins demandée
SELECT 
    p.id_pizza,
    p.nom,
    SUM(IFNULL(dc.quantite, 0)) AS nombre_total_commande
FROM Pizza p
LEFT JOIN DetailCommande dc ON p.id_pizza = dc.id_pizza
GROUP BY p.id_pizza, p.nom
ORDER BY nombre_total_commande ASC
LIMIT 1;

-- Identification de l'ingrédient favori
SELECT 
    i.id_ingredient,
    i.nom,
    SUM(cp.quantite * dc.quantite) AS utilisation_totale
FROM Ingredient i
JOIN CompositionPizza cp ON i.id_ingredient = cp.id_ingredient
JOIN DetailCommande dc ON cp.id_pizza = dc.id_pizza
GROUP BY i.id_ingredient, i.nom
ORDER BY utilisation_totale DESC
LIMIT 1; 