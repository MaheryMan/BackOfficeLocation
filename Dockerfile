# ==================================
# Stage 1: Build avec Maven
# ==================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier d'abord les JARs de lib
COPY BO-location/lib ./lib

# Copier le pom.xml et télécharger les dépendances
COPY BO-location/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copier le code source
COPY BO-location/src ./src

# Build de l'application
RUN mvn clean package -DskipTests

# ==================================
# Stage 2: Runtime avec Tomcat
# ==================================
FROM tomcat:10.1-jdk17

# Supprimer les applications par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le WAR depuis le stage de build
COPY --from=build /app/target/backoffice-location.war /usr/local/tomcat/webapps/ROOT.war

# Variables d'environnement par défaut (seront écrasées par Render)
ENV DATABASE_URL=jdbc:postgresql://localhost:5432/location_s5
ENV DATABASE_USER=location
ENV DATABASE_PASSWORD=password

# Exposer le port 8080
EXPOSE 8080

# Démarrer Tomcat
CMD ["catalina.sh", "run"]
