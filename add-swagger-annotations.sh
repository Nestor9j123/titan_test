#!/bin/bash

# Script pour ajouter automatiquement les annotations Swagger de base aux contrôleurs
# qui n'en ont pas encore

set -e

echo "🔧 Ajout des annotations Swagger aux contrôleurs..."

# Liste des contrôleurs à traiter (sans les 4 déjà configurés)
controllers=(
    "src/main/java/nitchcorp/backend/titan/events/Application/controller/EventsController.java"
    "src/main/java/nitchcorp/backend/titan/events/Application/controller/PurchasedTicketController.java"
    "src/main/java/nitchcorp/backend/titan/events/Application/controller/QrCodeTestController.java"
    "src/main/java/nitchcorp/backend/titan/events/Application/controller/TicketTemplateController.java"
    "src/main/java/nitchcorp/backend/titan/events/Application/controller/VoteController.java"
    "src/main/java/nitchcorp/backend/titan/food/Application/controllers/CommandeController.java"
    "src/main/java/nitchcorp/backend/titan/food/Application/controllers/OptionPersonaliserController.java"
    "src/main/java/nitchcorp/backend/titan/food/Application/controllers/PlatCommandeController.java"
    "src/main/java/nitchcorp/backend/titan/food/Application/controllers/PlatController.java"
    "src/main/java/nitchcorp/backend/titan/food/Application/controllers/RestaurantController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/AgentController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/CustomerController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/LeaseContratController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/NotificationController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/OwnerAgentAssignmentController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/OwnerController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/PaiementController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/PropertyController.java"
    "src/main/java/nitchcorp/backend/titan/immo/application/controller/VisitController.java"
    "src/main/java/nitchcorp/backend/titan/shared/minio/controller/MinioController.java"
    "src/main/java/nitchcorp/backend/titan/shared/securite/user/controllers/UserController.java"
)

# Fonction pour ajouter les imports Swagger
add_swagger_imports() {
    local file=$1
    
    # Vérifier si les imports Swagger existent déjà
    if grep -q "io.swagger.v3.oas.annotations" "$file"; then
        echo "⏭️  Imports Swagger déjà présents dans $file"
        return 0
    fi
    
    echo "📝 Ajout des imports Swagger à $file"
    
    # Créer un fichier temporaire avec les nouveaux imports
    temp_file=$(mktemp)
    
    # Copier le package et ajouter les imports Swagger après les imports existants
    awk '
    /^import/ && !swagger_added {
        print "import io.swagger.v3.oas.annotations.Operation;"
        print "import io.swagger.v3.oas.annotations.Parameter;"
        print "import io.swagger.v3.oas.annotations.media.Content;"
        print "import io.swagger.v3.oas.annotations.media.Schema;"
        print "import io.swagger.v3.oas.annotations.responses.ApiResponse;"
        print "import io.swagger.v3.oas.annotations.responses.ApiResponses;"
        print "import io.swagger.v3.oas.annotations.tags.Tag;"
        swagger_added = 1
    }
    {print}
    ' "$file" > "$temp_file"
    
    mv "$temp_file" "$file"
}

# Fonction pour ajouter l'annotation @Tag à la classe
add_tag_annotation() {
    local file=$1
    local class_name=$(basename "$file" .java)
    
    # Vérifier si @Tag existe déjà
    if grep -q "@Tag" "$file"; then
        echo "⏭️  Annotation @Tag déjà présente dans $file"
        return 0
    fi
    
    echo "🏷️  Ajout de l'annotation @Tag à $class_name"
    
    # Ajouter @Tag avant @RestController
    sed -i 's/@RestController/@Tag(name = "'"$class_name"'", description = "API pour '"$class_name"'")\n@RestController/' "$file"
}

# Traiter chaque contrôleur
for controller in "${controllers[@]}"; do
    if [ -f "$controller" ]; then
        echo "🔄 Traitement de $controller"
        add_swagger_imports "$controller"
        add_tag_annotation "$controller"
        echo "✅ $controller traité avec succès"
    else
        echo "⚠️  Fichier non trouvé: $controller"
    fi
done

echo ""
echo "🎉 Configuration Swagger terminée!"
echo "📚 Les contrôleurs ont maintenant les annotations Swagger de base."
echo "💡 Pour une documentation plus détaillée, ajoutez manuellement les annotations @Operation et @ApiResponse aux méthodes importantes."
