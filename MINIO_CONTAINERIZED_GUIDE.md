# Guide de Déploiement MinIO Containerisé sur Render

## 📋 Vue d'ensemble

Ce guide détaille la solution de déploiement de MinIO containerisé sur Render pour le projet Titan Backend. Cette approche permet d'avoir un stockage de fichiers S3-compatible entièrement contrôlé et intégré à votre infrastructure Render.

## 🏗️ Architecture de la Solution

```
┌─────────────────────────────────────────────────────────────┐
│                        Render Cloud                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────┐                │
│  │   titan-backend │    │   titan-minio   │                │
│  │  (Spring Boot)  │◄──►│   (MinIO S3)    │                │
│  │                 │    │                 │                │
│  │  Port: 8080     │    │  Ports: 9000,   │                │
│  │                 │    │         9001    │                │
│  └─────────────────┘    └─────────────────┘                │
│           │                       │                        │
│           ▼                       ▼                        │
│  ┌─────────────────┐    ┌─────────────────┐                │
│  │  PostgreSQL DB  │    │ Persistent Disk │                │
│  │   (Managed)     │    │    (10GB+)      │                │
│  └─────────────────┘    └─────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

## 📁 Structure des Fichiers

### 1. Dockerfile.minio
```dockerfile
# Dockerfile pour MinIO optimisé pour Render
FROM minio/minio:latest

# Créer le répertoire de données
RUN mkdir -p /data

# Exposer les ports MinIO
EXPOSE 9000 9001

# Variables d'environnement par défaut
ENV MINIO_ROOT_USER=admin
ENV MINIO_ROOT_PASSWORD=password123

# Point d'entrée avec configuration pour Render
ENTRYPOINT ["minio", "server", "/data", "--console-address", ":9001"]

# Health check pour Render
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:9000/minio/health/live || exit 1
```

### 2. Configuration render.yaml
```yaml
services:
  # Service MinIO pour stockage de fichiers
  - type: web
    name: titan-minio
    env: docker
    dockerfilePath: ./Dockerfile.minio
    plan: starter
    region: oregon
    branch: main
    
    # Variables d'environnement MinIO
    envVars:
      - key: MINIO_ROOT_USER
        value: "admin"
      - key: MINIO_ROOT_PASSWORD
        generateValue: true # Render génère un mot de passe sécurisé
    
    # Disk persistant pour les données MinIO
    disk:
      name: minio-data
      mountPath: /data
      sizeGB: 10 # Commencer avec 10GB, extensible
    
    # Health check MinIO
    healthCheckPath: /minio/health/live

  # Application Spring Boot principale
  - type: web
    name: titan-backend
    env: docker
    dockerfilePath: ./Dockerfile
    plan: starter
    region: oregon
    branch: main
    
    # Variables d'environnement
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: MINIO_URL
        value: "http://titan-minio:9000" # URL interne Render
      - key: MINIO_ACCESS_KEY
        fromService:
          type: web
          name: titan-minio
          envVarKey: MINIO_ROOT_USER
      - key: MINIO_SECRET_KEY
        fromService:
          type: web
          name: titan-minio  
          envVarKey: MINIO_ROOT_PASSWORD
      # ... autres variables
    
    # Health check
    healthCheckPath: /actuator/health

# Base de données PostgreSQL
databases:
  - name: titan-postgres
    databaseName: titan_prod
    user: titan_user
    plan: starter
