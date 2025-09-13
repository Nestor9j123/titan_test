#!/bin/bash

# Script de build Docker pour Titan Backend
# Usage: ./build-docker.sh [tag]

set -e

# Configuration par défaut
DEFAULT_TAG="titan-backend:latest"
TAG=${1:-$DEFAULT_TAG}
REGISTRY=${REGISTRY:-""}

echo "🚀 Construction de l'image Docker Titan Backend..."
echo "📦 Tag: $TAG"

# Vérifier que Docker est installé et en cours d'exécution
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "❌ Docker n'est pas en cours d'exécution"
    exit 1
fi

# Nettoyer les builds précédents si nécessaire
echo "🧹 Nettoyage des artefacts de build précédents..."
./gradlew clean

# Construire l'image Docker
echo "🔨 Construction de l'image Docker..."
docker build -t $TAG .

# Vérifier que l'image a été créée
if docker images | grep -q "${TAG%:*}"; then
    echo "✅ Image Docker construite avec succès: $TAG"
    
    # Afficher les informations de l'image
    echo "📊 Informations de l'image:"
    docker images | grep "${TAG%:*}"
    
    # Si un registry est défini, pousser l'image
    if [ ! -z "$REGISTRY" ]; then
        FULL_TAG="$REGISTRY/$TAG"
        echo "📤 Push vers le registry: $FULL_TAG"
        docker tag $TAG $FULL_TAG
        docker push $FULL_TAG
        echo "✅ Image poussée vers le registry avec succès"
    fi
    
    echo ""
    echo "🎉 Build terminé avec succès!"
    echo "💡 Pour lancer l'application:"
    echo "   docker run -p 8080:8080 $TAG"
    echo ""
    echo "💡 Pour lancer avec docker-compose:"
    echo "   docker-compose up -d"
    echo ""
    echo "💡 Pour le développement (sans build de l'app):"
    echo "   docker-compose -f docker-compose.dev.yml up -d"
    
else
    echo "❌ Échec de la construction de l'image Docker"
    exit 1
fi
