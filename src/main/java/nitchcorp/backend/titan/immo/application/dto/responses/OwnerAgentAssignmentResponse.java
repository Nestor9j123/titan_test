package nitchcorp.backend.titan.immo.application.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record OwnerAgentAssignmentResponse(
        UUID trackingId,
        Long id,
        UUID ownerId,
        UUID agentId,
        Long propertyId,
        LocalDateTime assignedAt,
        String instructions,
        Boolean isActive,
        String deactivationReason,
        String transferReason
) {}