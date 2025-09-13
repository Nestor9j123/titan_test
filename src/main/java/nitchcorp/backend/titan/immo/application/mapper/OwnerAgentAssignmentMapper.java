package nitchcorp.backend.titan.immo.application.mapper;

import nitchcorp.backend.titan.immo.application.dto.requests.OwnerAgentAssignmentRequest;
import nitchcorp.backend.titan.immo.application.dto.responses.OwnerAgentAssignmentResponse;
import nitchcorp.backend.titan.immo.domain.model.OwnerAgentAssignment;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import nitchcorp.backend.titan.immo.domain.model.Property;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OwnerAgentAssignmentMapper {

    public OwnerAgentAssignmentResponse toResponse(OwnerAgentAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("OwnerAgentAssignment cannot be null");
        }

        return new OwnerAgentAssignmentResponse(
                assignment.getTrackingId(),
                assignment.getId(),
                (assignment.getOwner() != null) ? assignment.getOwner().getTrackingId() : null,
                (assignment.getAgent() != null) ? assignment.getAgent().getTrackingId() : null,
                (assignment.getProperty() != null) ? assignment.getProperty().getId() : null,
                assignment.getAssignedAt(),
                assignment.getInstructions(),
                assignment.getIsActive(),
                assignment.getDeactivationReason(),
                assignment.getTransferReason()
        );
    }

    public OwnerAgentAssignment toEntity(OwnerAgentAssignmentRequest request, User owner, User agent, Property property) {
        if (request == null) {
            throw new IllegalArgumentException("OwnerAgentAssignmentRequest cannot be null");
        }

        OwnerAgentAssignment assignment = new OwnerAgentAssignment();
        assignment.setTrackingId(UUID.randomUUID());
        assignment.setOwner(owner);
        assignment.setAgent(agent);
        assignment.setProperty(property);
        assignment.setAssignedAt(request.assignedAt() != null ? request.assignedAt() : LocalDateTime.now());
        assignment.setInstructions(request.instructions());
        assignment.setIsActive(true); // New assignments are active by default

        return assignment;
    }

    public static OwnerAgentAssignment toEntityFromResponse(OwnerAgentAssignmentResponse response, User owner, User agent, Property property) {
        if (response == null) {
            throw new IllegalArgumentException("OwnerAgentAssignmentResponse cannot be null");
        }

        OwnerAgentAssignment assignment = new OwnerAgentAssignment();
        assignment.setTrackingId(response.trackingId());
        assignment.setId(response.id());
        assignment.setOwner(owner);
        assignment.setAgent(agent);
        assignment.setProperty(property);
        assignment.setAssignedAt(response.assignedAt());
        assignment.setInstructions(response.instructions());

        return assignment;
    }
}