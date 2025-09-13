package nitchcorp.backend.titan.coursier.Application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import nitchcorp.backend.titan.coursier.Application.dtos.requests.DeliveryCompanyRequest;
import nitchcorp.backend.titan.coursier.Application.dtos.responses.DeliveryCompanyResponse;
import nitchcorp.backend.titan.coursier.Application.services.DeliveryCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-companies")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Delivery Companies", description = "API de gestion des compagnies de livraison")
public class DeliveryCompanyController {

    private final DeliveryCompanyService companyService;

    @Operation(summary = "Créer une nouvelle compagnie de livraison")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compagnie créée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryCompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DeliveryCompanyResponse> createCompany(
            @RequestBody DeliveryCompanyRequest request) {
        return new ResponseEntity<>(companyService.createCompany(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Récupérer toutes les compagnies")
    @GetMapping
    public ResponseEntity<List<DeliveryCompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Récupérer toutes les compagnies actives")
    @GetMapping("/active")
    public ResponseEntity<List<DeliveryCompanyResponse>> getAllCompaniesActive() {
        return ResponseEntity.ok(companyService.getAllCompaniesActive());
    }

    @Operation(summary = "Récupérer une compagnie par son trackingId")
    @GetMapping("/{trackingId}")
    public ResponseEntity<DeliveryCompanyResponse> getCompanyByTrackingId(
            @Parameter(description = "UUID de la compagnie") @PathVariable UUID trackingId) {
        return ResponseEntity.ok(companyService.getCompanyByTrackingId(trackingId));
    }

    @Operation(summary = "Mettre à jour une compagnie")
    @PutMapping("/{trackingId}")
    public ResponseEntity<DeliveryCompanyResponse> updateCompany(
            @Parameter(description = "UUID de la compagnie") @PathVariable UUID trackingId,
            @RequestBody DeliveryCompanyRequest request) {
        return ResponseEntity.ok(companyService.updateCompany(trackingId, request));
    }

    @Operation(summary = "Désactiver une compagnie")
    @PatchMapping("/{trackingId}/deactivate")
    public ResponseEntity<Void> deactivateCompany(
            @Parameter(description = "UUID de la compagnie") @PathVariable UUID trackingId) {
        companyService.deactivateCompany(trackingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activer une compagnie")
    @PatchMapping("/{trackingId}/activate")
    public ResponseEntity<Void> activateCompany(
            @Parameter(description = "UUID de la compagnie") @PathVariable UUID trackingId) {
        companyService.activateCompany(trackingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprimer une compagnie")
    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "UUID de la compagnie") @PathVariable UUID trackingId) {
        companyService.deleteCompany(trackingId);
        return ResponseEntity.noContent().build();
    }
}
