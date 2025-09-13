package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.PaiementRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.PaiementResponse;
import nitchcorp.backend.titan.immo.domain.model.LeaseContrat;
import nitchcorp.backend.titan.immo.domain.model.Paiement;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaiementMapper {

    public PaiementResponse toResponse(Paiement paiement) {
        if (paiement == null) {
            throw new IllegalArgumentException("Paiement cannot be null");
        }

        return new PaiementResponse(
                paiement.getTrackingId(),
                paiement.getId(),
                (paiement.getLeaseContract() != null) ? paiement.getLeaseContract().getTrackingId() : null,
                (paiement.getCustomer() != null) ? paiement.getCustomer().getTrackingId() : null,
                paiement.getAmount(),
                paiement.getPaymentType(),
                paiement.getPaymentDate(),
                paiement.getDueDate(),
                paiement.getStatus(),
                paiement.getTransactionId()
        );
    }

    public Paiement toEntity(PaiementRequest request, LeaseContrat leaseContract, User customer) {
        if (request == null) {
            throw new IllegalArgumentException("PaiementRequest cannot be null");
        }

        Paiement paiement = new Paiement();
        paiement.setTrackingId(UUID.randomUUID());
        paiement.setLeaseContract(leaseContract);
        paiement.setCustomer(customer);
        paiement.setAmount(request.amount());
        paiement.setPaymentType(request.paymentType());
        paiement.setPaymentDate(request.paymentDate());
        paiement.setDueDate(request.dueDate());
        paiement.setStatus(request.status());
        paiement.setTransactionId(request.transactionId());

        return paiement;
    }

    public static Paiement toEntityFromResponse(PaiementResponse response, LeaseContrat leaseContract, User customer) {
        if (response == null) {
            throw new IllegalArgumentException("PaiementResponse cannot be null");
        }

        Paiement paiement = new Paiement();
        paiement.setTrackingId(response.trackingId());
        paiement.setId(response.id());
        paiement.setLeaseContract(leaseContract);
        paiement.setCustomer(customer);
        paiement.setAmount(response.amount());
        paiement.setPaymentType(response.paymentType());
        paiement.setPaymentDate(response.paymentDate());
        paiement.setDueDate(response.dueDate());
        paiement.setStatus(response.status());
        paiement.setTransactionId(response.transactionId());

        return paiement;
    }
}