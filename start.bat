@echo off
chcp 65001 >nul
cls

echo 🍕 RaPizz - Application de Gestion de Pizzeria
echo ==============================================
echo.

REM Vérification de Java
echo 🔍 Vérification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java n'est pas installé ou n'est pas dans le PATH
    echo    Veuillez installer Java 11 ou supérieur depuis :
    echo    https://www.oracle.com/java/technologies/downloads/
    echo    ou https://adoptium.net/
    pause
    exit /b 1
)
echo ✅ Java OK
java -version 2>&1 | findstr "version"

REM Vérification de Maven
echo.
echo 🔍 Vérification de Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven n'est pas installé ou n'est pas dans le PATH
    echo    Veuillez installer Maven depuis :
    echo    https://maven.apache.org/download.cgi
    echo    Et l'ajouter au PATH système
    pause
    exit /b 1
)
echo ✅ Maven OK
mvn -version 2>&1 | findstr "Apache Maven"

REM Vérification de MySQL
echo.
echo 🔍 Vérification de MySQL...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  MySQL client non trouvé dans le PATH
    echo    L'application peut fonctionner si MySQL Server est démarré
) else (
    echo ✅ MySQL client OK
)

echo.
echo 🚀 Compilation du projet...
call mvn clean compile

if %errorlevel% neq 0 (
    echo ❌ Erreur lors de la compilation
    echo    Vérifiez les erreurs ci-dessus
    pause
    exit /b 1
)

echo ✅ Compilation réussie
echo.
echo 🎯 Lancement de l'application...
echo    (Fermez la fenêtre de l'application pour arrêter)
echo.

REM Lancement de l'application
call mvn exec:java -Dexec.mainClass="fr.esiee.rapizz.RapizzApplication"

echo.
echo 👋 Application fermée
pause 