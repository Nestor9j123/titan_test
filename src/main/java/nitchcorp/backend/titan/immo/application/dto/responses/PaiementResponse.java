package nitchcorp.backend.titan.immo.application.dto.responses;

import nitchcorp.backend.titan.immo.domain.enums.PaiementStatus;
import nitchcorp.backend.titan.immo.domain.enums.PaiementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaiementResponse(
        UUID trackingId,
        Long id,
        UUID leaseContractTrackingId,
        UUID customerTrackingId,
        BigDecimal amount,
        PaiementType paymentType,
        LocalDateTime paymentDate,
        LocalDate dueDate,
        PaiementStatus status,
        String transactionId
) {}