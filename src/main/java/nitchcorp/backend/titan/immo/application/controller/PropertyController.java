package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.PropertyRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PropertyResponse;
import nitchcorp.backend.titan.immo.application.service.PropertyService;
import nitchcorp.backend.titan.immo.domain.enums.PropertyType;
import nitchcorp.backend.titan.immo.domain.enums.PropertyStatus;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import nitchcorp.backend.titan.shared.minio.enums.FileType;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Properties", description = "API pour la gestion des propriétés immobilières")
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    private final PropertyService propertyService;
    private final MinioService minioService;


    @GetMapping("/all")
    @Operation(summary = "Récupérer toutes les propriétés", description = "Récupère la liste de toutes les propriétés")
    @ApiResponse(responseCode = "200", description = "Liste des propriétés récupérée avec succès")
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        log.info("Fetching all properties at 08:50 PM GMT, 06/08/2025");
        try {
            List<PropertyResponse> properties = propertyService.getAllProperties();
            log.info("Retrieved {} properties", properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error fetching all properties: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une propriété par ID de suivi", description = "Récupère une propriété spécifique par son ID de suivi")
    @ApiResponse(responseCode = "200", description = "Propriété trouvée")
    @ApiResponse(responseCode = "404", description = "Propriété non trouvée")
    public ResponseEntity<PropertyResponse> getPropertyByTrackingId(
            @Parameter(description = "ID de suivi de la propriété", required = true)
            @PathVariable UUID trackingId) {
        log.info("Fetching property with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            PropertyResponse property = propertyService.getPropertyByTrackingId(trackingId);
            log.info("Property found with trackingId: {}", trackingId);
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Property not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/search")
    @Operation(summary = "Rechercher des propriétés", description = "Recherche des propriétés selon différents critères")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès")
    @ApiResponse(responseCode = "400", description = "Paramètres de recherche invalides")
    public ResponseEntity<List<PropertyResponse>> searchProperties(
            @Parameter(description = "Ville de recherche")
            @RequestParam(required = false) String city,
            @Parameter(description = "Pays de recherche")
            @RequestParam(required = false) String country,
            @Parameter(description = "Prix minimum")
            @RequestParam(required = false) Double priceMin,
            @Parameter(description = "Prix maximum")
            @RequestParam(required = false) Double priceMax,
            @Parameter(description = "Type de propriété")
            @RequestParam(required = false) String type,
            @Parameter(description = "Date de disponibilité")
            @RequestParam(required = false) String availabilityDate) {
        log.info("Searching properties with city: {}, country: {}, price range: [{}, {}], type: {}, availabilityDate: {}",
                city, country, priceMin, priceMax, type, availabilityDate);

        try {
            if (priceMin != null && priceMax != null && priceMin > priceMax) {
                log.error("Invalid price range: priceMin ({}) > priceMax ({})", priceMin, priceMax);
                return ResponseEntity.badRequest().build();
            }

            List<PropertyResponse> properties = propertyService.searchProperties(city, country, priceMin, priceMax, type, availabilityDate);
            log.info("Found {} properties", properties.size());
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            log.error("Invalid search parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error searching properties: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    @GetMapping("/search/address")
    public ResponseEntity<Page<PropertyResponse>> getPropertyByAddress(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam String ortDir,
            @RequestParam String adresse,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        Page<PropertyResponse> authorResponseDtos = propertyService.getPropertyByAdresse( page, size, sortBy, ortDir, adresse);
        return new ResponseEntity<>(authorResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/search/city")
    public ResponseEntity<Page<PropertyResponse>> getPropertyByCity(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam String ortDir,
            @RequestParam String city,
            @RequestParam(defaultValue = "id") String sortBy
            ){
        Page<PropertyResponse> authorResponseDtos = propertyService.getPropertyByCity( page, size, sortBy, ortDir, city);
        return new ResponseEntity<>(authorResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/search/country")
    public ResponseEntity<Page<PropertyResponse>> getPropertyByCountry(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam String ortDir,
            @RequestParam String country,
            @RequestParam(defaultValue = "id") String sortBy
            ){
        Page<PropertyResponse> authorResponseDtos = propertyService.getPropertyByCountry( page, size, sortBy, ortDir, country);
        return new ResponseEntity<>(authorResponseDtos, HttpStatus.OK);
    }

    @PutMapping("/update/{trackingId}")
    @Operation(summary = "Mettre à jour une propriété", description = "Met à jour les informations d'une propriété existante")
    @ApiResponse(responseCode = "200", description = "Propriété mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Propriété non trouvée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<PropertyResponse> updateProperty(
            @Parameter(description = "ID de suivi de la propriété", required = true)
            @PathVariable UUID trackingId,
            @Parameter(description = "Nouvelles informations de la propriété", required = true)
            @Valid @RequestBody PropertyRequest request) {
        log.info("Updating property with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            PropertyResponse updatedProperty = propertyService.updateProperty(trackingId, request);
            log.info("Property updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating property: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating property with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    @Operation(summary = "Supprimer une propriété", description = "Supprime une propriété du système")
    @ApiResponse(responseCode = "200", description = "Propriété supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Propriété non trouvée")
    public ResponseEntity<String> deleteProperty(
            @Parameter(description = "ID de suivi de la propriété", required = true)
            @PathVariable UUID trackingId) {
        log.info("Deleting property with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            propertyService.deleteProperty(trackingId);
            log.info("Property deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Propriété supprimée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting property with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Propriété non trouvée");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deletePropertyRestful(@PathVariable UUID trackingId) {
        return deleteProperty(trackingId);
    }

    // Endpoints manquants selon le cahier des charges

    @GetMapping("/search/nearby")
    @Operation(summary = "Rechercher des propriétés à proximité", description = "Recherche des propriétés dans un rayon donné")
    @ApiResponse(responseCode = "200", description = "Propriétés à proximité trouvées")
    public ResponseEntity<List<PropertyResponse>> searchPropertiesNearby(
            @Parameter(description = "Latitude", required = true)
            @RequestParam Double latitude,
            @Parameter(description = "Longitude", required = true)
            @RequestParam Double longitude,
            @Parameter(description = "Rayon de recherche en km")
            @RequestParam(defaultValue = "10") Double radiusKm,
            @Parameter(description = "Pays de recherche")
            @RequestParam(required = false) String country) {
        log.info("Searching properties near lat: {}, lng: {}, radius: {}km, country: {}", 
                latitude, longitude, radiusKm, country);
        try {
            List<PropertyResponse> properties = propertyService.searchPropertiesNearby(latitude, longitude, radiusKm, country);
            log.info("Found {} properties nearby", properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error searching nearby properties: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByCountry(@PathVariable String country) {
        log.info("Fetching properties for country: {}", country);
        try {
            List<PropertyResponse> properties = propertyService.getPropertiesByCountry(country);
            log.info("Retrieved {} properties for country: {}", properties.size(), country);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error fetching properties by country: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByAgent(@PathVariable UUID agentId) {
        log.info("Fetching properties managed by agent: {}", agentId);
        try {
            List<PropertyResponse> properties = propertyService.getPropertiesByAgent(agentId);
            log.info("Retrieved {} properties for agent: {}", properties.size(), agentId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error fetching properties by agent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByOwner(@PathVariable UUID ownerId) {
        log.info("Fetching properties owned by: {}", ownerId);
        try {
            List<PropertyResponse> properties = propertyService.getPropertiesByOwner(ownerId);
            log.info("Retrieved {} properties for owner: {}", properties.size(), ownerId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error fetching properties by owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{trackingId}/status")
    public ResponseEntity<PropertyResponse> updatePropertyStatus(
            @PathVariable UUID trackingId, 
            @RequestParam String status) {
        log.info("Updating property status: {} to {}", trackingId, status);
        try {
            PropertyResponse property = propertyService.updatePropertyStatus(trackingId, status);
            log.info("Property status updated successfully");
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating property status: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/create-with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer une propriété avec photos", description = "Crée une nouvelle propriété avec des photos en une seule requête")
    @ApiResponse(responseCode = "201", description = "Propriété créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<PropertyResponse> createPropertyWithPhotos(
            @Parameter(description = "ID du propriétaire", required = true, example = "9a5b786e-a444-4995-86a8-df6805bbe359")
            @RequestParam("ownerId") String ownerId,
            @Parameter(description = "Adresse de la propriété", required = true, example = "123 Rue de la Paix")
            @RequestParam("address") String address,
            @Parameter(description = "Prix de location", required = true, example = "1000")
            @RequestParam("rentPrice") String rentPrice,
            @Parameter(description = "Ville", example = "Paris")
            @RequestParam(value = "city", required = false) String city,
            @Parameter(description = "Pays", example = "France")
            @RequestParam(value = "country", required = false) String country,
            @Parameter(description = "Type de propriété", example = "APPARTMENT")
            @RequestParam(value = "propertyType", required = false) String propertyType,
            @Parameter(description = "Description", example = "Bel appartement lumineux")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Nombre de chambres", example = "2")
            @RequestParam(value = "bedrooms", required = false) String bedrooms,
            @Parameter(description = "Nombre de salles de bain", example = "1")
            @RequestParam(value = "bathrooms", required = false) String bathrooms,
            @Parameter(description = "Surface en m²", example = "75.5")
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        log.info("Creating property with photos for owner: {}, address: {}", ownerId, address);

        try {

            PropertyRequest propertyRequest = new PropertyRequest(
                    UUID.fromString(ownerId),           // ownerId
                    null,                               // agentId
                    PropertyType.APPARTMENT,            // type
                    address,                            // address
                    city != null ? city : "Unknown",   // city
                    country != null ? country : "Unknown", // country
                    null,                               // latitude
                    null,                               // longitude
                    description != null ? description : "", // description
                    new java.math.BigDecimal(rentPrice), // rentPrice
                    null,                               // additionalFees
                    null,                               // deposit
                    bedrooms != null ? Integer.parseInt(bedrooms) : null, // numberOfRooms
                    area != null ? Double.parseDouble(area) : null, // area
                    null,                               // amenities
                    null,                               // photos - will be handled separately
                    PropertyStatus.AVAILABLE,          // status
                    null                                // availabilityDate
            );

            PropertyResponse property = propertyService.createPropertyWithPhotos(propertyRequest, photos);
            log.info("Property with photos created successfully with ID: {}", property.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(property);

        } catch (NumberFormatException e) {
            log.error("Invalid number format in request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating property with photos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating property with photos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/{trackingId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter des photos à une propriété", description = "Ajoute des photos à une propriété existante et retourne les URLs des photos")
    @ApiResponse(responseCode = "200", description = "Photos ajoutées avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur lors de l'ajout des photos")
    @ApiResponse(responseCode = "404", description = "Propriété non trouvée")
    public ResponseEntity<PropertyResponse> addPhotosToProperty(
            @Parameter(description = "ID de tracking de la propriété", required = true)
            @PathVariable UUID trackingId,
            @RequestParam("photos") List<MultipartFile> photos) {
        
        log.info("Adding {} photos to property: {}", photos.size(), trackingId);
        
        try {
            if (photos == null || photos.isEmpty()) {
                log.error("No photos provided for property: {}", trackingId);
                return ResponseEntity.badRequest().build();
            }
            
            PropertyResponse property = propertyService.addPhotosToProperty(trackingId, photos);
            log.info("Photos added successfully to property: {}. Photo URLs returned in response.", trackingId);
            return ResponseEntity.ok(property);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for adding photos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error adding photos to property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{trackingId}/photos")
    public ResponseEntity<PropertyResponse> removePhotosFromProperty(@PathVariable UUID trackingId, @RequestBody List<Long> imageIds) {
        PropertyResponse updatedProperty = propertyService.removePhotosFromProperty(trackingId, imageIds);
        return ResponseEntity.ok(updatedProperty);
    }

    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties(
            @RequestParam(required = false) String country) {
        log.info("Fetching available properties for country: {}", country);
        try {
            List<PropertyResponse> properties = propertyService.getAvailableProperties(country);
            log.info("Retrieved {} available properties", properties.size());
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            log.error("Error fetching available properties: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{trackingId}/photos/{photoIndex}")
    @Operation(summary = "Afficher une photo de propriété", description = "Retourne le contenu binaire d'une photo pour affichage direct dans Postman")
    @ApiResponse(responseCode = "200", description = "Image retournée avec succès")
    @ApiResponse(responseCode = "404", description = "Propriété ou photo non trouvée")
    public ResponseEntity<byte[]> displayPropertyPhoto(
            @Parameter(description = "ID de tracking de la propriété", required = true)
            @PathVariable UUID trackingId,
            @Parameter(description = "Index de la photo (0, 1, 2...)", required = true, example = "0")
            @PathVariable int photoIndex) {
        
        log.info("Displaying photo {} for property: {}", photoIndex, trackingId);
        
        try {
            // Récupérer la propriété
            PropertyResponse property = propertyService.getPropertyByTrackingId(trackingId);
            
            if (property.photos() == null || property.photos().isEmpty()) {
                log.warn("No photos found for property: {}", trackingId);
                return ResponseEntity.notFound().build();
            }
            
            if (photoIndex < 0 || photoIndex >= property.photos().size()) {
                log.warn("Photo index {} out of bounds for property {} (has {} photos)", 
                        photoIndex, trackingId, property.photos().size());
                return ResponseEntity.notFound().build();
            }
            
            // Extraire le nom du fichier depuis l'URL
            String photoUrl = property.photos().get(photoIndex);
            String fileName = minioService.extractFileNameFromUrl(photoUrl);
            
            if (fileName == null) {
                log.error("Could not extract filename from URL: {}", photoUrl);
                return ResponseEntity.notFound().build();
            }
            
            // Récupérer le contenu du fichier depuis MinIO
            byte[] imageBytes = minioService.getFileContentAsBytes(fileName, FileType.IMAGE);
            
            // Récupérer les métadonnées pour le Content-Type
            var metadata = minioService.getFileMetadata(fileName, FileType.IMAGE);
            String contentType = metadata.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/jpeg"; // Fallback
            }
            
            log.info("Successfully retrieved photo {} for property {} (size: {} bytes)", 
                    photoIndex, trackingId, imageBytes.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(imageBytes.length)
                    .body(imageBytes);
                    
        } catch (Exception e) {
            log.error("Error displaying photo {} for property {}: {}", photoIndex, trackingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{trackingId}/photos")
    @Operation(summary = "Récupérer toutes les photos d'une propriété", description = "Retourne la liste des URLs de toutes les photos d'une propriété")
    @ApiResponse(responseCode = "200", description = "Photos récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Propriété non trouvée")
    public ResponseEntity<List<String>> getAllPropertyPhotos(
            @Parameter(description = "ID de tracking de la propriété", required = true)
            @PathVariable UUID trackingId) {
        
        log.info("Getting all photos for property: {}", trackingId);
        
        try {
            PropertyResponse property = propertyService.getPropertyByTrackingId(trackingId);
            
            if (property.photos() == null || property.photos().isEmpty()) {
                log.info("No photos found for property: {}", trackingId);
                return ResponseEntity.ok(List.of());
            }
            
            log.info("Found {} photos for property: {}", property.photos().size(), trackingId);
            return ResponseEntity.ok(property.photos());
            
        } catch (Exception e) {
            log.error("Error getting photos for property {}: {}", trackingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}