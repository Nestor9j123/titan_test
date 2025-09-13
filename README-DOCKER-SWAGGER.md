# Titan Backend - Configuration Docker & Swagger

## 🚀 Configuration Swagger/OpenAPI

### Accès à la documentation API
Une fois l'application lancée, la documentation Swagger est accessible via :
- **Interface Swagger UI** : http://localhost:8080/swagger-ui.html
- **Spécification OpenAPI JSON** : http://localhost:8080/api-docs

### Fonctionnalités Swagger configurées
- ✅ Configuration complète avec authentification JWT
- ✅ Documentation des 4 contrôleurs principaux (Delivery Orders, Companies, Persons, Consumer Food)
- ✅ Schémas de réponse et codes d'erreur documentés
- ✅ Paramètres et descriptions détaillées
- ✅ Serveurs de développement et production configurés

### Contrôleurs documentés
1. **Delivery Orders** (`/api/delivery-orders`) - Gestion des commandes de livraison
2. **Delivery Companies** (`/api/delivery-companies`) - Gestion des entreprises de livraison
3. **Delivery Persons** (`/api/delivery-persons`) - Gestion des livreurs
4. **Consumer Food** (`/api/consumer/restaurants`) - Consommation de nourriture

## 🐳 Configuration Docker

### Structure des fichiers Docker
```
├── Dockerfile                 # Image de production multi-stage
├── docker-compose.yml         # Environnement complet (app + DB + MinIO)
├── docker-compose.dev.yml     # Environnement de développement (DB + MinIO seulement)
├── .dockerignore             # Fichiers à ignorer lors du build
└── build-docker.sh           # Script de build automatisé
```

### 🔨 Build de l'image Docker

#### Option 1: Script automatisé (recommandé)
```bash
# Rendre le script exécutable
chmod +x build-docker.sh

# Build avec tag par défaut
./build-docker.sh

# Build avec tag personnalisé
./build-docker.sh titan-backend:v1.0.0

# Build et push vers un registry
REGISTRY=your-registry.com ./build-docker.sh titan-backend:v1.0.0
```

#### Option 2: Build manuel
```bash
# Build de l'image
docker build -t titan-backend:latest .

# Vérifier l'image
docker images | grep titan-backend
```

### 🚀 Lancement de l'application

#### Environnement complet (Production)
```bash
# Lancer tous les services (app + PostgreSQL + MinIO)
docker-compose up -d

# Voir les logs
docker-compose logs -f titan-backend

# Arrêter les services
docker-compose down
```

#### Environnement de développement
```bash
# Lancer seulement PostgreSQL et MinIO (pour développer l'app localement)
docker-compose -f docker-compose.dev.yml up -d

# L'application Spring Boot sera lancée depuis votre IDE
# avec les profils de développement
```

#### Lancement simple de l'application
```bash
# Lancer seulement l'application (nécessite PostgreSQL externe)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/titan_db \
  titan-backend:latest
```

### 🔧 Services inclus

#### PostgreSQL
- **Port** : 5433
- **Base de données** : titan_db
- **Utilisateur** : titan_user
- **Mot de passe** : titan_password

#### MinIO (Stockage de fichiers)
- **API** : http://localhost:9000
- **Console** : http://localhost:9001
- **Utilisateur** : admin
- **Mot de passe** : password123
- **Buckets automatiquement créés** :
  - titan-songs
  - titan-images
  - titan-videos
  - titan-photos

### 🏥 Health Check
L'application inclut un health check automatique :
```bash
# Vérifier le statut de l'application
curl http://localhost:8080/actuator/health
```

### 📊 Monitoring
- **Logs** : `docker-compose logs -f [service-name]`
- **Métriques** : http://localhost:8080/actuator
- **Health** : http://localhost:8080/actuator/health

## 🛠️ Développement

### Ajout d'annotations Swagger aux autres contrôleurs
```bash
# Rendre le script exécutable
chmod +x add-swagger-annotations.sh

# Ajouter les annotations Swagger de base aux contrôleurs restants
./add-swagger-annotations.sh
```

### Variables d'environnement importantes
```bash
# Profil Spring
SPRING_PROFILES_ACTIVE=dev|prod

# Base de données
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5433/titan_db
SPRING_DATASOURCE_USERNAME=titan_user
SPRING_DATASOURCE_PASSWORD=titan_password

# MinIO
MINIO_URL=http://minio:9000
MINIO_ACCESS_KEY=admin
MINIO_SECRET_KEY=password123

# JVM
JAVA_OPTS="-Xmx512m -Xms256m"
```

### Workflow de développement recommandé
1. **Développement local** :
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   # Lancer l'app depuis l'IDE avec profil 'dev'
   ```

2. **Test en environnement conteneurisé** :
   ```bash
   ./build-docker.sh
   docker-compose up -d
   ```

3. **Déploiement** :
   ```bash
   REGISTRY=your-registry.com ./build-docker.sh titan-backend:v1.0.0
   ```

## 📚 Modules de l'application

Le projet Titan Backend contient 5 modules principaux :

1. **Coursier** - Service de livraison
2. **Events** - Gestion d'événements
3. **Food** - Restauration et commandes
4. **Immo** - Immobilier
5. **Shared** - Services partagés (sécurité, MinIO, etc.)

## 🔐 Sécurité

- Authentification JWT configurée
- Utilisateur non-root dans le conteneur Docker
- Variables d'environnement pour les secrets
- CORS configuré pour le développement

## 📞 Support

Pour toute question ou problème :
1. Vérifiez les logs : `docker-compose logs -f`
2. Consultez la documentation Swagger : http://localhost:8080/swagger-ui.html
3. Vérifiez le health check : http://localhost:8080/actuator/health
