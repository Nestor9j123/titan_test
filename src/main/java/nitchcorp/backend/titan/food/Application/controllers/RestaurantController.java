package nitchcorp.backend.titan.food.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantLocationRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.RestaurantRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantLocationResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.RestaurantResponse;
import nitchcorp.backend.titan.food.Application.services.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "RESTAURANT" , description = "Gestion d'un restaurant")
@CrossOrigin("*")
public class RestaurantController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantController.class);
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "Récupérer un restaurant", description = "Récupère un restaurant par son tracking ID avec toutes ses images")
    @ApiResponse(
            responseCode = "200",
            description = "Restaurant récupéré avec succès",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Restaurant non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    @GetMapping("/{trackingId}")
    public ResponseEntity<?> getRestaurantByTrackingId(@PathVariable UUID trackingId) {
        try {
            RestaurantResponse response = restaurantService.getRestaurantByTrackingId(trackingId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Operation(summary = "Lister tous les événements", description = "Récupère tous les événements avec leurs images")
    @ApiResponse(
            responseCode = "200",
            description = "Événements récupérés avec succès",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Aucun événement trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        try {
                List<RestaurantResponse> response = restaurantService.getAllRestaurants();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
    }


    @Operation(summary = "Supprimer un restaurant ", description = "Supprimer un restaurant et toutes ses images associées")
    @ApiResponse(
            responseCode = "200",
            description = "Restaurant supprimé avec succès",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(responseCode = "204", description = "Restaurant supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Restaurant non trouvé")
    @DeleteMapping("/{trackingId}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID trackingId) {
        try {
            restaurantService.deleteRestaurant(trackingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Mettre à jour un restaurant", description = "Met à jour les informations d'un restaurant existant")
    @ApiResponse(
            responseCode = "200",
            description = "Restaurant mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Restaurant non trouvé")
    @PutMapping("/{trackingId}")
    public ResponseEntity<?> updateRestaurant(
            @PathVariable UUID trackingId,
            @RequestBody RestaurantRequest request) {
        try {
            RestaurantResponse response = restaurantService.updateRestaurant(trackingId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Operation(summary = "Ajoute la latitude et la longitude à un restaurant", description = "Met à jour la latitude et longitude d'un restaurant existant")
    @ApiResponse(
            responseCode = "200",
            description = "Restaurant mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Restaurant non trouvé")
    @PutMapping("location/{trackingId}")
    public ResponseEntity<?> updateRestaurantLocation(
            @PathVariable("trackingId") UUID trackingId,
            @RequestBody RestaurantLocationRequest request) {
        try {
            RestaurantLocationResponse response = restaurantService.updateRestaurantLocation(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }


    @Operation(summary = "Créer un restaurant avec photos", description = "Créer un nouveau restaurant avec plusieurs photos")
    @ApiResponse(
            responseCode = "201",
            description = "Restaurant créé avec succès",
            content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "413", description = "Fichier trop lourd")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    @PostMapping(value = "/create-with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantResponse> createRestaurantWithPhotos(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("phone") String phone,
            @RequestParam("adresse") String adresse,
            @RequestParam("kitchenType") String kitchenType,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("openingHour") String openingHourStr,
            @RequestParam("haveDelevery") boolean haveDelevery,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime openingHour = LocalDateTime.parse(openingHourStr, formatter);

            RestaurantRequest request = new RestaurantRequest(
                    name,
                    description,
                    phone,
                    adresse,
                    kitchenType,
                    latitude,
                    longitude,
                    openingHour,
                    haveDelevery,
                    photos != null ? photos.stream().map(MultipartFile::getOriginalFilename).toList() : null
            );
            RestaurantResponse restaurant = restaurantService.createRestaurantWithPhotos(request, photos);
            return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
        } catch (NumberFormatException e) {
            log.error("Format numérique invalide: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            log.error("Requête invalide: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur création restaurant avec photos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
