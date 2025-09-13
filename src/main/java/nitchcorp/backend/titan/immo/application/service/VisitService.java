package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.VisitRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.VisitResponse;

import java.util.List;
import java.util.UUID;

public interface VisitService {
    VisitResponse createVisit(VisitRequest request);
    List<VisitResponse> getAllVisits();
    VisitResponse updateVisit(UUID trackingId, VisitRequest request);
    void deleteVisit(UUID trackingId);
    VisitResponse getVisitByTrackingId(UUID trackingId);
    
    // Nouvelles méthodes ajoutées pour les endpoints
    List<VisitResponse> getVisitsByAgent(UUID agentId);
    List<VisitResponse> getVisitsByCustomer(UUID customerId);
    List<VisitResponse> getVisitsByProperty(UUID propertyId);
    VisitResponse confirmVisit(UUID trackingId);
    VisitResponse cancelVisit(UUID trackingId, String reason);
    VisitResponse completeVisit(UUID trackingId, String comments);
    List<String> getAgentAvailability(UUID agentId, String startDate, String endDate);
    List<VisitResponse> getVisitsByStatus(String status);
    VisitResponse rescheduleVisit(UUID trackingId, String newDateTime, String reason);
}