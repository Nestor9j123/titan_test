package nitchcorp.backend.titan.food.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nitchcorp.backend.titan.food.Application.dtos.request.NotePlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.NotePlatResponse;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatResponse;
import nitchcorp.backend.titan.food.Domain.enums.CategoriePlat;
import nitchcorp.backend.titan.food.Application.services.ServiceImpl.PlatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plats")
@Tag(name = "PLAT", description = "Gestion des plats")
@CrossOrigin("*")
public class PlatController {

    private static final Logger log = LoggerFactory.getLogger(PlatController.class);
    private final PlatServiceImpl platService;

    public  PlatController(PlatServiceImpl platService) {
        this.platService = platService;
    }

    @PostMapping("/{trackingId}")
    public NotePlatResponse noterPlat(@PathVariable UUID trackingId, @RequestBody NotePlatRequest request) {
        return platService.noterPlat(trackingId, request);
    }


    @Operation(summary = "Récupérer tous les plats", description = "Récupère la liste de tous les plats")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des plats récupérée avec succès",
            content = @Content(schema = @Schema(implementation = PlatResponse.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Aucun plat trouvé"
    )
    @GetMapping
    public ResponseEntity<?> getAllPlats(@RequestParam(value = "categorie", required = false) CategoriePlat categorie) {
        try {
            List<PlatResponse> plats;

            if (categorie != null) {
                plats = platService.getPlatsByCategorie(categorie);
            } else {
                plats = platService.getAllPlats();
            }

            if (plats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun plat trouvé");
            }

            return ResponseEntity.ok(plats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }


    @ApiResponse(
            responseCode = "200",
            description = "Liste des plats recommandées récupérée avec succès",
            content = @Content(schema = @Schema(implementation = PlatResponse.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Aucun plat recommandé trouvé"
    )
    @GetMapping("/recommendations")
    public ResponseEntity<?> recommendPlatsForUser(
            @RequestParam("userTrackingId") UUID userTrackingId,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        try {
            if (limit <= 0) {
                return ResponseEntity.badRequest().body("Le paramètre 'limit' doit être supérieur à 0");
            }
            List<PlatResponse> plats = platService.recommendPlatsForUser(userTrackingId, limit);
            if (plats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune recommandation trouvée pour l'utilisateur " + userTrackingId);
            }
            return ResponseEntity.ok(plats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }


    @Operation(summary = "Récupérer un plat", description = "Récupère un plat par son tracking ID")
    @ApiResponse(
            responseCode = "200",
            description = "Plat récupéré avec succès",
            content = @Content(schema = @Schema(implementation = PlatResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Plat non trouvé"
    )
    @GetMapping("/{trackingId}")
    public ResponseEntity<PlatResponse> getPlatByTrackingId(@PathVariable UUID trackingId) {
        try{
            PlatResponse response = platService.getPlat(trackingId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Operation(summary = "Mettre à jour un plat", description = "Mettre à jour les informations d'un plat par son tracking ID")
    @ApiResponse(
            responseCode = "200",
            description = "Plat mis à jour avec succès",
            content = @Content(schema = @Schema(implementation = PlatResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Plat non trouvé"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur"
    )
    @PutMapping("/{trackingId}")
    public ResponseEntity<PlatResponse> updatePlat(@PathVariable UUID trackingId, @RequestBody PlatRequest platRequest) {
        try{
            PlatResponse response = platService.updatePlat(trackingId, platRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Operation(summary = "Supprimer un plat ", description = "Supprimer un plat par son tracking ID")
    @ApiResponse(
            responseCode = "204",
            description = "Plat supprimé avec succès",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Plat non trouvé"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Erreur serveur"
    )
    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> deletePlat(@PathVariable UUID trackingId) {
        try{
            platService.deletePlat(trackingId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(value = "/create-with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlatResponse> createPlatWithPhotos(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categorie") String categorie,
            @RequestParam("prix") double prix,
            @RequestParam("tempsDePreparation") String tempsDePreparation,
            @RequestParam("tracking_restaurantId") UUID tracking_restaurantId,
            @RequestParam("rate") double rate,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos
    ) {
        try{
            PlatRequest platRequest =  new PlatRequest(
                    name,
                    description,
                    categorie,
                    prix,
                    tempsDePreparation,
                    tracking_restaurantId,
                    rate,
                    photos != null ? photos.stream().map(MultipartFile::getOriginalFilename).toList() : null

            );
            PlatResponse response = platService.createPlatWithPhotos(platRequest, photos);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
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
