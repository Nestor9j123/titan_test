package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.LeaseContratRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.LeaseContratResponse;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.UUID;

@NamedInterface
public interface LeaseContratService {
    LeaseContratResponse createLeaseContrat(LeaseContratRequest request);
    List<LeaseContratResponse> getAllLeaseContrats();
    LeaseContratResponse updateLeaseContrat(UUID trackingId, LeaseContratRequest request);
    void deleteLeaseContrat(UUID trackingId);
    LeaseContratResponse getLeaseContratByTrackingId(UUID trackingId);
    
    // Nouvelles méthodes ajoutées pour les endpoints
    List<LeaseContratResponse> getLeaseContratsByProperty(UUID propertyId);
    List<LeaseContratResponse> getLeaseContratsByCustomer(UUID customerId);
    List<LeaseContratResponse> getLeaseContratsByOwner(UUID ownerId);
    List<LeaseContratResponse> getLeaseContratsByAgent(UUID agentId);
    List<LeaseContratResponse> getActiveLeaseContracts();
    List<LeaseContratResponse> getExpiringLeaseContracts(int daysBeforeExpiry);
    LeaseContratResponse signLeaseContract(UUID trackingId, String signerType);
    LeaseContratResponse terminateLeaseContract(UUID trackingId, String reason, String terminationDate);
    LeaseContratResponse renewLeaseContract(UUID trackingId, String newEndDate, Double newRent);
    LeaseContratResponse confirmDeposit(UUID trackingId, Double depositAmount);
    byte[] generateLeaseContractPdf(UUID trackingId);
    List<LeaseContratResponse> getLeaseContractHistory(UUID propertyId);
}