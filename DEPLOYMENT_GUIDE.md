# 🚀 Guide de déploiement Titan Backend sur Render

## 📋 Vue d'ensemble

Ce guide vous explique comment déployer l'application Titan Backend sur Render avec différentes options pour le stockage de fichiers (MinIO).

## 🏗️ Architecture de déploiement

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Render Web    │    │  Render PostgreSQL│    │  Stockage Files │
│  (Spring Boot)  │◄──►│    (Database)     │    │ (AWS S3/Other)  │
│                 │    │                   │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## 💰 Coûts estimés

### Plan Render Starter
- **Web Service**: $7/mois (512MB RAM, 0.5 CPU)
- **PostgreSQL**: Gratuit (1GB) puis $7/mois (10GB)
- **Total Render**: $7-14/mois

### Options stockage fichiers
1. **AWS S3**: ~$2-5/mois (selon usage)
2. **DigitalOcean Spaces**: $5/mois (250GB)
3. **Cloudinary**: Gratuit (25GB) puis $89/mois
4. **Render Persistent Disk**: $1/GB/mois

## 🔧 Étapes de déploiement

### 1. Préparation du repository

```bash
# Assurez-vous que tous les fichiers sont commitées
git add .
git commit -m "Préparation déploiement Render"
git push origin main
```

### 2. Création du service sur Render

1. **Connectez votre repository GitHub** à Render
2. **Créez un nouveau Web Service** avec ces paramètres :
   - **Build Command**: (laisser vide, utilise Dockerfile)
   - **Start Command**: (laisser vide, utilise Dockerfile)
   - **Plan**: Starter ($7/mois)
   - **Region**: Oregon (US) ou Frankfurt (EU)

### 3. Configuration de la base de données

1. **Créez une base PostgreSQL** sur Render :
   - Nom: `titan-postgres`
   - Plan: Starter (gratuit 1GB)
2. **Les variables DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD** seront automatiquement configurées

### 4. Configuration des variables d'environnement

Dans le dashboard Render, ajoutez ces variables :

#### Variables obligatoires
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport
```

#### Variables JWT (générer de nouvelles clés pour la production)
```
JWT_SECRET=[générer une nouvelle clé sécurisée]
JWT_EXPIRATION_MS=86400000
```

#### Variables Email
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[votre email]
MAIL_PASSWORD=[mot de passe d'application Gmail]
```

## 📦 Solutions pour MinIO (Stockage de fichiers)

### Option 1: AWS S3 (Recommandée) ⭐

**Avantages**: Fiable, scalable, CDN CloudFront intégré
**Coût**: ~$2-5/mois selon usage

#### Configuration AWS S3
1. **Créez un bucket S3** pour chaque type de fichier :
   - `titan-songs-prod`
   - `titan-images-prod`
   - `titan-videos-prod`
   - `titan-photos-prod`

2. **Créez un utilisateur IAM** avec ces permissions :
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::titan-*",
                "arn:aws:s3:::titan-*/*"
            ]
        }
    ]
}
```

3. **Variables d'environnement Render** :
```
MINIO_URL=https://s3.amazonaws.com
MINIO_ACCESS_KEY=[AWS Access Key ID]
MINIO_SECRET_KEY=[AWS Secret Access Key]
MINIO_BUCKET_SONGS=titan-songs-prod
MINIO_BUCKET_IMAGES=titan-images-prod
MINIO_BUCKET_VIDEOS=titan-videos-prod
MINIO_BUCKET_PHOTOS=titan-photos-prod
```

### Option 2: DigitalOcean Spaces 🌊

**Avantages**: Compatible S3, moins cher qu'AWS
**Coût**: $5/mois pour 250GB

#### Configuration DO Spaces
1. **Créez un Space** sur DigitalOcean
2. **Générez des clés API** Spaces
3. **Variables d'environnement** :
```
MINIO_URL=https://[region].digitaloceanspaces.com
MINIO_ACCESS_KEY=[Spaces Access Key]
MINIO_SECRET_KEY=[Spaces Secret Key]
```

### Option 3: Cloudinary (Images/Vidéos) 🖼️

**Avantages**: Optimisation automatique, transformations d'images
**Coût**: Gratuit jusqu'à 25GB

#### Configuration Cloudinary
1. **Créez un compte** Cloudinary
2. **Modifiez le code** pour utiliser l'API Cloudinary au lieu de MinIO
3. **Variables d'environnement** :
```
CLOUDINARY_CLOUD_NAME=[votre cloud name]
CLOUDINARY_API_KEY=[votre API key]
CLOUDINARY_API_SECRET=[votre API secret]
```

## 🔄 Déploiement automatique avec render.yaml

Le fichier `render.yaml` permet un déploiement automatique. Pour l'utiliser :

1. **Commitez le fichier** `render.yaml` dans votre repository
2. **Créez le service** via "Infrastructure as Code" sur Render
3. **Le déploiement se fera automatiquement** selon la configuration

## 🏥 Health Check et Monitoring

L'application expose des endpoints de santé :
- **Health Check**: `/actuator/health`
- **Info**: `/actuator/info`

Render vérifiera automatiquement la santé de l'application.

## 🔒 Sécurité en production

### Variables sensibles
- ✅ Utilisez les variables d'environnement Render (chiffrées)
- ✅ Générez de nouvelles clés JWT pour la production
- ✅ Utilisez des mots de passe d'application Gmail
- ✅ Configurez CORS pour votre domaine uniquement

### Base de données
- ✅ Connexions SSL automatiques avec Render PostgreSQL
- ✅ Pool de connexions optimisé
- ✅ Backup automatique avec Render

## 🚀 Mise en production

### 1. Test en staging
```bash
# Testez d'abord avec le profil de production en local
export SPRING_PROFILES_ACTIVE=prod
./gradlew bootRun
```

### 2. Déploiement
```bash
git push origin main
# Render déploiera automatiquement
```

### 3. Vérification
- ✅ Vérifiez `/actuator/health`
- ✅ Testez Swagger UI : `https://votre-app.onrender.com/swagger-ui/index.html`
- ✅ Testez l'upload de fichiers
- ✅ Vérifiez les logs Render

## 📊 Monitoring et logs

### Logs Render
- Accédez aux logs via le dashboard Render
- Configurez des alertes pour les erreurs

### Métriques
- Utilisez les métriques Render intégrées
- Considérez New Relic ou DataDog pour un monitoring avancé

## 🔧 Dépannage

### Problèmes courants
1. **Timeout de démarrage** : Augmentez `JAVA_OPTS` mémoire
2. **Connexion DB** : Vérifiez les variables DATABASE_*
3. **Upload fichiers** : Vérifiez la configuration MinIO/S3
4. **CORS** : Configurez les origines autorisées

### Commandes utiles
```bash
# Vérifier les logs
curl https://votre-app.onrender.com/actuator/health

# Tester l'API
curl https://votre-app.onrender.com/api-docs
```

## 💡 Optimisations

### Performance
- Utilisez un CDN (CloudFlare) devant Render
- Activez la compression gzip
- Optimisez les requêtes SQL avec des index

### Coûts
- Surveillez l'usage de la base de données
- Optimisez le stockage de fichiers selon l'usage
- Considérez le plan Standard Render si nécessaire

---

## 📞 Support

Pour toute question sur le déploiement :
1. Consultez la documentation Render
2. Vérifiez les logs de l'application
3. Testez les endpoints de santé

**L'application sera accessible à** : `https://titan-backend.onrender.com`
