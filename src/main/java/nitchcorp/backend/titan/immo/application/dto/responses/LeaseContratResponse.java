package nitchcorp.backend.titan.immo.application.dto.responses;

import nitchcorp.backend.titan.immo.domain.enums.LeaseStatus;
import org.springframework.modulith.NamedInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@NamedInterface
public record LeaseContratResponse(
        UUID trackingId,
        Long id,
        Long propertyId,
        Long customerId,
        Long agentId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal rentAmount,
        BigDecimal depositAmount,
        String contractDocument,
        LeaseStatus status
) {}