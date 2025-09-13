package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.OwnerAgentAssignmentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerAgentAssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface OwnerAgentAssignmentService {
    OwnerAgentAssignmentResponse createOwnerAgentAssignment(OwnerAgentAssignmentRequest request);
    List<OwnerAgentAssignmentResponse> getAllOwnerAgentAssignments();
    OwnerAgentAssignmentResponse updateOwnerAgentAssignment(UUID trackingId, OwnerAgentAssignmentRequest request);
    void deleteOwnerAgentAssignment(UUID trackingId);
    OwnerAgentAssignmentResponse getOwnerAgentAssignmentByTrackingId(UUID trackingId);
    
    // Nouvelles méthodes ajoutées pour les endpoints
    List<OwnerAgentAssignmentResponse> getAssignmentsByOwner(UUID ownerId);
    List<OwnerAgentAssignmentResponse> getAssignmentsByAgent(UUID agentId);
    OwnerAgentAssignmentResponse getAssignmentByProperty(UUID propertyId);
    List<OwnerAgentAssignmentResponse> getAvailableAgents(String country);
    OwnerAgentAssignmentResponse activateAssignment(UUID trackingId);
    OwnerAgentAssignmentResponse deactivateAssignment(UUID trackingId);
    OwnerAgentAssignmentResponse transferAssignment(UUID trackingId, UUID newAgentId, String reason);
}