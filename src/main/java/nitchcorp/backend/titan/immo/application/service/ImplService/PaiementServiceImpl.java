package nitchcorp.backend.titan.immo.application.service.ImplService;

import lombok.RequiredArgsConstructor;
import nitchcorp.backend.titan.immo.application.dto.requests.PaiementRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PaiementResponse;
import nitchcorp.backend.titan.immo.application.mapper.PaiementMapper;
import nitchcorp.backend.titan.immo.domain.model.Customer;
import nitchcorp.backend.titan.immo.domain.model.LeaseContrat;
import nitchcorp.backend.titan.immo.domain.model.Paiement;
import nitchcorp.backend.titan.immo.domain.enums.PaiementStatus;
import nitchcorp.backend.titan.immo.domain.exceptions.PaiementNotFoundException;
import nitchcorp.backend.titan.immo.infrastructure.CustomerRepository;
import nitchcorp.backend.titan.immo.infrastructure.LeaseContractRepository;
import nitchcorp.backend.titan.immo.infrastructure.PaiementRepository;
import nitchcorp.backend.titan.immo.application.service.PaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final LeaseContractRepository leaseContratRepository;
    private final CustomerRepository customerRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public PaiementResponse createPaiement(PaiementRequest request) {
        LeaseContrat leaseContrat = leaseContratRepository.getLeaseContractByTrackingId(request.leaseContractTrackingId())
                .orElseThrow(() -> new PaiementNotFoundException("LeaseContrat not found with ID: " + request.leaseContractTrackingId()));
        Customer customer = customerRepository.getCustomerByTrackingId(request.customerTrackingId())
                .orElseThrow(() -> new PaiementNotFoundException("Customer not found with ID: " + request.customerTrackingId()));

        Paiement paiement = paiementMapper.toEntity(request, leaseContrat, customer);
        Paiement savedPaiement = paiementRepository.save(paiement);

        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    public List<PaiementResponse> getAllPaiements() {
        List<Paiement> paiements = paiementRepository.findAll();
        return paiements.stream().map(paiementMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaiementResponse updatePaiement(UUID trackingId, PaiementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PaiementRequest cannot be null");
        }
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Paiement existingPaiement = paiementRepository.getPaiementByTrackingId(trackingId)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found with trackingId: " + trackingId));

        LeaseContrat leaseContrat = leaseContratRepository.getLeaseContractByTrackingId(request.leaseContractTrackingId())
                .orElseThrow(() -> new PaiementNotFoundException("LeaseContrat not found with ID: " + request.leaseContractTrackingId()));
        Customer customer = customerRepository.getCustomerByTrackingId(request.customerTrackingId())
                .orElseThrow(() -> new PaiementNotFoundException("Customer not found with ID: " + request.customerTrackingId()));

        existingPaiement.setLeaseContract(leaseContrat);
        existingPaiement.setCustomer(customer);
        existingPaiement.setAmount(request.amount());
        existingPaiement.setPaymentType(request.paymentType());
        existingPaiement.setPaymentDate(request.paymentDate());
        existingPaiement.setDueDate(request.dueDate());
        existingPaiement.setStatus(request.status());
        existingPaiement.setTransactionId(request.transactionId());

        Paiement updatedPaiement = paiementRepository.save(existingPaiement);
        return paiementMapper.toResponse(updatedPaiement);
    }

    @Override
    public void deletePaiement(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Paiement existingPaiement = paiementRepository.getPaiementByTrackingId(trackingId)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found with trackingId: " + trackingId));

        paiementRepository.delete(existingPaiement);
    }

    @Override
    public PaiementResponse getPaiementByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            throw new IllegalArgumentException("trackingId cannot be null");
        }

        Paiement paiement = paiementRepository.getPaiementByTrackingId(trackingId)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found with trackingId: " + trackingId));

        return paiementMapper.toResponse(paiement);
    }

    @Override
    public List<PaiementResponse> getPaiementsByLeaseContract(UUID contractId) {
        return paiementRepository.findByLeaseContractTrackingId(contractId).stream()
                .map(paiementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementResponse> getPaiementsByCustomer(UUID customerId) {
        return paiementRepository.findByCustomerTrackingId(customerId).stream()
                .map(paiementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementResponse> getPaiementsByOwner(UUID ownerId) {
        return paiementRepository.getPaiementByTrackingId(ownerId).stream()
                .map(paiementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementResponse> getOverduePayments() {
        return paiementRepository.findOverduePayments().stream()
                .map(paiementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaiementResponse> getPaiementsByStatus(String status) {
        try {
            PaiementStatus paiementStatus = PaiementStatus.valueOf(status.toUpperCase());
            return paiementRepository.findByStatus(paiementStatus).stream()
                    .map(paiementMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    @Override
    public PaiementResponse confirmPayment(UUID trackingId) {
        Paiement paiement = paiementRepository.getPaiementByTrackingId(trackingId)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found"));
        paiement.setStatus(PaiementStatus.PAID);
        return paiementMapper.toResponse(paiementRepository.save(paiement));
    }

    @Override
    public PaiementResponse markPaymentOverdue(UUID trackingId) {
        Paiement paiement = paiementRepository.getPaiementByTrackingId(trackingId)
                .orElseThrow(() -> new PaiementNotFoundException("Paiement not found"));
        paiement.setStatus(PaiementStatus.OVERDUE);
        return paiementMapper.toResponse(paiementRepository.save(paiement));
    }

    @Override
    public void generatePaymentReminder(UUID trackingId) {
        // Logique pour générer et envoyer un rappel (ex: email, notification)
        // Cette partie nécessite une implémentation plus complexe (service de notification, etc.)
    }

    @Override
    public List<PaiementResponse> getPaymentHistory(String startDate, String endDate, UUID customerId, UUID ownerId) {
        // La logique de filtrage par date, client et propriétaire doit être implémentée ici.
        // Pour l'instant, nous retournons une liste vide.
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getPaymentStats(UUID ownerId) {
        // Logique pour calculer les statistiques (total perçu, paiements en retard, etc.)
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Statistics generation not yet implemented.");
        return stats;
    }

    @Override
    public void setupAutoPayment(UUID leaseContractId, boolean enabled) {
        LeaseContrat leaseContract = leaseContratRepository.getLeaseContractByTrackingId(leaseContractId)
                .orElseThrow(() -> new PaiementNotFoundException("LeaseContrat not found with ID: " + leaseContractId));
        leaseContract.setAutoPaymentEnabled(enabled);
        leaseContratRepository.save(leaseContract);
    }
}