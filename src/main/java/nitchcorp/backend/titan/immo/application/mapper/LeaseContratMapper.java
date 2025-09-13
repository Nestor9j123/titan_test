package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.LeaseContratRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.LeaseContratResponse;
import nitchcorp.backend.titan.immo.domain.model.LeaseContrat;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.immo.domain.model.Property;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LeaseContratMapper {

    public LeaseContratResponse toResponse(LeaseContrat leaseContrat) {
        if (leaseContrat == null) {
            throw new IllegalArgumentException("LeaseContrat cannot be null");
        }

        UUID propertyTrackingId = (leaseContrat.getProperty() != null) ? leaseContrat.getProperty().getTrackingId() : null;

        return new LeaseContratResponse(
                leaseContrat.getTrackingId(),
                leaseContrat.getId(),
                (leaseContrat.getProperty() != null) ? leaseContrat.getProperty().getId() : null,
                (leaseContrat.getCustomer() != null) ? leaseContrat.getCustomer().getId() : null,
                (leaseContrat.getAgent() != null) ? leaseContrat.getAgent().getId() : null,
                leaseContrat.getStartDate(),
                leaseContrat.getEndDate(),
                leaseContrat.getRentAmount(),
                leaseContrat.getDepositAmount(),
                leaseContrat.getContractDocument(),
                leaseContrat.getStatus()
        );
    }

    public LeaseContrat toEntity(LeaseContratRequest request, Property property, User customer, User agent) {
        if (request == null) {
            throw new IllegalArgumentException("LeaseContratRequest cannot be null");
        }

        LeaseContrat leaseContrat = new LeaseContrat();
        leaseContrat.setTrackingId(UUID.randomUUID());
        leaseContrat.setProperty(property);
        leaseContrat.setCustomer(customer);
        leaseContrat.setAgent(agent);
        leaseContrat.setStartDate(request.startDate());
        leaseContrat.setEndDate(request.endDate());
        leaseContrat.setRentAmount(request.rentAmount());
        leaseContrat.setDepositAmount(request.depositAmount());
        leaseContrat.setContractDocument(request.contractDocument());
        leaseContrat.setStatus(request.status());

        return leaseContrat;
    }

    public static LeaseContrat toEntityFromResponse(LeaseContratResponse response, Property property, User customer, User agent) {
        if (response == null) {
            throw new IllegalArgumentException("LeaseContratResponse cannot be null");
        }

        LeaseContrat leaseContrat = new LeaseContrat();
        leaseContrat.setTrackingId(response.trackingId());
        leaseContrat.setId(response.id());
        leaseContrat.setProperty(property);
        leaseContrat.setCustomer(customer);
        leaseContrat.setAgent(agent);
        leaseContrat.setStartDate(response.startDate());
        leaseContrat.setEndDate(response.endDate());
        leaseContrat.setRentAmount(response.rentAmount());
        leaseContrat.setDepositAmount(response.depositAmount());
        leaseContrat.setContractDocument(response.contractDocument());
        leaseContrat.setStatus(response.status());

        return leaseContrat;
    }
}