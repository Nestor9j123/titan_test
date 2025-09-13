package nitchcorp.backend.titan.immo.application.dto.responses;

import nitchcorp.backend.titan.immo.domain.enums.VisitStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitResponse(
        UUID trackingId,
        Long id,
        Long propertyId,
        Long agentId,
        Long customerId,
        LocalDateTime visitDate,
        VisitStatus status,
        String comments
) {}