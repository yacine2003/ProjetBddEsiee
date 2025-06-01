#!/bin/bash

# Script de démarrage pour RaPizz
# Compatible macOS et Linux

echo "🍕 RaPizz - Application de Gestion de Pizzeria"
echo "=============================================="
echo ""

# Vérification de Java
echo "🔍 Vérification de Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé ou n'est pas dans le PATH"
    echo "   Veuillez installer Java 11 ou supérieur"
    echo "   macOS: brew install openjdk@11"
    echo "   Ubuntu: sudo apt install openjdk-11-jdk"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java 11 ou supérieur requis (version détectée: $JAVA_VERSION)"
    exit 1
fi
echo "✅ Java OK (version: $(java -version 2>&1 | head -n 1))"

# Vérification de Maven
echo "🔍 Vérification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé ou n'est pas dans le PATH"
    echo "   Veuillez installer Maven 3.6 ou supérieur"
    echo "   macOS: brew install maven"
    echo "   Ubuntu: sudo apt install maven"
    exit 1
fi
echo "✅ Maven OK ($(mvn -version | head -n 1))"

# Vérification de MySQL
echo "🔍 Vérification de MySQL..."
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL client non trouvé dans le PATH"
    echo "   L'application peut fonctionner si MySQL Server est démarré"
else
    echo "✅ MySQL client OK"
fi

echo ""
echo "🚀 Compilation du projet..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    echo "   Vérifiez les erreurs ci-dessus"
    exit 1
fi

echo "✅ Compilation réussie"
echo ""
echo "🎯 Lancement de l'application..."
echo "   (Fermez la fenêtre de l'application pour arrêter)"
echo ""

# Lancement de l'application
mvn exec:java -Dexec.mainClass="fr.esiee.rapizz.RapizzApplication"

echo ""
echo "👋 Application fermée" 