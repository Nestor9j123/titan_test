package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.OwnerAgentAssignmentRequest;
import nitchcorp.backend.titan.immo.application.dto.requests.OwnerRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.AgentResponse;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerAgentAssignmentResponse;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.application.service.AgentService;
import nitchcorp.backend.titan.immo.application.service.OwnerAgentAssignmentService;
import nitchcorp.backend.titan.immo.application.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "OwnerController", description = "API pour OwnerController")
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class OwnerController {

    private final OwnerService ownerService;
    private final OwnerAgentAssignmentService assignmentService;
    private final AgentService agentService;

    @PostMapping("/create")
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody OwnerRequest request) {
        log.info("Creating owner with email: {} at 11:19 AM GMT, 20/08/2025", request.email());
        try {
            OwnerResponse response = ownerService.createOwner(request);
            log.info("Owner created successfully with trackingId: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating owner: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OwnerResponse>> getAllOwners() {
        log.info("Fetching all owners at 11:19 AM GMT, 20/08/2025");
        try {
            List<OwnerResponse> owners = ownerService.getAllOwners();
            log.info("Retrieved {} owners", owners.size());
            return ResponseEntity.ok(owners);
        } catch (Exception e) {
            log.error("Error fetching all owners: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<OwnerResponse> getOwnerByTrackingId(@PathVariable UUID trackingId) {
        log.info("Fetching owner with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            OwnerResponse owner = ownerService.getOwnerByTrackingId(trackingId);
            log.info("Owner found with trackingId: {}", trackingId);
            return ResponseEntity.ok(owner);
        } catch (UserNotFoundException e) {
            log.error("Owner not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable UUID trackingId, @Valid @RequestBody OwnerRequest request) {
        log.info("Updating owner with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            OwnerResponse updatedOwner = ownerService.updateOwner(trackingId, request);
            log.info("Owner updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedOwner);
        } catch (UserNotFoundException e) {
            log.error("Owner not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating owner: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    public ResponseEntity<String> deleteOwner(@PathVariable UUID trackingId) {
        log.info("Deleting owner with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            ownerService.deleteOwner(trackingId);
            log.info("Owner deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Propriétaire supprimé avec succès");
        } catch (UserNotFoundException e) {
            log.error("Owner not found with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Propriétaire non trouvé");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        }
    }

    // ===== ENDPOINTS FOR PROPERTY OWNER AGENT MANAGEMENT =====
    
    /**
     * Permet au propriétaire d'assigner un agent à une de ses propriétés
     */
    @PostMapping("/{ownerTrackingId}/assign-agent")
    public ResponseEntity<OwnerAgentAssignmentResponse> assignAgentToProperty(
            @PathVariable UUID ownerTrackingId,
            @Valid @RequestBody OwnerAgentAssignmentRequest request) {
        log.info("Owner {} assigning agent {} to property {}", ownerTrackingId, request.agentId(), request.propertyId());
        try {
            // Vérifier que le propriétaire existe et correspond à celui de la requête
            if (!ownerTrackingId.equals(request.ownerId())) {
                log.error("Owner trackingId mismatch: path={}, request={}", ownerTrackingId, request.ownerId());
                return ResponseEntity.badRequest().body(null);
            }
            
            OwnerAgentAssignmentResponse response = assignmentService.createOwnerAgentAssignment(request);
            log.info("Agent assigned successfully with trackingId: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error assigning agent to property: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Récupère toutes les assignations d'agents pour un propriétaire
     */
    @GetMapping("/{ownerTrackingId}/agent-assignments")
    public ResponseEntity<List<OwnerAgentAssignmentResponse>> getOwnerAgentAssignments(@PathVariable UUID ownerTrackingId) {
        log.info("Fetching agent assignments for owner: {}", ownerTrackingId);
        try {
            List<OwnerAgentAssignmentResponse> assignments = assignmentService.getAssignmentsByOwner(ownerTrackingId);
            log.info("Retrieved {} agent assignments for owner", assignments.size());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error fetching owner agent assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère la liste des agents disponibles pour assignation dans le pays du propriétaire
     */
    @GetMapping("/{ownerTrackingId}/available-agents")
    public ResponseEntity<List<AgentResponse>> getAvailableAgentsForOwner(@PathVariable UUID ownerTrackingId) {
        log.info("Fetching available agents for owner: {}", ownerTrackingId);
        try {
            // Récupérer le propriétaire pour obtenir son pays
            OwnerResponse owner = ownerService.getOwnerByTrackingId(ownerTrackingId);
            
            // Récupérer tous les agents validés dans le même pays
            List<AgentResponse> availableAgents = agentService.getAllAgents().stream()
                    .filter(agent -> agent.country().equals(owner.country()) && agent.isActif())
                    .toList();
            
            log.info("Retrieved {} available agents for owner in country: {}", availableAgents.size(), owner.country());
            return ResponseEntity.ok(availableAgents);
        } catch (UserNotFoundException e) {
            log.error("Owner not found: {}", ownerTrackingId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching available agents: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Transfère la gestion d'une propriété à un autre agent
     */
    @PutMapping("/{ownerTrackingId}/transfer-agent/{assignmentTrackingId}")
    public ResponseEntity<OwnerAgentAssignmentResponse> transferPropertyAgent(
            @PathVariable UUID ownerTrackingId,
            @PathVariable UUID assignmentTrackingId,
            @RequestParam UUID newAgentId,
            @RequestParam(required = false) String reason) {
        log.info("Owner {} transferring assignment {} to new agent: {}", ownerTrackingId, assignmentTrackingId, newAgentId);
        try {
            // Vérifier que l'assignation appartient bien au propriétaire
            OwnerAgentAssignmentResponse existingAssignment = assignmentService.getOwnerAgentAssignmentByTrackingId(assignmentTrackingId);
            if (!existingAssignment.ownerId().equals(ownerTrackingId)) {
                log.error("Assignment does not belong to owner: assignment owner={}, requested owner={}", 
                         existingAssignment.ownerId(), ownerTrackingId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            OwnerAgentAssignmentResponse response = assignmentService.transferAssignment(assignmentTrackingId, newAgentId, reason);
            log.info("Agent transfer completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error transferring agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Active ou désactive une assignation d'agent
     */
    @PutMapping("/{ownerTrackingId}/toggle-assignment/{assignmentTrackingId}")
    public ResponseEntity<OwnerAgentAssignmentResponse> toggleAgentAssignment(
            @PathVariable UUID ownerTrackingId,
            @PathVariable UUID assignmentTrackingId,
            @RequestParam boolean activate) {
        log.info("Owner {} {} assignment {}", ownerTrackingId, activate ? "activating" : "deactivating", assignmentTrackingId);
        try {
            // Vérifier que l'assignation appartient bien au propriétaire
            OwnerAgentAssignmentResponse existingAssignment = assignmentService.getOwnerAgentAssignmentByTrackingId(assignmentTrackingId);
            if (!existingAssignment.ownerId().equals(ownerTrackingId)) {
                log.error("Assignment does not belong to owner");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            OwnerAgentAssignmentResponse response = activate 
                ? assignmentService.activateAssignment(assignmentTrackingId)
                : assignmentService.deactivateAssignment(assignmentTrackingId);
            
            log.info("Assignment {} successfully", activate ? "activated" : "deactivated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error toggling assignment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Supprime une assignation d'agent (révoque l'accès)
     */
    @DeleteMapping("/{ownerTrackingId}/revoke-agent/{assignmentTrackingId}")
    public ResponseEntity<String> revokeAgentAssignment(
            @PathVariable UUID ownerTrackingId,
            @PathVariable UUID assignmentTrackingId) {
        log.info("Owner {} revoking agent assignment {}", ownerTrackingId, assignmentTrackingId);
        try {
            // Vérifier que l'assignation appartient bien au propriétaire
            OwnerAgentAssignmentResponse existingAssignment = assignmentService.getOwnerAgentAssignmentByTrackingId(assignmentTrackingId);
            if (!existingAssignment.ownerId().equals(ownerTrackingId)) {
                log.error("Assignment does not belong to owner");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Assignation non autorisée");
            }
            
            assignmentService.deleteOwnerAgentAssignment(assignmentTrackingId);
            log.info("Agent assignment revoked successfully");
            return ResponseEntity.ok("Assignation d'agent révoquée avec succès");
        } catch (Exception e) {
            log.error("Error revoking agent assignment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur lors de la révocation de l'assignation");
        }
    }
}
