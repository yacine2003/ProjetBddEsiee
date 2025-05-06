#!/bin/bash
# Script d'installation de la base de données RaPizz
# Ce script exécute automatiquement tous les scripts SQL nécessaires

# Définition des variables
MYSQL_PATH="/usr/local/mysql/bin/mysql"
SQL_DIR="./sql"

# Message d'accueil
echo "🍕 Installation de la base de données RaPizz 🍕"
echo "==============================================="
echo ""

# Vérification que le chemin MySQL existe
if [ ! -f "$MYSQL_PATH" ]; then
    echo "❌ Erreur: MySQL n'est pas trouvé à l'emplacement $MYSQL_PATH"
    echo "Veuillez modifier le script pour indiquer le bon chemin vers MySQL."
    exit 1
fi

# Demande du mot de passe root
echo "Veuillez entrer le mot de passe root MySQL:"
read -s MYSQL_PASSWORD
echo ""

# Fonction pour exécuter un script SQL
execute_sql_script() {
    local script=$1
    echo "⏳ Exécution de $script..."
    
    echo "SET foreign_key_checks = 0;" > /tmp/temp_script.sql
    cat "$SQL_DIR/$script" >> /tmp/temp_script.sql
    echo "SET foreign_key_checks = 1;" >> /tmp/temp_script.sql
    
    $MYSQL_PATH -u root -p"$MYSQL_PASSWORD" < /tmp/temp_script.sql
    
    if [ $? -eq 0 ]; then
        echo "✅ $script exécuté avec succès."
    else
        echo "❌ Erreur lors de l'exécution de $script."
        rm /tmp/temp_script.sql
        exit 1
    fi
    
    rm /tmp/temp_script.sql
}

# Exécution des scripts dans l'ordre
echo "🔄 Création des tables..."
execute_sql_script "creation_tables.sql"

echo "🔄 Insertion des données..."
execute_sql_script "insertion_donnees.sql"

echo ""
echo "📊 Base de données RaPizz installée avec succès! 📊"
echo ""
echo "Pour exécuter les requêtes, utilisez la commande:"
echo "sudo $MYSQL_PATH -p rapizz < $SQL_DIR/requetes.sql"
echo ""
echo "Ou connectez-vous à MySQL et exécutez les requêtes interactivement:"
echo "sudo $MYSQL_PATH -p"
echo "USE rapizz;"
echo "SOURCE $SQL_DIR/requetes.sql;"
echo ""

exit 0 