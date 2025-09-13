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
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerAgentAssignmentResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.OwnerAgentAssignmentNotFoundException;
import nitchcorp.backend.titan.immo.application.service.OwnerAgentAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "OwnerAgentAssignmentController", description = "API pour OwnerAgentAssignmentController")
@RestController
@RequestMapping("/api/owner-agent-assignments")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class OwnerAgentAssignmentController {

    private final OwnerAgentAssignmentService assignmentService;

    @PostMapping("/create")
    public ResponseEntity<OwnerAgentAssignmentResponse> createOwnerAgentAssignment(@Valid @RequestBody OwnerAgentAssignmentRequest request) {
        log.info("Received request: ownerId={}, agentId={}, propertyId={}",
                request.ownerId(), request.agentId(), request.propertyId());

        if (request.ownerId() == null || request.agentId() == null || request.propertyId() == null) {
            log.error("Missing required IDs in the request. Full request: {}", request);
            return ResponseEntity.badRequest().body(null);
        }

        log.info("Creating owner-agent assignment for property: {}", request.propertyId());

        try {
            OwnerAgentAssignmentResponse response = assignmentService.createOwnerAgentAssignment(request);
            log.info("Owner-agent assignment created successfully with ID: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (OwnerAgentAssignmentNotFoundException e) {
            log.error("Owner-agent assignment creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating owner-agent assignment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating owner-agent assignment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OwnerAgentAssignmentResponse>> getAllOwnerAgentAssignments() {
        log.info("Fetching all owner-agent assignments");
        try {
            List<OwnerAgentAssignmentResponse> assignments = assignmentService.getAllOwnerAgentAssignments();
            log.info("Retrieved {} owner-agent assignments", assignments.size());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error fetching all owner-agent assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<OwnerAgentAssignmentResponse> getOwnerAgentAssignmentByTrackingId(@PathVariable UUID trackingId) {
        log.info("Fetching owner-agent assignment with trackingId: {}", trackingId);
        try {
            OwnerAgentAssignmentResponse assignment = assignmentService.getOwnerAgentAssignmentByTrackingId(trackingId);
            log.info("Owner-agent assignment found with trackingId: {}", trackingId);
            return ResponseEntity.ok(assignment);
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Owner-agent assignment not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    public ResponseEntity<OwnerAgentAssignmentResponse> updateOwnerAgentAssignment(
            @PathVariable UUID trackingId, @Valid @RequestBody OwnerAgentAssignmentRequest request) {
        log.info("Updating owner-agent assignment with trackingId: {}", trackingId);
        try {
            OwnerAgentAssignmentResponse updatedAssignment = assignmentService.updateOwnerAgentAssignment(trackingId, request);
            log.info("Owner-agent assignment updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedAssignment);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating owner-agent assignment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating owner-agent assignment with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    public ResponseEntity<String> deleteOwnerAgentAssignment(@PathVariable UUID trackingId) {
        log.info("Deleting owner-agent assignment with trackingId: {}", trackingId);
        try {
            assignmentService.deleteOwnerAgentAssignment(trackingId);
            log.info("Owner-agent assignment deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Assignation supprimée avec succès");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        }
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<String> deleteOwnerAgentAssignmentRestful(@PathVariable UUID trackingId) {
        return deleteOwnerAgentAssignment(trackingId);
    }

    // Endpoints manquants selon le cahier des charges

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<OwnerAgentAssignmentResponse>> getAssignmentsByOwner(@PathVariable UUID ownerId) {
        log.info("Fetching assignments for owner: {}", ownerId);
        try {
            List<OwnerAgentAssignmentResponse> assignments = assignmentService.getAssignmentsByOwner(ownerId);
            log.info("Retrieved {} assignments for owner", assignments.size());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error fetching assignments by owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<OwnerAgentAssignmentResponse>> getAssignmentsByAgent(@PathVariable UUID agentId) {
        log.info("Fetching assignments for agent: {}", agentId);
        try {
            List<OwnerAgentAssignmentResponse> assignments = assignmentService.getAssignmentsByAgent(agentId);
            log.info("Retrieved {} assignments for agent", assignments.size());
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            log.error("Error fetching assignments by agent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<OwnerAgentAssignmentResponse> getAssignmentByProperty(@PathVariable UUID propertyId) {
        log.info("Fetching assignment for property: {}", propertyId);
        try {
            OwnerAgentAssignmentResponse assignment = assignmentService.getAssignmentByProperty(propertyId);
            log.info("Retrieved assignment for property");
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error fetching assignment by property: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/available-agents")
    public ResponseEntity<List<OwnerAgentAssignmentResponse>> getAvailableAgents(@RequestParam(required = false) String country) {
        log.info("Fetching available agents for country: {}", country);
        try {
            List<OwnerAgentAssignmentResponse> agents = assignmentService.getAvailableAgents(country);
            log.info("Retrieved {} available agents", agents.size());
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            log.error("Error fetching available agents: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{trackingId}/activate")
    public ResponseEntity<OwnerAgentAssignmentResponse> activateAssignment(@PathVariable UUID trackingId) {
        log.info("Activating assignment: {}", trackingId);
        try {
            OwnerAgentAssignmentResponse assignment = assignmentService.activateAssignment(trackingId);
            log.info("Assignment activated successfully");
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error activating assignment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/deactivate")
    public ResponseEntity<OwnerAgentAssignmentResponse> deactivateAssignment(@PathVariable UUID trackingId) {
        log.info("Deactivating assignment: {}", trackingId);
        try {
            OwnerAgentAssignmentResponse assignment = assignmentService.deactivateAssignment(trackingId);
            log.info("Assignment deactivated successfully");
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error deactivating assignment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{trackingId}/transfer")
    public ResponseEntity<OwnerAgentAssignmentResponse> transferAssignment(
            @PathVariable UUID trackingId, 
            @RequestParam UUID newAgentId,
            @RequestParam(required = false) String reason) {
        log.info("Transferring assignment: {} to new agent: {} for reason: {}", trackingId, newAgentId, reason);
        try {
            OwnerAgentAssignmentResponse assignment = assignmentService.transferAssignment(trackingId, newAgentId, reason);
            log.info("Assignment transferred successfully");
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            log.error("Error transferring assignment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
