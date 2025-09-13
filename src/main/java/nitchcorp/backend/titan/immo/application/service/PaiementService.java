package nitchcorp.backend.titan.immo.application.service;

import nitchcorp.backend.titan.immo.application.dto.requests.PaiementRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PaiementResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaiementService {
    PaiementResponse createPaiement(PaiementRequest request);
    List<PaiementResponse> getAllPaiements();
    PaiementResponse updatePaiement(UUID trackingId, PaiementRequest request);
    void deletePaiement(UUID trackingId);
    PaiementResponse getPaiementByTrackingId(UUID trackingId);
    
    // Nouvelles méthodes ajoutées pour les endpoints
    List<PaiementResponse> getPaiementsByLeaseContract(UUID contractId);
    List<PaiementResponse> getPaiementsByCustomer(UUID customerId);
    List<PaiementResponse> getPaiementsByOwner(UUID ownerId);
    List<PaiementResponse> getOverduePayments();
    List<PaiementResponse> getPaiementsByStatus(String status);
    PaiementResponse confirmPayment(UUID trackingId);
    PaiementResponse markPaymentOverdue(UUID trackingId);
    void generatePaymentReminder(UUID trackingId);
    List<PaiementResponse> getPaymentHistory(String startDate, String endDate, UUID customerId, UUID ownerId);
    Map<String, Object> getPaymentStats(UUID ownerId);
    void setupAutoPayment(UUID leaseContractId, boolean enabled);
}