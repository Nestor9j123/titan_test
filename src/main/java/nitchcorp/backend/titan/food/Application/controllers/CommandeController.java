package nitchcorp.backend.titan.food.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nitchcorp.backend.titan.food.Application.dtos.request.CommandeRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.CommandeResponse;
import nitchcorp.backend.titan.food.Application.services.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin("*")
@Tag(name = "Commandes", description = "API de gestion des commandes restaurant")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    @Operation(summary = "Créer une commande", description = "Création d'une nouvelle commande")
    @ApiResponse(responseCode = "200", description = "Commande créée avec succès",
            content = @Content(schema = @Schema(implementation = CommandeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createCommande(@RequestBody CommandeRequest request) {
        try {
            CommandeResponse response = commandeService.createCommande(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
        }
    }



    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une commande", description = "Récupère une commande par son tracking ID")
    public ResponseEntity<?> getCommande(@PathVariable UUID trackingId) {
        try {
            CommandeResponse response = commandeService.getCommandeByTrackingId(trackingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "COMMANDE_NOT_FOUND", "message", e.getMessage()));
        }
    }


    @GetMapping
    @Operation(summary = "Lister toutes les commandes", description = "Récupère toutes les commandes")
    public ResponseEntity<List<CommandeResponse>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }


    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour une commande", description = "Met à jour une commande existante")
    public ResponseEntity<?> updateCommande(@PathVariable UUID trackingId, @RequestBody CommandeRequest request) {
        try {
            CommandeResponse response = commandeService.updateCommande(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
        }
    }



    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer une commande", description = "Supprime une commande par son tracking ID")
    public ResponseEntity<?> deleteCommande(@PathVariable UUID trackingId) {
        try {
            commandeService.deleteCommande(trackingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
        }
    }
}
