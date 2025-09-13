package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.VisitRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.VisitResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.VisitNotFoundException;
import nitchcorp.backend.titan.immo.application.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Visits", description = "API de gestion des visites immobilières")
@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class VisitController {

    private final VisitService visitService;

    @PostMapping("/create")
    @Operation(summary = "Créer une nouvelle visite", description = "Crée une nouvelle visite immobilière")
    @ApiResponse(responseCode = "201", description = "Visite créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<VisitResponse> createVisit(
            @Parameter(description = "Informations de la visite à créer", required = true)
            @Valid @RequestBody VisitRequest request) {
        log.info("Received request: propertyId={}, agentId={}, customerId={}",
                request.propertyId(), request.agentId(), request.customerId());

        if (request.propertyId() == null || request.agentId() == null || request.customerId() == null) {
            log.error("Missing required IDs in the request. Full request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        log.info("Creating visit for property: {}", request.propertyId());

        try {
            VisitResponse response = visitService.createVisit(request);
            log.info("Visit created successfully with ID: {} at 08:50 PM GMT, 06/08/2025", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (VisitNotFoundException e) {
            log.error("Visit creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating visit: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating visit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Récupérer toutes les visites", description = "Récupère la liste de toutes les visites")
    @ApiResponse(responseCode = "200", description = "Liste des visites récupérée avec succès")
    public ResponseEntity<List<VisitResponse>> getAllVisits() {
        log.info("Fetching all visits at 08:50 PM GMT, 06/08/2025");
        try {
            List<VisitResponse> visits = visitService.getAllVisits();
            log.info("Retrieved {} visits", visits.size());
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            log.error("Error fetching all visits: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une visite par ID de suivi", description = "Récupère une visite spécifique par son ID de suivi")
    @ApiResponse(responseCode = "200", description = "Visite trouvée")
    @ApiResponse(responseCode = "404", description = "Visite non trouvée")
    public ResponseEntity<VisitResponse> getVisitByTrackingId(
            @Parameter(description = "ID de suivi de la visite", required = true)
            @PathVariable UUID trackingId) {
        log.info("Fetching visit with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            VisitResponse visit = visitService.getVisitByTrackingId(trackingId);
            log.info("Visit found with trackingId: {}", trackingId);
            return ResponseEntity.ok(visit);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Visit not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    @Operation(summary = "Mettre à jour une visite", description = "Met à jour les informations d'une visite existante")
    @ApiResponse(responseCode = "200", description = "Visite mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Visite non trouvée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<VisitResponse> updateVisit(
            @Parameter(description = "ID de suivi de la visite", required = true)
            @PathVariable UUID trackingId,
            @Parameter(description = "Nouvelles informations de la visite", required = true)
            @Valid @RequestBody VisitRequest request) {
        log.info("Updating visit with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            VisitResponse updatedVisit = visitService.updateVisit(trackingId, request);
            log.info("Visit updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedVisit);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating visit: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating visit with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    @Operation(summary = "Supprimer une visite", description = "Supprime une visite du système")
    @ApiResponse(responseCode = "200", description = "Visite supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Visite non trouvée")
    public ResponseEntity<String> deleteVisit(
            @Parameter(description = "ID de suivi de la visite", required = true)
            @PathVariable UUID trackingId) {
        log.info("Deleting visit with trackingId: {} at 08:50 PM GMT, 06/08/2025", trackingId);
        try {
            visitService.deleteVisit(trackingId);
            log.info("Visit deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Visite supprimée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        } catch (Exception e) {
            log.error("Error deleting visit with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Visite non trouvée");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deleteVisitRestful(@PathVariable UUID trackingId) {
        return deleteVisit(trackingId);
    }

    // Endpoints manquants selon le cahier des charges

    @GetMapping("/upcoming/agent/{agentId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByAgent(@PathVariable UUID agentId) {
        log.info("Fetching visits for agent: {}", agentId);
        try {
            List<VisitResponse> visits = visitService.getVisitsByAgent(agentId);
            log.info("Retrieved {} visits for agent", visits.size());
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            log.error("Error fetching visits by agent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/upcoming/customer/{customerId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByCustomer(@PathVariable UUID customerId) {
        log.info("Fetching visits for customer: {}", customerId);
        try {
            List<VisitResponse> visits = visitService.getVisitsByCustomer(customerId);
            log.info("Retrieved {} visits for customer", visits.size());
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            log.error("Error fetching visits by customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByProperty(@PathVariable UUID propertyId) {
        log.info("Fetching visits for property: {}", propertyId);
        try {
            List<VisitResponse> visits = visitService.getVisitsByProperty(propertyId);
            log.info("Retrieved {} visits for property", visits.size());
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            log.error("Error fetching visits by property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{trackingId}/confirm")
    public ResponseEntity<VisitResponse> confirmVisit(@PathVariable UUID trackingId) {
        log.info("Confirming visit: {}", trackingId);
        try {
            VisitResponse visit = visitService.confirmVisit(trackingId);
            log.info("Visit confirmed successfully");
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            log.error("Error confirming visit: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/cancel")
    public ResponseEntity<VisitResponse> cancelVisit(@PathVariable UUID trackingId, @RequestParam(required = false) String reason) {
        log.info("Cancelling visit: {} with reason: {}", trackingId, reason);
        try {
            VisitResponse visit = visitService.cancelVisit(trackingId, reason);
            log.info("Visit cancelled successfully");
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            log.error("Error cancelling visit: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/complete")
    public ResponseEntity<VisitResponse> completeVisit(@PathVariable UUID trackingId, @RequestParam(required = false) String comments) {
        log.info("Completing visit: {} with comments: {}", trackingId, comments);
        try {
            VisitResponse visit = visitService.completeVisit(trackingId, comments);
            log.info("Visit completed successfully");
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            log.error("Error completing visit: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/agent/{agentId}/availability")
    public ResponseEntity<List<String>> getAgentAvailability(
            @PathVariable UUID agentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("Fetching availability for agent: {} from {} to {}", agentId, startDate, endDate);
        try {
            List<String> availableSlots = visitService.getAgentAvailability(agentId, startDate, endDate);
            log.info("Retrieved {} available slots", availableSlots.size());
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            log.error("Error fetching agent availability: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VisitResponse>> getVisitsByStatus(@PathVariable String status) {
        log.info("Fetching visits with status: {}", status);
        try {
            List<VisitResponse> visits = visitService.getVisitsByStatus(status);
            log.info("Retrieved {} visits with status: {}", visits.size(), status);
            return ResponseEntity.ok(visits);
        } catch (Exception e) {
            log.error("Error fetching visits by status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/reschedule")
    public ResponseEntity<VisitResponse> rescheduleVisit(
            @PathVariable UUID trackingId,
            @RequestParam String newDateTime,
            @RequestParam(required = false) String reason) {
        log.info("Rescheduling visit: {} to {} for reason: {}", trackingId, newDateTime, reason);
        try {
            VisitResponse visit = visitService.rescheduleVisit(trackingId, newDateTime, reason);
            log.info("Visit rescheduled successfully");
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            log.error("Error rescheduling visit: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}