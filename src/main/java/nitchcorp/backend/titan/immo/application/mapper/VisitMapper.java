package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.VisitRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.VisitResponse;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.immo.domain.model.Property;
import nitchcorp.backend.titan.immo.domain.model.Visit;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VisitMapper {

    public VisitResponse toResponse(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Visit cannot be null");
        }

        UUID propertyTrackingId = (visit.getProperty() != null) ? visit.getProperty().getTrackingId() : null;

        return new VisitResponse(
                visit.getTrackingId(),
                visit.getId(),
                (visit.getProperty() != null) ? visit.getProperty().getId() : null,
                (visit.getAgent() != null) ? visit.getAgent().getId() : null,
                (visit.getCustomer() != null) ? visit.getCustomer().getId() : null,
                visit.getVisitDate(),
                visit.getStatus(),
                visit.getComments()
        );
    }

    public Visit toEntity(VisitRequest request, Property property, User agent, User customer) {
        if (request == null) {
            throw new IllegalArgumentException("VisitRequest cannot be null");
        }

        Visit visit = new Visit();
        visit.setTrackingId(UUID.randomUUID());
        visit.setProperty(property);
        visit.setAgent(agent);
        visit.setCustomer(customer);
        visit.setVisitDate(request.visitDate());
        visit.setStatus(request.status());
        visit.setComments(request.comments());

        return visit;
    }

    public static Visit toEntityFromResponse(VisitResponse response, Property property, User agent, User customer) {
        if (response == null) {
            throw new IllegalArgumentException("VisitResponse cannot be null");
        }

        Visit visit = new Visit();
        visit.setTrackingId(response.trackingId());
        visit.setId(response.id());
        visit.setProperty(property);
        visit.setAgent(agent);
        visit.setCustomer(customer);
        visit.setVisitDate(response.visitDate());
        visit.setStatus(response.status());
        visit.setComments(response.comments());

        return visit;
    }
}