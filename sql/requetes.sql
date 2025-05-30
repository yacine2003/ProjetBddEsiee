USE rapizz;

------------------------------------------------------------
-- 1) MENU (inchangé, car on calcule les prix « naine/ogresse » nous‑mêmes)
------------------------------------------------------------
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

------------------------------------------------------------
-- 2) FICHE DE LIVRAISON  (⚠️ jointure Taille + libellé)
------------------------------------------------------------
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
        CONCAT(p.nom, ' (', t.libelle, ') x', dc.quantite)
        SEPARATOR ', '
    ) AS pizzas,
    SUM(dc.prix_unitaire * dc.quantite) AS prix_total,
    IF(com.est_gratuite, 'OUI', 'NON') AS commande_gratuite
FROM Livraison l
JOIN Livreur lv        ON l.id_livreur  = lv.id_livreur
JOIN Vehicule v        ON l.id_vehicule = v.id_vehicule
JOIN Commande com      ON l.id_commande = com.id_commande
JOIN Client c          ON com.id_client = c.id_client
JOIN DetailCommande dc ON com.id_commande = dc.id_commande
JOIN Taille t          ON dc.id_taille  = t.id_taille      -- ← nouvelle jointure
JOIN Pizza p           ON dc.id_pizza   = p.id_pizza
WHERE l.id_livraison = 3   -- à remplacer par la livraison voulue
GROUP BY l.id_livraison, lv.nom, lv.prenom, v.type, v.immatriculation,
         c.nom, c.prenom, c.adresse, c.telephone,
         com.date_commande, com.heure_commande,
         duree_livraison, l.est_en_retard, com.est_gratuite;

------------------------------------------------------------
-- 3) QUESTIONS DIVERSES  (Aucune référence à dc.taille → restent valides)
------------------------------------------------------------

-- a) Véhicules n’ayant jamais servi
SELECT v.id_vehicule, v.type, v.immatriculation
FROM Vehicule v
LEFT JOIN Livraison l ON v.id_vehicule = l.id_vehicule
WHERE l.id_vehicule IS NULL;

-- b) Nombre de commandes par client
SELECT 
    c.id_client,
    CONCAT(c.nom, ' ', c.prenom) AS client,
    COUNT(com.id_commande) AS nombre_commandes
FROM Client c
LEFT JOIN Commande com ON c.id_client = com.id_client
GROUP BY c.id_client, c.nom, c.prenom
ORDER BY nombre_commandes DESC;

-- c) Moyenne de commandes
SELECT AVG(sous.nombre_commandes) AS moyenne_commandes
FROM (
    SELECT c.id_client, COUNT(com.id_commande) AS nombre_commandes
    FROM Client c
    LEFT JOIN Commande com ON c.id_client = com.id_client
    GROUP BY c.id_client
) AS sous;

-- d) Clients > moyenne
WITH commandes_par_client AS (
    SELECT c.id_client,
           CONCAT(c.nom, ' ', c.prenom) AS client,
           COUNT(com.id_commande) AS nombre_commandes
    FROM Client c
    LEFT JOIN Commande com ON c.id_client = com.id_client
    GROUP BY c.id_client, c.nom, c.prenom
),
moyenne AS (
    SELECT AVG(nombre_commandes) AS moyenne_commandes FROM commandes_par_client
)
SELECT cpc.*, m.moyenne_commandes
FROM commandes_par_client cpc, moyenne m
WHERE cpc.nombre_commandes > m.moyenne_commandes
ORDER BY cpc.nombre_commandes DESC;

------------------------------------------------------------
-- Statistiques (inchangées, pas de taille utilisée)
------------------------------------------------------------

-- Meilleur client
SELECT c.id_client,
       CONCAT(c.nom, ' ', c.prenom) AS client,
       SUM(dc.prix_unitaire * dc.quantite) AS montant_total_depense
FROM Client c
JOIN Commande com      ON c.id_client = com.id_client
JOIN DetailCommande dc ON com.id_commande = dc.id_commande
WHERE com.est_gratuite = FALSE
GROUP BY c.id_client
ORDER BY montant_total_depense DESC
LIMIT 1;

-- Plus mauvais livreur
SELECT id_livreur, CONCAT(nom, ' ', prenom) AS livreur, nb_retards
FROM Livreur
ORDER BY nb_retards DESC
LIMIT 1;

-- Pizza la plus demandée
SELECT p.id_pizza, p.nom,
       SUM(dc.quantite) AS nombre_total_commande
FROM Pizza p
JOIN DetailCommande dc ON p.id_pizza = dc.id_pizza
GROUP BY p.id_pizza, p.nom
ORDER BY nombre_total_commande DESC
LIMIT 1;

-- Pizza la moins demandée
SELECT p.id_pizza, p.nom,
       SUM(IFNULL(dc.quantite,0)) AS nombre_total_commande
FROM Pizza p
LEFT JOIN DetailCommande dc ON p.id_pizza = dc.id_pizza
GROUP BY p.id_pizza, p.nom
ORDER BY nombre_total_commande ASC
LIMIT 1;

-- Ingrédient favori
SELECT i.id_ingredient, i.nom,
       SUM(cp.quantite * dc.quantite) AS utilisation_totale
FROM Ingredient i
JOIN CompositionPizza cp ON i.id_ingredient = cp.id_ingredient
JOIN DetailCommande dc   ON cp.id_pizza   = dc.id_pizza
GROUP BY i.id_ingredient, i.nom
ORDER BY utilisation_totale DESC
LIMIT 1;