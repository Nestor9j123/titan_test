# Dockerfile multi-stage pour optimiser la taille de l'image
FROM gradle:8.5-jdk21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Copier le code source
COPY src src

# Construire l'application
RUN ./gradlew build -x test --no-daemon

# Stage de production avec une image JRE plus légère
FROM eclipse-temurin:21-jre-alpine

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Changer le propriétaire du fichier
RUN chown appuser:appuser app.jar

# Basculer vers l'utilisateur non-root
USER appuser

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement pour la JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Point d'entrée de l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Health check simple (peut être configuré avec un outil externe comme Docker Compose)
# HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#     CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
