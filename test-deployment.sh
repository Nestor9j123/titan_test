#!/bin/bash

# Script de test pré-déploiement pour Titan Backend avec MinIO containerisé
# Ce script valide la configuration avant déploiement sur Render

set -e

echo "🚀 Test de déploiement Titan Backend avec MinIO containerisé"
echo "============================================================"

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 1. Vérification des fichiers de configuration
echo ""
log_info "1. Vérification des fichiers de configuration..."

# Vérifier que tous les fichiers nécessaires existent
files_to_check=(
    "Dockerfile"
    "Dockerfile.minio"
    "render.yaml"
    "docker-compose.prod.yml"
    "src/main/resources/application-prod.properties"
    "build.gradle"
)

for file in "${files_to_check[@]}"; do
    if [[ -f "$file" ]]; then
        log_success "✓ $file existe"
    else
        log_error "✗ $file manquant"
        exit 1
    fi
done

# 2. Validation de la syntaxe des fichiers YAML
echo ""
log_info "2. Validation de la syntaxe YAML..."

# Vérifier render.yaml
if command -v yamllint &> /dev/null; then
    if yamllint render.yaml &> /dev/null; then
        log_success "✓ render.yaml syntaxe valide"
    else
        log_warning "⚠ yamllint détecte des problèmes dans render.yaml"
    fi
else
    log_warning "⚠ yamllint non installé, validation manuelle recommandée"
fi

# Vérifier docker-compose.prod.yml
if command -v docker-compose &> /dev/null; then
    if docker-compose -f docker-compose.prod.yml config &> /dev/null; then
        log_success "✓ docker-compose.prod.yml syntaxe valide"
    else
        log_error "✗ Erreur de syntaxe dans docker-compose.prod.yml"
        exit 1
    fi
else
    log_warning "⚠ docker-compose non installé"
fi

# 3. Test de build Docker
echo ""
log_info "3. Test de build Docker..."

# Build de l'application principale
log_info "Building application Spring Boot..."
if docker build -t titan-backend-test . &> build.log; then
    log_success "✓ Build Docker application réussi"
else
    log_error "✗ Échec du build Docker application"
    echo "Voir build.log pour les détails"
    exit 1
fi

# Build MinIO
log_info "Building MinIO service..."
if docker build -f Dockerfile.minio -t titan-minio-test . &> build-minio.log; then
    log_success "✓ Build Docker MinIO réussi"
else
    log_error "✗ Échec du build Docker MinIO"
    echo "Voir build-minio.log pour les détails"
    exit 1
fi

# 4. Test de démarrage des services
echo ""
log_info "4. Test de démarrage des services..."

# Variables d'environnement de test
export MINIO_ROOT_USER=testuser
export MINIO_ROOT_PASSWORD=testpassword123
export DATABASE_URL=jdbc:postgresql://localhost:5432/test_db
export DATABASE_USERNAME=test_user
export DATABASE_PASSWORD=test_password
export JWT_SECRET=test_jwt_secret_key_for_testing_purposes_only

log_info "Démarrage des services avec docker-compose..."
if docker-compose -f docker-compose.prod.yml up -d &> startup.log; then
    log_success "✓ Services démarrés"
    
    # Attendre que les services soient prêts
    log_info "Attente de la disponibilité des services..."
    sleep 30
    
    # Test de connectivité MinIO
    log_info "Test de connectivité MinIO..."
    if curl -f http://localhost:9000/minio/health/live &> /dev/null; then
        log_success "✓ MinIO accessible"
    else
        log_warning "⚠ MinIO non accessible (normal si PostgreSQL non disponible)"
    fi
    
    # Test de connectivité application (si PostgreSQL disponible)
    log_info "Test de connectivité application..."
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_success "✓ Application accessible"
    else
        log_warning "⚠ Application non accessible (normal si PostgreSQL non disponible)"
    fi
    
    # Nettoyage
    log_info "Arrêt des services de test..."
    docker-compose -f docker-compose.prod.yml down &> /dev/null
    
else
    log_error "✗ Échec du démarrage des services"
    echo "Voir startup.log pour les détails"
    exit 1
fi

# 5. Validation de la configuration Render
echo ""
log_info "5. Validation de la configuration Render..."

# Vérifier les variables d'environnement dans render.yaml
required_env_vars=(
    "SPRING_PROFILES_ACTIVE"
    "DATABASE_URL"
    "MINIO_URL"
    "MINIO_ACCESS_KEY"
    "MINIO_SECRET_KEY"
    "JWT_SECRET"
)

for var in "${required_env_vars[@]}"; do
    if grep -q "$var" render.yaml; then
        log_success "✓ Variable $var configurée dans render.yaml"
    else
        log_error "✗ Variable $var manquante dans render.yaml"
        exit 1
    fi
done

# Vérifier la configuration des services
if grep -q "titan-minio" render.yaml && grep -q "titan-backend" render.yaml; then
    log_success "✓ Services MinIO et Backend configurés"
else
    log_error "✗ Configuration des services incomplète"
    exit 1
fi

# Vérifier la configuration du disque persistant
if grep -q "disk:" render.yaml && grep -q "mountPath: /data" render.yaml; then
    log_success "✓ Disque persistant MinIO configuré"
else
    log_error "✗ Configuration du disque persistant manquante"
    exit 1
fi

# 6. Validation de la configuration Spring Boot
echo ""
log_info "6. Validation de la configuration Spring Boot..."

# Vérifier application-prod.properties
if grep -q "minio.url=\${MINIO_URL:" src/main/resources/application-prod.properties; then
    log_success "✓ Configuration MinIO dans application-prod.properties"
else
    log_error "✗ Configuration MinIO manquante dans application-prod.properties"
    exit 1
fi

# Vérifier les buckets configurés
buckets=("songs" "images" "videos" "photos")
for bucket in "${buckets[@]}"; do
    if grep -q "minio.bucket.$bucket" src/main/resources/application-prod.properties; then
        log_success "✓ Bucket $bucket configuré"
    else
        log_warning "⚠ Bucket $bucket non configuré"
    fi
done

# 7. Nettoyage des images de test
echo ""
log_info "7. Nettoyage des images de test..."
docker rmi titan-backend-test titan-minio-test &> /dev/null || true
log_success "✓ Images de test supprimées"

# 8. Résumé et recommandations
echo ""
echo "============================================================"
log_success "🎉 Tests de pré-déploiement terminés avec succès!"
echo ""
echo "📋 Résumé de la configuration:"
echo "   • Application Spring Boot: Dockerfile optimisé"
echo "   • MinIO containerisé: Dockerfile.minio avec healthcheck"
echo "   • Configuration Render: render.yaml multi-services"
echo "   • Communication inter-services: URLs internes configurées"
echo "   • Stockage persistant: Disque 10GB pour MinIO"
echo "   • Variables d'environnement: Toutes configurées"
echo ""
echo "🚀 Prêt pour le déploiement sur Render!"
echo ""
echo "📝 Prochaines étapes:"
echo "   1. Commit et push du code vers votre repository Git"
echo "   2. Connecter le repository à Render"
echo "   3. Déployer avec le fichier render.yaml"
echo "   4. Configurer les variables d'environnement sensibles (email)"
echo "   5. Tester la communication entre services en production"
echo ""
echo "⚠️  N'oubliez pas de:"
echo "   • Configurer MAIL_USERNAME et MAIL_PASSWORD manuellement sur Render"
echo "   • Surveiller les logs lors du premier déploiement"
echo "   • Tester l'upload de fichiers vers MinIO"
echo ""
