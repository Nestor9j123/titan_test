package nitchcorp.backend.titan.food.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.food.Application.dtos.request.OptionPersonaliserRequest;
import nitchcorp.backend.titan.food.Application.dtos.response.OptionPersonaliserResponse;
import nitchcorp.backend.titan.food.Application.services.OptionPersonaliserService;
import nitchcorp.backend.titan.shared.minio.service.MinioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/optionPersonaliser")
@Tag(name = "Options Personnalisation", description = "API de gestion des options de personnalisation des plats")
public class OptionPersonaliserController {

    private final OptionPersonaliserService optionService;
    private final MinioService minioService;

    public OptionPersonaliserController(OptionPersonaliserService optionService, MinioService minioService) {
        this.optionService = optionService;
        this.minioService = minioService;
    }

    @PostMapping("/create")
    @Operation(summary = "Créer une option de personnalisation", description = "Création d'une nouvelle option de personnalisation")
    @ApiResponse(
            responseCode = "200",
            description = "Option créée avec succès",
            content = @Content(schema = @Schema(implementation = OptionPersonaliserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> createOption(@RequestBody OptionPersonaliserRequest request) {
        try {
            OptionPersonaliserResponse response = optionService.createOption(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "CREATION_FAILED",
                            "message", "Erreur lors de la création de l'option: " + e.getMessage())
                    );
        }
    }


    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une option", description = "Récupère une option de personnalisation par son tracking ID")
    public ResponseEntity<?> getOption(@PathVariable UUID trackingId) {
        try {
            OptionPersonaliserResponse response = optionService.getOption(trackingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "OPTION_NOT_FOUND", "message", "Option non trouvée avec l'ID: " + trackingId));
        }
    }


    @GetMapping("/optionpersonalisers")
    @Operation(summary = "Lister toutes les options", description = "Récupère toutes les options de personnalisation")
    public ResponseEntity<List<OptionPersonaliserResponse>> getAllOptions() {
        List<OptionPersonaliserResponse> options = optionService.getAllOptions();
        return ResponseEntity.ok(options);
    }


    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour une option", description = "Met à jour les informations d'une option de personnalisation existante")
    public ResponseEntity<?> updateOption(
            @PathVariable UUID trackingId, OptionPersonaliserRequest request) {
        try {
            OptionPersonaliserResponse response = optionService.updateOption(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "UPDATE_FAILED",
                            "message", "Erreur lors de la mise à jour de l'option: " + e.getMessage())
                    );
        }
    }


    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer une option", description = "Supprime une option de personnalisation par son tracking ID")
    public ResponseEntity<?> deleteOption(@PathVariable UUID trackingId) {
        try {
            optionService.deleteOption(trackingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "DELETE_FAILED", "message", "Impossible de supprimer l'option: " + e.getMessage()));
        }
    }

}