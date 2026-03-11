#!/bin/bash

# Configuration
PROJECT_ROOT="$PWD"
WEBAPP_NAME="BO-location"
TOMCAT_HOME="/Users/andriamalalatojo/Documents/Dependances/apache-tomcat-10.1.30"
TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"

# Création des répertoires temporaires
BUILD_DIR="$PROJECT_ROOT/build"
CLASSES_DIR="$BUILD_DIR/WEB-INF/classes"

echo "=== Nettoyage des anciens builds ==="
if [ -d "$BUILD_DIR" ]; then
    rm -rf "$BUILD_DIR"
fi
mkdir -p "$CLASSES_DIR"

echo "=== Compilation des fichiers Java ==="
# Création du CLASSPATH
CLASSPATH=""
for jar in "$PROJECT_ROOT/lib"/*.jar; do
    if [ -f "$jar" ]; then
        if [ -z "$CLASSPATH" ]; then
            CLASSPATH="$jar"
        else
            CLASSPATH="$CLASSPATH:$jar"
        fi
    fi
done

# Compilation directe sans fichier temporaire
find "$PROJECT_ROOT/src/main/java" -name "*.java" -exec javac -parameters -cp "$CLASSPATH" -d "$CLASSES_DIR" {} +
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation"
    exit 1
fi

echo "=== Copie des ressources Web ==="
# Copie du contenu webapp avec la nouvelle structure
if [ -d "$PROJECT_ROOT/src/main/webapp" ]; then
    cp -r "$PROJECT_ROOT/src/main/webapp/"* "$BUILD_DIR/"
fi

# Copie des bibliothèques
mkdir -p "$BUILD_DIR/WEB-INF/lib"
if [ -d "$PROJECT_ROOT/lib" ]; then
    cp "$PROJECT_ROOT/lib"/*.jar "$BUILD_DIR/WEB-INF/lib/" 2>/dev/null
fi

# Suppression de servlet-api.jar du WAR (fourni par Tomcat)
if [ -f "$BUILD_DIR/WEB-INF/lib/servlet-api.jar" ]; then
    rm "$BUILD_DIR/WEB-INF/lib/servlet-api.jar"
fi

echo "=== Création du WAR ==="
cd "$BUILD_DIR"
jar -cvf "$WEBAPP_NAME.war" * > /dev/null

echo "=== Déploiement vers Tomcat ==="
# Suppression de l'ancienne version si elle existe
if [ -f "$TOMCAT_WEBAPPS/$WEBAPP_NAME.war" ]; then
    rm "$TOMCAT_WEBAPPS/$WEBAPP_NAME.war"
fi
if [ -d "$TOMCAT_WEBAPPS/$WEBAPP_NAME" ]; then
    rm -rf "$TOMCAT_WEBAPPS/$WEBAPP_NAME"
fi

# Copie du nouveau WAR
cp "$WEBAPP_NAME.war" "$TOMCAT_WEBAPPS/"

echo "=== Déploiement terminé ==="
echo ""
echo "Accédez à votre application sur: http://localhost:8888/$WEBAPP_NAME"
echo ""
cd "$PROJECT_ROOT"