# 🍕 Projet BDD ESIEE - Gestion d'une entreprise de pizzas à domicile

## Description du projet

Ce projet vise à modéliser la gestion d'une entreprise de fabrication et de livraison de pizzas à domicile nommée RaPizz. Il s'agit d'une société en franchise qui utilise des formats et des compositions de pizzas normalisées à partir d'un ensemble d'ingrédients déterminés.

## Fonctionnalités principales

- Gestion des clients et de leurs comptes prépayés
- Gestion du catalogue de pizzas et de leurs ingrédients
- Suivi des commandes et des livraisons
- Gestion des livreurs et de leurs véhicules
- Système de bonification (fidélité et retard)
- Statistiques diverses sur l'activité

## Structure du projet

```
ProjetBddEsiee/
├── conception/        # Modèles entité-association et relationnel
├── sql/               # Scripts SQL
│   ├── creation_tables.sql    # Création des tables
│   ├── insertion_donnees.sql  # Insertion des données de test
│   └── requetes.sql           # Requêtes demandées
├── src/               # Code source de l'application Java
│   └── main/
│       └── java/
│           └── fr/
│               └── esiee/
│                   └── rapizz/
│                       ├── dao/        # Classes d'accès aux données
│                       ├── model/      # Classes modèles
│                       ├── view/       # Interface utilisateur (SWING)
│                       ├── controller/ # Contrôleurs
│                       └── util/       # Classes utilitaires
└── docs/              # Documentation
```

## Modèle de données

Le système gère les entités suivantes :
- Clients
- Pizzas
- Ingrédients
- Commandes
- Livraisons
- Livreurs
- Véhicules

## Spécificités du système

- Pizzas disponibles en trois tailles : naine (-1/3 du prix), humaine (prix de base), ogresse (+1/3 du prix)
- Système prépayé : les clients doivent s'abonner et approvisionner leur compte
- Deux systèmes de bonification :
  - Pizza gratuite au bout de 10 pizzas achetées
  - Pizza gratuite si livrée en plus de 30 minutes

## Technologies utilisées

- Base de données relationnelle (MySQL/MariaDB)
- Java pour l'application
- JDBC pour la connexion à la base de données
- Swing pour l'interface graphique
- Pattern DAO pour l'accès aux données

## Installation et démarrage

### Méthode automatisée (recommandée)
1. Rendre le script d'installation exécutable (si ce n'est pas déjà fait) :
   ```bash
   chmod +x setup_database.sh
   ```
2. Exécuter le script d'installation :
   ```bash
   ./setup_database.sh
   ```
   - Le script vous demandera le mot de passe root MySQL
   - Il exécutera automatiquement les scripts de création et d'insertion

### Méthode manuelle
1. Créer une base de données MySQL/MariaDB
   - Cette étape est optionnelle car le script `creation_tables.sql` crée automatiquement la base de données nommée `rapizz`
   - Si vous souhaitez utiliser un autre nom, modifiez les scripts SQL en conséquence
2. Exécuter les scripts SQL dans l'ordre suivant :
   ```bash
   sudo /usr/local/mysql/bin/mysql -p < sql/creation_tables.sql
   sudo /usr/local/mysql/bin/mysql -p rapizz < sql/insertion_donnees.sql
   ```
3. Pour exécuter les requêtes :
   ```bash
   sudo /usr/local/mysql/bin/mysql -p rapizz < sql/requetes.sql
   ```

### Configuration et démarrage de l'application
1. Configurer les paramètres de connexion dans l'application Java
2. Compiler et exécuter l'application

## Requêtes SQL implémentées

1. Extraction des données pour le menu (carte des pizzas)
2. Génération de fiches de livraison
3. Statistiques diverses :
   - Véhicules jamais utilisés
   - Nombre de commandes par client
   - Moyenne des commandes
   - Clients ayant commandé plus que la moyenne
   - Identification du meilleur client
   - Identification du plus mauvais livreur
   - Identification des pizzas les plus/moins demandées
   - Identification de l'ingrédient favori

## Contributeurs

- [Votre nom]
- [Nom des autres membres de l'équipe]

---

Projet réalisé dans le cadre du cours de Base de Données à l'ESIEE Paris.