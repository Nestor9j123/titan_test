package nitchcorp.backend.titan.immo.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nitchcorp.backend.titan.immo.application.dto.requests.AgentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.AgentResponse;
import nitchcorp.backend.titan.immo.domain.exceptions.UserNotFoundException;
import nitchcorp.backend.titan.immo.application.service.AgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Agents", description = "API pour la gestion des agents immobiliers")
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/create")
    @Operation(summary = "Créer un nouvel agent", description = "Crée un nouvel agent immobilier dans le système")
    @ApiResponse(responseCode = "201", description = "Agent créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<AgentResponse> createAgent(
            @Parameter(description = "Informations de l'agent à créer", required = true)
            @Valid @RequestBody AgentRequest request) {
        log.info("Creating agent with email: {} at 11:19 AM GMT, 20/08/2025", request.email());
        try {
            AgentResponse response = agentService.createAgent(request);
            log.info("Agent created successfully with trackingId: {}", response.trackingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for creating agent: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating agent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Récupérer tous les agents", description = "Récupère la liste de tous les agents immobiliers")
    @ApiResponse(responseCode = "200", description = "Liste des agents récupérée avec succès")
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        log.info("Fetching all agents at 11:19 AM GMT, 20/08/2025");
        try {
            List<AgentResponse> agents = agentService.getAllAgents();
            log.info("Retrieved {} agents", agents.size());
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            log.error("Error fetching all agents: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un agent par ID de suivi", description = "Récupère un agent spécifique par son ID de suivi")
    @ApiResponse(responseCode = "200", description = "Agent trouvé")
    @ApiResponse(responseCode = "404", description = "Agent non trouvé")
    public ResponseEntity<AgentResponse> getAgentByTrackingId(
            @Parameter(description = "ID de suivi de l'agent", required = true)
            @PathVariable UUID trackingId) {
        log.info("Fetching agent with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            AgentResponse agent = agentService.getAgentByTrackingId(trackingId);
            log.info("Agent found with trackingId: {}", trackingId);
            return ResponseEntity.ok(agent);
        } catch (UserNotFoundException e) {
            log.error("Agent not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{trackingId}")
    @Operation(summary = "Mettre à jour un agent", description = "Met à jour les informations d'un agent existant")
    @ApiResponse(responseCode = "200", description = "Agent mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Agent non trouvé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<AgentResponse> updateAgent(
            @Parameter(description = "ID de suivi de l'agent", required = true)
            @PathVariable UUID trackingId,
            @Parameter(description = "Nouvelles informations de l'agent", required = true)
            @Valid @RequestBody AgentRequest request) {
        log.info("Updating agent with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            AgentResponse updatedAgent = agentService.updateAgent(trackingId, request);
            log.info("Agent updated successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok(updatedAgent);
        } catch (UserNotFoundException e) {
            log.error("Agent not found with trackingId: {}", trackingId);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request for updating agent: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{trackingId}")
    @Operation(summary = "Supprimer un agent", description = "Supprime un agent du système")
    @ApiResponse(responseCode = "200", description = "Agent supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Agent non trouvé")
    public ResponseEntity<String> deleteAgent(
            @Parameter(description = "ID de suivi de l'agent", required = true)
            @PathVariable UUID trackingId) {
        log.info("Deleting agent with trackingId: {} at 11:19 AM GMT, 20/08/2025", trackingId);
        try {
            agentService.deleteAgent(trackingId);
            log.info("Agent deleted successfully with trackingId: {}", trackingId);
            return ResponseEntity.ok("Agent supprimé avec succès");
        } catch (UserNotFoundException e) {
            log.error("Agent not found with trackingId: {}", trackingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agent non trouvé");
        } catch (IllegalArgumentException e) {
            log.error("Invalid trackingId for deletion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("ID de tracking invalide");
        }
    }
}