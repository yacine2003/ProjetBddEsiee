# 🍕 RaPizz - Application de Gestion de Pizzeria

**Projet de Base de Données - ESIEE Paris**

Application Java complète de gestion de pizzeria utilisant Maven, MySQL et Swing.

## 🚀 Démarrage Rapide (5 minutes)

### 1. Prérequis
- **Java 11+** : `java -version`
- **Maven 3.6+** : `mvn -version`
- **MySQL Server** démarré

### 2. Configuration Base de Données
```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter les scripts
source sql/creation_tables.sql
source sql/insertion_donnees.sql
exit
```

### 3. Lancement
```bash
# Script automatique (Recommandé)
./start.sh          # Linux/macOS
start.bat           # Windows

# Ou manuellement
mvn clean compile
mvn exec:java -Dexec.mainClass="fr.esiee.rapizz.RapizzApplication"
```

L'application s'ouvre avec une **page d'accueil élégante** présentant le titre RaPizz. Cliquez sur "Accéder à l'Application" pour accéder aux fonctionnalités principales.

## ✨ Fonctionnalités

L'application comprend 5 modules :

- **👥 Clients** - Gestion CRUD, soldes, historique
- **🍕 Pizzas** - Catalogue avec ingrédients et tailles
- **📋 Commandes** - Création, modification, suivi des statuts
- **🚚 Livraisons** - Attribution livreur/véhicule, gestion retards
- **📊 Statistiques** - Tableau de bord temps réel

## 🔧 Configuration

Si nécessaire, modifier `src/main/java/fr/esiee/rapizz/util/DatabaseConfig.java` :
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/rapizz";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "VotreMotDePasse";
```

## 🐛 Dépannage

**Erreur de connexion MySQL :**
- Vérifier que MySQL est démarré
- Vérifier les paramètres dans `DatabaseConfig.java`

**Erreur "Class not found" :**
```bash
mvn clean compile
```

**Base de données manquante :**
```bash
mysql -u root -p
source sql/creation_tables.sql
source sql/insertion_donnees.sql
```

## 📁 Structure du Projet

```
src/main/java/fr/esiee/rapizz/
├── RapizzApplication.java          # Point d'entrée
├── model/                          # Modèles (Client, Pizza, etc.)
├── dao/                           # Accès données
├── view/                          # Interface Swing
│   ├── WelcomeFrame.java          # Page d'accueil
│   └── MainFrame.java             # Application principale
└── util/                          # Configuration DB
```

---

**Développé dans le cadre du projet de Base de Données - ESIEE Paris**