```

### 3. Configuration Spring Boot (application-prod.properties)
```properties
# MinIO Configuration Production (Service Render interne)
# Render utilise des URLs internes pour la communication inter-services
minio.url=${MINIO_URL:http://titan-minio:9000}
minio.access-key=${MINIO_ACCESS_KEY:admin}
minio.secret-key=${MINIO_SECRET_KEY:password123}
minio.bucket.songs=${MINIO_BUCKET_SONGS:titan-songs-prod}
minio.bucket.images=${MINIO_BUCKET_IMAGES:titan-images-prod}
minio.bucket.videos=${MINIO_BUCKET_VIDEOS:titan-videos-prod}
minio.bucket.photos=${MINIO_BUCKET_PHOTOS:titan-photos-prod}
```

## 🚀 Processus de Déploiement

### Étape 1: Préparation du Code
```bash
# 1. Vérifier que tous les fichiers sont en place
ls -la Dockerfile Dockerfile.minio render.yaml docker-compose.prod.yml

# 2. Tester la configuration localement
./test-deployment.sh

# 3. Commit et push vers votre repository Git
git add .
git commit -m "feat: Add MinIO containerized deployment for Render"
git push origin main
```

### Étape 2: Configuration sur Render
1. **Connecter le Repository**
   - Aller sur [Render Dashboard](https://dashboard.render.com)
   - Cliquer sur "New" → "Blueprint"
   - Connecter votre repository Git
   - Sélectionner le fichier `render.yaml`

2. **Déploiement Automatique**
   - Render détecte automatiquement les services définis
   - Les services sont déployés dans l'ordre de dépendance
   - MinIO est déployé en premier avec son disque persistant
   - L'application Spring Boot suit avec les variables d'environnement liées

### Étape 3: Configuration Post-Déploiement
1. **Variables d'Environnement Sensibles**
   ```
   MAIL_USERNAME=votre-email@gmail.com
   MAIL_PASSWORD=votre-mot-de-passe-app
   ```

2. **Vérification des Services**
   - MinIO Console: `https://titan-minio-[random].onrender.com` (port 9001)
   - Application: `https://titan-backend-[random].onrender.com`
   - Health Check: `https://titan-backend-[random].onrender.com/actuator/health`

## 🔧 Communication Inter-Services

### URLs Internes Render
- **Application → MinIO**: `http://titan-minio:9000`
- **Application → PostgreSQL**: Géré automatiquement par Render
- **Externe → Application**: `https://titan-backend-[random].onrender.com`
- **Externe → MinIO Console**: `https://titan-minio-[random].onrender.com`

### Configuration Automatique des Buckets
Le docker-compose.prod.yml inclut un service `minio-setup` qui:
- Crée automatiquement les buckets nécessaires
- Configure les politiques d'accès public
- S'exécute une seule fois au démarrage

## 💾 Gestion du Stockage

### Disque Persistant MinIO
- **Taille initiale**: 10GB (configurable dans render.yaml)
- **Montage**: `/data` dans le container MinIO
- **Extensibilité**: Peut être étendu via le dashboard Render
- **Sauvegarde**: Gérée automatiquement par Render

### Buckets Configurés
1. `titan-songs-prod` - Fichiers audio
2. `titan-images-prod` - Images et photos
3. `titan-videos-prod` - Fichiers vidéo
4. `titan-photos-prod` - Photos utilisateur

## 🔒 Sécurité

### Authentification MinIO
- **Utilisateur**: Défini par `MINIO_ROOT_USER`
- **Mot de passe**: Généré automatiquement par Render
- **Accès**: Limité aux services internes Render

### Réseau
- Communication inter-services via réseau privé Render
- MinIO non accessible directement depuis l'extérieur (sauf console)
- Application expose uniquement les endpoints nécessaires

## 📊 Monitoring et Logs

### Health Checks
- **MinIO**: `/minio/health/live` (port 9000)
- **Application**: `/actuator/health` (port 8080)
- **Fréquence**: Toutes les 30 secondes

### Logs
```bash
# Voir les logs MinIO
render logs --service titan-minio

# Voir les logs Application
render logs --service titan-backend

# Logs en temps réel
render logs --service titan-backend --follow
```

## 🛠️ Maintenance et Troubleshooting

### Problèmes Courants

1. **MinIO non accessible**
   ```bash
   # Vérifier les logs MinIO
   render logs --service titan-minio --tail 100
   
   # Vérifier l'espace disque
   # Via Render Dashboard → titan-minio → Metrics
   ```

2. **Erreur de connexion Application → MinIO**
   ```bash
   # Vérifier la configuration des variables d'environnement
   render env --service titan-backend
   
   # Vérifier que MINIO_URL pointe vers http://titan-minio:9000
   ```

3. **Buckets non créés**
   ```bash
   # Redémarrer le service pour relancer minio-setup
   render restart --service titan-minio
   ```

### Commandes Utiles
```bash
# Redémarrer MinIO
render restart --service titan-minio

# Redémarrer l'application
render restart --service titan-backend

# Voir l'utilisation des ressources
render metrics --service titan-minio

# Étendre le disque (via dashboard uniquement)
# Render Dashboard → titan-minio → Settings → Disk
```

## 💰 Coûts Estimés (USD/mois)

| Service | Plan | Coût |
|---------|------|------|
| titan-backend | Starter | $7 |
| titan-minio | Starter | $7 |
| titan-postgres | Starter | Gratuit (1GB) |
| Persistent Disk | 10GB | ~$1 |
| **Total** | | **~$15/mois** |

## 🔄 Alternatives et Migration

### Migration vers AWS S3
Si vous souhaitez migrer vers AWS S3 plus tard:
1. Modifier `application-prod.properties`:
   ```properties
   minio.url=https://s3.amazonaws.com
   minio.access-key=${AWS_ACCESS_KEY_ID}
   minio.secret-key=${AWS_SECRET_ACCESS_KEY}
   ```
2. Supprimer le service MinIO du `render.yaml`
3. Migrer les données existantes

### Autres Options
- **DigitalOcean Spaces**: Compatible S3, moins cher
- **Cloudinary**: Optimisé pour les images avec CDN
- **Backblaze B2**: Très économique pour le stockage

## 📝 Checklist de Déploiement

- [ ] Dockerfile.minio créé et testé
- [ ] render.yaml configuré avec les deux services
- [ ] application-prod.properties mis à jour
- [ ] docker-compose.prod.yml fonctionnel
- [ ] Script de test exécuté avec succès
- [ ] Repository Git à jour
- [ ] Services déployés sur Render
- [ ] Variables d'environnement email configurées
- [ ] Health checks fonctionnels
- [ ] Upload de fichiers testé
- [ ] Buckets MinIO créés et accessibles

## 🎯 Conclusion

Cette solution MinIO containerisée offre:
- ✅ **Contrôle total** sur le stockage de fichiers
- ✅ **Coût prévisible** et raisonnable
- ✅ **Intégration native** avec Render
- ✅ **Scalabilité** via l'extension du disque
- ✅ **Sécurité** avec réseau privé
- ✅ **Simplicité** de déploiement et maintenance

La solution est prête pour la production et peut facilement évoluer selon vos besoins futurs.
