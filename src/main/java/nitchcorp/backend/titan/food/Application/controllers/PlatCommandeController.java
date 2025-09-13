package nitchcorp.backend.titan.food.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.food.Application.dtos.request.PlatCommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.PlatCommandeResponse;
import nitchcorp.backend.titan.food.Application.services.PlatCommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/platcommandes")
@Tag(name = "Plats Commandes", description = "API de gestion des plats dans les commandes")
public class PlatCommandeController {

    private final PlatCommandeService platCommandeService;

    public PlatCommandeController(PlatCommandeService platCommandeService) {
        this.platCommandeService = platCommandeService;
    }

    @PostMapping("/create")
    @Operation(summary = "Créer un plat commandé", description = "Création d'un nouveau plat commandé")
    @ApiResponse(
            responseCode = "200",
            description = "Plat commandé créé avec succès",
            content = @Content(schema = @Schema(implementation = PlatCommandeResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createPlatCommande(@RequestBody PlatCommandeRequest request ) {
        try {
            PlatCommandeResponse response = platCommandeService.createPlatCommande(request);


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
        }
    }


    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un plat commandé", description = "Récupère un plat commandé par son tracking ID")
    public ResponseEntity<?> getPlatCommande(@PathVariable UUID trackingId) {
        try {
            PlatCommandeResponse response = platCommandeService.getPlatCommande(trackingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "PLAT_COMMANDE_NOT_FOUND", "message", "Plat commandé non trouvé avec l'ID: " + trackingId));
        }
    }


    @GetMapping("/allplatcommandes")
    @Operation(summary = "Lister tous les plats commandés", description = "Récupère tous les plats commandés")
    public ResponseEntity<List<PlatCommandeResponse>> getAllPlatsCommande() {
        return ResponseEntity.ok(platCommandeService.getAllPlatCommandes());
    }


    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un plat commandé", description = "Met à jour un plat commandé existant")
    public ResponseEntity<?> updatePlatCommande(
            @PathVariable UUID trackingId,
            @RequestBody PlatCommandeRequest request) {
        try {
            PlatCommandeResponse response = platCommandeService.updatePlatCommande(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
        }
    }


    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un plat commandé", description = "Supprime un plat commandé par son tracking ID")
    public ResponseEntity<?> deletePlatCommande(@PathVariable UUID trackingId) {
        try {
            platCommandeService.deletePlatCommande(trackingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "DELETE_FAILED", "message", "Impossible de supprimer le plat commandé: " + e.getMessage()));
       }
    }


    @PostMapping("/optionPersonaliser/addOption/")
    @Operation(summary = "Ajouter une option à un plat commandé", description = "Ajoute une option de personnalisation à un plat commandé")
    public ResponseEntity<?> addOptionToPlatCommande(@RequestParam UUID trackingId, @RequestParam UUID optionId){
        try {
            PlatCommandeResponse response = platCommandeService.addOptionToPlatCommande(trackingId, optionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "imposible d'ajouter l'option", "message", "Impossible de supprimer l'option du platComande: " + e.getMessage()));
        }
    };


    @DeleteMapping("/optionPersonaliser/deleteImage/")
    @Operation(summary = "Supprimer une option d'un plat commandé", description = "Supprime une option de personnalisation d'un plat commandé")
   public ResponseEntity<?> deleteOptionToPlatCommande(@RequestParam UUID trackingId ,@RequestParam UUID optionId){
       try {
           platCommandeService.deleteOptionToPlatCommande(trackingId,optionId);
           return ResponseEntity.noContent().build();
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("error", "PLAT_COMMANDE_NOT_FOUND", "message", "Plat commandé non trouvé avec l'ID: " + trackingId));
       }
   };


}
